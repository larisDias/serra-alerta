/*
 * Modo estático (GitHub Pages): sem servidor PHP, a API do site é simulada no navegador.
 *   GET    /api/relatos      → relatos de exemplo (api/relatos.json, gerado no build) + os enviados neste navegador
 *   GET    /api/focos        → focos do INPE lidos ao vivo do WFS público do INPE (CORS liberado), filtrados por ?dias=
 *   POST   /api/relatos      → guarda o relato (com as fotos reduzidas) no localStorage deste navegador
 *   DELETE /api/relatos/{id} → remove-o de lá
 * Precisa ser carregado antes de landing.js / app-demo.js, que continuam chamando fetch() normalmente.
 */
(() => {
    const raiz = new URL('../', document.currentScript.src).href;
    const CHAVE = 'serra-alerta:estatico:relatos';
    const MAX_FOTOS = 3;
    const original = window.fetch.bind(window);

    const resposta = (corpo, status = 200) => new Response(
        corpo === null ? null : JSON.stringify(corpo),
        { status, headers: { 'Content-Type': 'application/json' } },
    );

    const lerLocais = () => {
        try { return JSON.parse(localStorage.getItem(CHAVE)) || []; } catch { return []; }
    };

    // Fotos de celular passam de 5 MB; o localStorage guarda ~5 MB no total.
    const reduzir = (arquivo) => new Promise((ok, falha) => {
        const url = URL.createObjectURL(arquivo);
        const img = new Image();
        img.onload = () => {
            const escala = Math.min(1, 900 / Math.max(img.width, img.height));
            const tela = document.createElement('canvas');
            tela.width = Math.round(img.width * escala);
            tela.height = Math.round(img.height * escala);
            tela.getContext('2d').drawImage(img, 0, 0, tela.width, tela.height);
            URL.revokeObjectURL(url);
            ok(tela.toDataURL('image/jpeg', 0.72));
        };
        img.onerror = () => { URL.revokeObjectURL(url); falha(new Error('Não foi possível ler a foto.')); };
        img.src = url;
    });

    const listar = async () => {
        const base = await (await original(`${raiz}api/relatos.json`)).json();
        const data = [...lerLocais(), ...base.data];
        return resposta({ data, total: data.length });
    };

    /*
     * Focos de calor, em duas camadas:
     *  1. snapshot (api/focos.json): gerado de hora em hora pelo GitHub Actions e servido pelo próprio Pages;
     *     responde na hora e é o que o mapa desenha primeiro (ou o único, se o INPE estiver fora).
     *  2. ao vivo: uma única consulta ao WFS do INPE (CORS liberado) traz os últimos 30 dias só da região da Serra
     *     (poucos KB). O resultado fica 15 min no localStorage, compartilhado entre abas, e ao chegar avisa a
     *     página (evento `serra-alerta:focos`) para redesenhar. ?dias= filtra localmente.
     * Cada visitante faz no máximo 4 consultas por hora ao INPE; após uma falha espera 60 s antes de tentar de novo.
     */
    const WFS = 'https://terrabrasilis.dpi.inpe.br/queimadas/geoserver/wfs';
    const CHAVE_FOCOS = 'serra-alerta:estatico:focos';
    const EVENTO_FOCOS = 'serra-alerta:focos';
    const VALIDADE_FOCOS = 15 * 60 * 1000;
    const PAUSA_APOS_FALHA = 60 * 1000;
    const REGIAO = { minLon: -47.4, minLat: -22.2, maxLon: -46.7, maxLat: -21.5 }; // mesmo recorte do app
    let consulta = null;
    let snapshot = null;
    let falhouEm = 0;

    // "ESPÍRITO SANTO DO PINHAL" → "Espírito Santo do Pinhal"
    const nomeProprio = (s = '') => s.toLowerCase()
        .replace(/(^|\s)(\S)/g, (_, a, b) => a + b.toUpperCase())
        .replace(/\s(Da|Das|De|Do|Dos|E)(?=\s)/g, (m) => m.toLowerCase());

    const lerFocos = () => {
        try { return JSON.parse(localStorage.getItem(CHAVE_FOCOS)); } catch { return null; }
    };

    const consultarInpe = async () => {
        const desde = new Date(Date.now() - 30 * 864e5).toISOString().slice(0, 10) + 'T00:00:00Z';
        const { minLon, minLat, maxLon, maxLat } = REGIAO;
        const params = new URLSearchParams({
            service: 'WFS',
            version: '1.0.0',
            request: 'GetFeature',
            typeName: 'bdqueimadas2:focos',
            outputFormat: 'application/json',
            propertyName: 'id_foco_bdq,latitude,longitude,data_hora_gmt,satelite,municipio,frp',
            CQL_FILTER: `BBOX(geometria,${minLon},${minLat},${maxLon},${maxLat}) AND data_hora_gmt >= '${desde}'`,
        });
        const r = await original(`${WFS}?${params}`);
        if (!r.ok) throw new Error(`INPE respondeu ${r.status}`);
        const { features } = await r.json();
        return features
            .map(({ properties: p }) => ({
                id: String(p.id_foco_bdq),
                latitude: p.latitude,
                longitude: p.longitude,
                data_deteccao: p.data_hora_gmt,
                frp: p.frp ?? null,
                satelite: p.satelite ?? '',
                municipio: nomeProprio(p.municipio),
            }))
            .sort((a, b) => b.data_deteccao.localeCompare(a.data_deteccao));
    };

    const lerSnapshot = () => snapshot ??= original(`${raiz}api/focos.json`)
        .then(r => (r.ok ? r.json() : null))
        .then(j => (j && j.disponivel ? { em: Date.parse(j.atualizado_em), data: j.data } : null))
        .catch(() => null);

    const consultarAoVivo = () => {
        if (consulta || Date.now() - falhouEm < PAUSA_APOS_FALHA) return consulta;
        consulta = consultarInpe()
            .then((data) => {
                const novo = { em: Date.now(), data };
                try { localStorage.setItem(CHAVE_FOCOS, JSON.stringify(novo)); } catch { /* sem espaço: segue sem cache */ }
                window.dispatchEvent(new Event(EVENTO_FOCOS));
                return novo;
            })
            .catch(() => { falhouEm = Date.now(); return null; })
            .finally(() => { consulta = null; });
        return consulta;
    };

    const focosBase = async () => {
        const guardado = lerFocos();
        if (guardado && Date.now() - guardado.em < VALIDADE_FOCOS) return guardado;
        const aoVivo = consultarAoVivo();
        // Responde já com o dado mais novo que houver (cache vencido ou snapshot); o ao vivo chega depois pelo evento.
        const antigo = [guardado, await lerSnapshot()].filter(Boolean).sort((a, b) => b.em - a.em)[0];
        return antigo || (await aoVivo);
    };

    const focos = async (url) => {
        const base = await focosBase();
        if (!base) return resposta({ data: [], total: 0, disponivel: false }, 503);
        const dias = Math.min(30, Math.max(1, Number(url.searchParams.get('dias')) || 7));
        const desde = Date.now() - dias * 864e5;
        const data = base.data.filter(f => new Date(f.data_deteccao).getTime() >= desde);
        return resposta({
            data,
            total: data.length,
            fonte: 'INPE/BDQueimadas',
            disponivel: true,
            atualizado_em: new Date(base.em).toISOString(),
        });
    };

    const criar = async (dados) => {
        const arquivos = [...dados.getAll('fotos[]'), ...dados.getAll('foto')].filter(f => f instanceof File && f.size);
        if (!arquivos.length) return resposta({ message: 'Tire ou escolha uma foto da ocorrência.' }, 422);
        try {
            const fotos = await Promise.all(arquivos.slice(0, MAX_FOTOS).map(reduzir));
            const agora = new Date().toISOString();
            const relato = {
                id: Date.now(),
                categoria: dados.get('categoria'),
                latitude: Number(dados.get('latitude')),
                longitude: Number(dados.get('longitude')),
                descricao: dados.get('descricao') || null,
                ajustada_manualmente: dados.get('ajustada_manualmente') === '1',
                foto: null,
                foto_url: fotos[0],
                fotos_urls: fotos,
                created_at: agora,
                updated_at: agora,
            };
            localStorage.setItem(CHAVE, JSON.stringify([relato, ...lerLocais()]));
            return resposta({ data: relato, token: 'estatico' }, 201);
        } catch (e) {
            const cheio = e && e.name === 'QuotaExceededError';
            return resposta({ message: cheio ? 'Sem espaço neste navegador para guardar as fotos. Exclua relatos antigos.' : e.message }, 422);
        }
    };

    const excluir = (id) => {
        const locais = lerLocais();
        if (!locais.some(r => r.id === id)) return resposta({ message: 'Relato não encontrado.' }, 404);
        localStorage.setItem(CHAVE, JSON.stringify(locais.filter(r => r.id !== id)));
        return resposta(null, 204);
    };

    window.fetch = (entrada, opcoes = {}) => {
        const url = new URL(typeof entrada === 'string' ? entrada : entrada.url, location.href);
        if (url.origin !== location.origin) return original(entrada, opcoes);

        const metodo = (opcoes.method || 'GET').toUpperCase();
        const caminho = url.pathname.replace(/\/+$/, '');
        const relato = caminho.match(/\/api\/relatos(?:\/(\d+))?$/);

        if (caminho.endsWith('/api/focos') && metodo === 'GET') return focos(url);
        if (relato && !relato[1] && metodo === 'GET') return listar();
        if (relato && !relato[1] && metodo === 'POST') return criar(opcoes.body);
        if (relato && relato[1] && metodo === 'DELETE') return Promise.resolve(excluir(Number(relato[1])));
        return original(entrada, opcoes);
    };
})();
