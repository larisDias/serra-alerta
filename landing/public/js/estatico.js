/*
 * Modo estático (GitHub Pages): sem servidor PHP, a API do site é simulada no navegador.
 *   GET    /api/relatos      → relatos de exemplo (api/relatos.json, gerado no build) + os enviados neste navegador
 *   GET    /api/focos        → focos do INPE coletados no build (api/focos.json), filtrados por ?dias=
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

    const focos = async (url) => {
        const r = await original(`${raiz}api/focos.json`);
        if (!r.ok) return resposta({ data: [], total: 0, disponivel: false }, 503);
        const base = await r.json();
        const dias = Math.min(30, Math.max(1, Number(url.searchParams.get('dias')) || 7));
        const desde = Date.now() - dias * 864e5;
        const data = base.data.filter(f => new Date(f.data_deteccao).getTime() >= desde);
        return resposta({ ...base, data, total: data.length });
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
