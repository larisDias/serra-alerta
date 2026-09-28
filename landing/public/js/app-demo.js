(() => {
    'use strict';

    const $ = (s, el = document) => el.querySelector(s);
    const $$ = (s, el = document) => [...el.querySelectorAll(s)];
    const IC = window.SERRA_ICONES;
    const DIA = 86400000;

    /* ---------- Domínio (espelha domain/model e ui/theme do app Android) ---------- */
    const CATEGORIAS = {
        queima_controlada: {
            rotulo: 'Queima controlada', cor: '#A16207', icone: 'tractor',
            resumo: 'Fogo planejado, autorizado e acompanhado',
            prevencao: 'Uso planejado do fogo, com autorização do órgão ambiental, área delimitada, aceiros e acompanhamento.',
        },
        queimada_irregular: {
            rotulo: 'Queimada irregular', cor: '#C2410C', icone: 'flame',
            resumo: 'Lixo ou pasto queimado sem autorização',
            prevencao: 'Fogo sem autorização, como queimar lixo ou limpar pasto por conta própria. É infração ambiental.',
        },
        incendio_florestal: {
            rotulo: 'Incêndio florestal', cor: '#991B1B', icone: 'trees',
            resumo: 'Fogo sem controle em mata ou vegetação',
            prevencao: 'Fogo sem controle que atinge mata e vegetação nativa. Qualquer uma das anteriores pode virar um incêndio.',
        },
        fumaca_nao_identificada: {
            rotulo: 'Fumaça não identificada', cor: '#57534E', icone: 'cloud',
            resumo: 'Vejo fumaça, mas não sei a origem',
        },
    };
    const NIVEIS = [
        { rotulo: 'Baixa', cor: '#EAB308', faixa: '< 10', ate: 10 },
        { rotulo: 'Moderada', cor: '#F97316', faixa: '10–50', ate: 50 },
        { rotulo: 'Alta', cor: '#DC2626', faixa: '50–100', ate: 100 },
        { rotulo: 'Extrema', cor: '#7F1D1D', faixa: '≥ 100', ate: Infinity },
    ];
    const NIVEL_DESCONHECIDO = { rotulo: 'Não informada', cor: '#78716C' };
    const nivelFoco = (frp) => (frp == null || frp < 0 ? NIVEL_DESCONHECIDO : NIVEIS.find(n => frp < n.ate));

    const CENTRO = [-21.9695, -46.7985];
    const COBERTURA = { lat: [-22.6, -21.4], lon: [-47.3, -46.3] };
    const naCobertura = (p) => p[0] >= COBERTURA.lat[0] && p[0] <= COBERTURA.lat[1] && p[1] >= COBERTURA.lon[0] && p[1] <= COBERTURA.lon[1];

    const app = $('#app');
    const API = app.dataset.api;
    const API_FOCOS = app.dataset.focos;
    const EMBED = app.dataset.embed === '1';
    const CSRF = $('meta[name="csrf-token"]').content;

    /* ---------- Utilidades ---------- */
    const esc = (s) => String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
    const ms = (iso) => new Date(iso).getTime();
    const tempo = (iso) => {
        const min = Math.round((Date.now() - ms(iso)) / 60000);
        if (min < 1) return 'agora';
        if (min < 60) return `há ${min} min`;
        if (min < 1440) return `há ${Math.floor(min / 60)} h`;
        const d = Math.floor(min / 1440);
        return `há ${d} ${d === 1 ? 'dia' : 'dias'}`;
    };
    const dois = (n) => String(n).padStart(2, '0');
    const data = (iso, comAno = true) => {
        const d = new Date(iso);
        return `${dois(d.getDate())}/${dois(d.getMonth() + 1)}${comAno ? '/' + d.getFullYear() : ''} · ${dois(d.getHours())}:${dois(d.getMinutes())}`;
    };
    const ehHoje = (iso) => new Date(iso).toDateString() === new Date().toDateString();
    const coords = (lat, lon) => `${lat.toFixed(4)}, ${lon.toFixed(4)}`;
    const distM = (a, b) => L.latLng(a).distanceTo(L.latLng(b));
    const glifo = (cat, tam) => `<i class="glifo" style="--c:${CATEGORIAS[cat].cor}${tam ? `;--s:${tam}px` : ''}">${IC[CATEGORIAS[cat].icone]}</i>`;
    const plural = (n, s, p) => `${n} ${n === 1 ? s : p}`;

    const armazenado = (chave, padrao) => {
        try { return JSON.parse(localStorage.getItem(chave)) ?? padrao; } catch { return padrao; }
    };
    const armazenar = (chave, valor) => { try { localStorage.setItem(chave, JSON.stringify(valor)); } catch { /* modo privado */ } };
    const meus = new Set(armazenado('serra-alerta:meus', []));

    let toastTimer;
    const toast = (msg) => {
        const t = $('#toast');
        t.textContent = msg;
        t.hidden = false;
        clearTimeout(toastTimer);
        toastTimer = setTimeout(() => { t.hidden = true; }, 3200);
    };

    /* ---------- Curvas de nível (TopoArt.kt) ---------- */
    const desenharTopo = (canvas, progresso = 1) => {
        const w = canvas.clientWidth;
        const h = canvas.clientHeight;
        if (!w || !h) return;
        const dpr = window.devicePixelRatio || 1;
        canvas.width = w * dpr;
        canvas.height = h * dpr;
        const ctx = canvas.getContext('2d');
        ctx.scale(dpr, dpr);
        const [fx, fy] = canvas.dataset.centro.split(',').map(Number);
        const niveis = +canvas.dataset.niveis;
        const cx = w * fx;
        const cy = h * fy;
        const maior = Math.max(w, h);
        ctx.strokeStyle = canvas.dataset.cor;
        for (let k = 0; k < niveis; k++) {
            const revelado = Math.min(Math.max(progresso * niveis - k, 0), 1);
            if (revelado <= 0) continue;
            const raio = maior * .06 + maior * .055 * k;
            ctx.beginPath();
            for (let i = 0; i <= 96; i++) {
                const t = (i / 96) * Math.PI * 2;
                const onda = 1 + .09 * Math.sin(3 * t + k * .35) + .05 * Math.sin(5 * t + 1.3 + k * .12) + .025 * Math.cos(9 * t + k);
                const x = cx + Math.cos(t) * raio * onda * 1.25;
                const y = cy + Math.sin(t) * raio * onda * .82;
                i ? ctx.lineTo(x, y) : ctx.moveTo(x, y);
            }
            ctx.closePath();
            ctx.globalAlpha = revelado * Math.max(.95 - k * .045, .12);
            ctx.lineWidth = k % 4 === 3 ? 2.2 : 1.1;
            ctx.stroke();
        }
    };
    const desenharTopos = (raiz = app) => $$('canvas.topo-art', raiz).forEach(c => desenharTopo(c));
    const animarTopo = (canvas, dur) => {
        const inicio = performance.now();
        const passo = (t) => {
            const p = Math.min((t - inicio) / dur, 1);
            desenharTopo(canvas, 1 - Math.pow(1 - p, 3));
            if (p < 1) requestAnimationFrame(passo);
        };
        requestAnimationFrame(passo);
    };
    window.addEventListener('resize', () => desenharTopos());

    /* ---------- Mapa ---------- */
    const mapa = L.map('mapa', { zoomControl: false, attributionControl: true, zoomSnap: 0.5 }).setView(CENTRO, 11.5);
    mapa.attributionControl.setPrefix(false);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '© OpenStreetMap' }).addTo(mapa);
    const camadaFocos = L.layerGroup().addTo(mapa);
    const camadaRelatos = L.layerGroup().addTo(mapa);
    let marcadorVoce = null;

    // Chama dos marcadores (SerraMapView.kt); a base da chama marca o ponto exato.
    const CHAMA = 'M12 2.5C12.9 5.8 17.6 8 17.6 13.6L17.6 14A5.6 5.6 0 0 1 6.4 14C6.4 11.2 7.8 9.3 9.3 8C9.5 9.9 10.4 11.1 11.6 11.7C11.1 8.4 11.1 5.3 12 2.5Z';
    const NUCLEO = 'M12 12.2C12.5 13.9 14.7 14.9 14.7 16.9A2.7 2.7 0 0 1 9.3 16.9C9.3 15.6 10 14.8 10.7 14.3C10.9 15 11.3 15.4 11.8 15.6C11.6 14.5 11.6 13.3 12 12.2Z';
    const chama = (cor, selo) => L.divIcon({
        className: '',
        html: `<svg class="chama" viewBox="0 0 24 24" style="width:40px;height:40px">
            <path d="${CHAMA}" transform="translate(0 .9)" fill="rgba(22,19,17,.25)"/>
            <path d="${CHAMA}" fill="${cor}" stroke="#fff" stroke-width="2.4" stroke-linejoin="round" paint-order="stroke"/>
            <path d="${NUCLEO}" fill="#FFE7A3"/>
            ${selo ? '<circle cx="18.24" cy="17.76" r="3.3" fill="#fff"/><circle cx="18.24" cy="17.76" r="2.1" fill="#1F5F73"/>' : ''}
        </svg>`,
        iconSize: [40, 40],
        iconAnchor: [20, 40 * 19.6 / 24],
    });

    $('#zoomMais').onclick = () => mapa.zoomIn();
    $('#zoomMenos').onclick = () => mapa.zoomOut();

    const obterGps = () => new Promise((ok) => {
        if (!navigator.geolocation) return ok({ erro: 'indisponivel' });
        navigator.geolocation.getCurrentPosition(
            (p) => ok({ pos: [p.coords.latitude, p.coords.longitude], precisao: p.coords.accuracy }),
            (e) => ok({ erro: e.code === 1 ? 'permissao' : 'falha' }),
            { enableHighAccuracy: true, timeout: 10000, maximumAge: 30000 },
        );
    });

    $('#btnLocalizar').onclick = async () => {
        const btn = $('#btnLocalizar');
        if (btn.classList.contains('carregando')) return;
        btn.classList.add('carregando');
        const r = await obterGps();
        btn.classList.remove('carregando');
        if (!r.pos) return toast('Não foi possível obter sua localização.');
        if (marcadorVoce) mapa.removeLayer(marcadorVoce);
        marcadorVoce = L.marker(r.pos, { icon: L.divIcon({ className: '', html: '<div class="voce"></div>', iconSize: [36, 36], iconAnchor: [18, 18] }), interactive: false }).addTo(mapa);
        mapa.flyTo(r.pos, 15, { duration: 0.6 });
        if (!naCobertura(r.pos)) toast('Você está fora da área do protótipo (São João da Boa Vista).');
    };

    /* ---------- Estado ---------- */
    let relatos = [];
    let focos = [];
    let estadoFocos = 'ocioso';
    let focosAtualizados = 0;
    let diasCarregados = 0;
    const filtroPadrao = () => ({ relatos: true, focos: true, categorias: new Set(Object.keys(CATEGORIAS)), periodo: 7, inicio: null, fim: null });
    let filtro = filtroPadrao();

    const noPeriodo = (f, iso) => {
        const t = ms(iso);
        if (f.periodo === 'datas') return t >= (f.inicio ?? 0) && t < (f.fim != null ? f.fim + DIA : Infinity);
        return t >= Date.now() - f.periodo * DIA;
    };
    const incluiRelato = (f, r) => f.relatos && f.categorias.has(r.categoria) && noPeriodo(f, r.created_at);
    const incluiFoco = (f, fc) => f.focos && noPeriodo(f, fc.data_deteccao);
    const diasNecessarios = (f) => {
        if (f.periodo !== 'datas') return f.periodo;
        return Math.min(30, Math.max(1, Math.ceil((Date.now() - (f.inicio ?? Date.now())) / DIA) + 1));
    };

    const rotuloPeriodo = (f) => {
        if (f.periodo === 1) return 'Últimas 24 h';
        if (f.periodo === 'datas') {
            const fmt = (t) => { const d = new Date(t); return `${dois(d.getDate())}/${dois(d.getMonth() + 1)}`; };
            return [...new Set([f.inicio, f.fim].filter(v => v != null).map(fmt))].join(' – ') || 'Datas';
        }
        return `Últimos ${f.periodo} dias`;
    };
    const rotuloCategorias = (cats) => {
        const total = Object.keys(CATEGORIAS).length;
        if (cats.size === total) return 'Todas as categorias';
        if (cats.size === 0) return 'Nenhuma categoria';
        if (cats.size === 1) return CATEGORIAS[[...cats][0]].rotulo;
        return `${cats.size} categorias`;
    };

    /* ---------- Desenho do mapa ---------- */
    const desenharMapa = () => {
        const visiveis = relatos.filter(r => incluiRelato(filtro, r));
        const focosVisiveis = focos.filter(f => incluiFoco(filtro, f));

        camadaFocos.clearLayers();
        focosVisiveis.forEach(f => L.marker([f.latitude, f.longitude], { icon: chama(nivelFoco(f.frp).cor, true), title: 'Foco oficial — INPE/BDQueimadas' })
            .on('click', () => abrirFoco(f)).addTo(camadaFocos));
        camadaRelatos.clearLayers();
        visiveis.forEach(r => L.marker([r.latitude, r.longitude], { icon: chama(CATEGORIAS[r.categoria].cor, false), title: `Relato: ${CATEGORIAS[r.categoria].rotulo}` })
            .on('click', () => abrirDetalhe(r)).addTo(camadaRelatos));

        $('#chipPeriodo span').textContent = rotuloPeriodo(filtro);
        $('#chipCategorias span').textContent = rotuloCategorias(filtro.categorias);
        $('#chipInpe').classList.toggle('ativo', filtro.focos);
        $('#statusFocos').hidden = !filtro.focos;
        $('#legenda').innerHTML = `<span class="l-relato">${IC.flame}</span>${plural(visiveis.length, 'relato', 'relatos')}`
            + (filtro.focos ? `<span class="l-foco">${IC.satellite}</span>${plural(focosVisiveis.length, 'foco', 'focos')} INPE` : '');
        desenharStatusFocos();
    };

    const desenharStatusFocos = () => {
        const el = $('#statusFocos');
        el.classList.toggle('carregando', estadoFocos === 'carregando');
        el.classList.toggle('falhou', estadoFocos === 'falhou');
        const min = Math.floor((Date.now() - focosAtualizados) / 60000);
        $('span', el).textContent =
            estadoFocos === 'carregando' ? 'Atualizando focos do INPE…'
                : estadoFocos === 'falhou' ? 'Focos INPE indisponíveis · toque para tentar'
                    : !focosAtualizados ? 'Focos INPE não carregados · toque para atualizar'
                        : min < 1 ? 'Focos INPE atualizados agora'
                            : `Focos INPE atualizados há ${min} min`;
    };

    /* ---------- Dados ---------- */
    const carregarRelatos = async () => {
        try {
            const r = await fetch(API, { headers: { Accept: 'application/json' } });
            relatos = (await r.json()).data;
            desenharMapa();
            if (topo() === 'relatos') desenharRelatos();
        } catch {
            toast('Sem conexão com o servidor de relatos.');
        }
    };

    const carregarFocos = async (dias = diasNecessarios(filtro)) => {
        if (estadoFocos === 'carregando') return;
        estadoFocos = 'carregando';
        desenharStatusFocos();
        try {
            const r = await fetch(`${API_FOCOS}?dias=${dias}`, { headers: { Accept: 'application/json' } });
            const json = await r.json();
            if (!r.ok || !json.disponivel) throw new Error();
            focos = json.data;
            diasCarregados = dias;
            focosAtualizados = Date.now();
            estadoFocos = 'ocioso';
        } catch {
            estadoFocos = 'falhou';
        }
        desenharMapa();
    };
    $('#statusFocos').onclick = () => carregarFocos();

    /* ---------- Navegação ---------- */
    const ABAS = ['mapa', 'relatos', 'prevencao', 'emergencia'];
    let pilha = ['mapa'];
    const topo = () => pilha[pilha.length - 1];

    const mostrar = () => {
        const atual = topo();
        $$('[id^="tela-"]').forEach(t => { t.hidden = t.id !== `tela-${atual}`; });
        $('#camadaMapa').hidden = atual !== 'mapa';
        $('#dock').hidden = !ABAS.includes(atual);
        $$('.dock__item').forEach(b => b.classList.toggle('ativo', b.dataset.aba === atual));
        if (atual === 'mapa' || atual === 'ajuste') setTimeout(() => mapa.invalidateSize(), 0);
        if (atual === 'relatos') desenharRelatos();
        requestAnimationFrame(() => desenharTopos($(`#tela-${atual}`) || app));
    };
    const ir = (tela) => { pilha.push(tela); mostrar(); };
    const trocar = (tela) => { pilha[pilha.length - 1] = tela; mostrar(); };
    const irAba = (aba) => { pilha = ['mapa']; if (aba !== 'mapa') pilha.push(aba); mostrar(); };
    const voltar = () => {
        if (topo() === 'ajuste') mapa.off('move', aoMoverAjuste);
        if (pilha.length > 1) pilha.pop();
        mostrar();
    };

    $$('.dock__item').forEach(b => b.onclick = () => irAba(b.dataset.aba));
    app.addEventListener('click', (e) => {
        if (e.target.closest('[data-voltar]')) voltar();
        else if (e.target.closest('[data-registrar]')) iniciarRegistro();
        else if (e.target.closest('[data-ir-mapa]')) irAba('mapa');
        else if (e.target.closest('[data-compartilhar]')) compartilhar();
        else if (e.target.closest('[data-ir]')) ir(e.target.closest('[data-ir]').dataset.ir);
    });

    /* ---------- Detalhe do relato ---------- */
    let relatoAberto = null;
    const abrirDetalhe = (r) => {
        relatoAberto = r;
        const c = CATEGORIAS[r.categoria];
        $('#detalheFoto').innerHTML = r.foto_url
            ? `<img src="${esc(r.foto_url)}" alt="Foto do relato">`
            : '<canvas class="topo-art" data-cor="#FF7A45" data-centro=".5,.45" data-niveis="12"></canvas><span class="sem-foto">Relato de exemplo, sem foto</span>';
        $('#detalheConteudo').innerHTML = `
            <div class="detalhe__meta">${glifo(r.categoria)}<span class="eyebrow">${data(r.created_at)} · ${tempo(r.created_at)}</span></div>
            <h2>${c.rotulo}</h2>
            ${meus.has(r.id) ? '<span class="pilula pilula--brasa">Enviado deste aparelho</span>' : '<span class="pilula pilula--seguro">Relato da comunidade</span>'}
            ${r.descricao ? `<p class="citacao">${esc(r.descricao)}</p>` : ''}
            <div class="cartao-base cartao-local">
                <span>${IC.pin}</span>
                <div><span class="eyebrow">Localização</span><span class="dado">${coords(r.latitude, r.longitude)}</span>
                <small>${r.ajustada_manualmente ? 'Posição ajustada manualmente' : 'GPS'}</small></div>
            </div>
            <p class="nota">${IC.info}<span>Alerta preliminar enviado por um cidadão. Não substitui a verificação dos órgãos competentes.</span></p>`;
        $('#tela-detalhe .detalhe__rolagem').scrollTop = 0;
        ir('detalhe');
    };

    const compartilhar = async () => {
        const r = relatoAberto;
        if (!r) return;
        const texto = [
            'Serra Alerta — relato de ocorrência',
            `Categoria: ${CATEGORIAS[r.categoria].rotulo}`,
            `Localização: ${coords(r.latitude, r.longitude)}`,
            `Mapa: https://www.openstreetmap.org/?mlat=${r.latitude}&mlon=${r.longitude}#map=16/${r.latitude}/${r.longitude}`,
            `Data: ${data(r.created_at).replace(' · ', ' ')}`,
            r.descricao ? `Descrição: ${r.descricao}` : null,
            '',
            'Alerta preliminar — confirme a situação com os órgãos competentes.',
        ].filter(l => l !== null).join('\n');
        try {
            if (navigator.share) await navigator.share({ title: 'Serra Alerta', text: texto });
            else { await navigator.clipboard.writeText(texto); toast('Texto do relato copiado.'); }
        } catch { /* compartilhamento cancelado */ }
    };

    /* ---------- Foco oficial ---------- */
    const escala = (atual) => NIVEIS.map(n => {
        const ativo = !atual || atual === n;
        return `<div class="${ativo ? '' : 'apagado'}" style="${atual === n ? `background:${n.cor}24` : ''}">
            <span style="color:${n.cor}">${IC.flame}</span><strong>${n.rotulo}</strong><small>${n.faixa}</small></div>`;
    }).join('');

    const abrirFoco = (f) => {
        const nivel = nivelFoco(f.frp);
        $('#focoConteudo').innerHTML = `
            <div class="cartao-inpe">
                <canvas class="topo-art" data-cor="rgba(124,196,214,.55)" data-centro=".85,.2" data-niveis="12"></canvas>
                <span class="eyebrow">${IC.satellite}Dado oficial · satélite</span>
                <h2>Foco de calor detectado</h2>
                <span class="pilula" style="background:${nivel.cor};color:#fff">${IC.flame}Intensidade ${nivel.rotulo.toLowerCase()}</span>
                <span class="eyebrow rotulo-frp">Potência radiativa do fogo (FRP)</span>
                <div class="frp"><b>${f.frp != null ? f.frp.toLocaleString('pt-BR', { minimumFractionDigits: 1, maximumFractionDigits: 1 }) : '—'}</b>${f.frp != null ? '<span>MW</span>' : ''}</div>
            </div>
            <div class="cartao-base">
                <div class="linha-dado"><span class="eyebrow">Detecção</span><span class="dado">${data(f.data_deteccao)}</span></div>
                <div class="linha-dado"><span class="eyebrow">Coordenadas</span><span class="dado">${coords(f.latitude, f.longitude)}</span></div>
                <div class="linha-dado"><span class="eyebrow">Município</span><span class="dado">${esc(f.municipio)}</span></div>
                <div class="linha-dado"><span class="eyebrow">Satélite</span><span class="dado">${esc(f.satelite)}</span></div>
                <div class="linha-dado"><span class="eyebrow">Fonte</span><span class="dado">INPE/BDQueimadas</span></div>
            </div>
            <div class="cartao-base escala-cartao"><span class="eyebrow" style="display:block;margin:0 4px 6px">Escala de intensidade · FRP em MW</span><div class="escala">${escala(nivel)}</div></div>
            <p class="nota">${IC.info}<span>Um foco de calor indica temperatura elevada na superfície e pode não ser um incêndio ativo. Este dado é somente leitura.</span></p>`;
        ir('foco');
    };

    /* ---------- Relatos ---------- */
    let segRelatos = 'todos';
    const grupoData = (iso) => {
        const d = new Date(iso);
        if (ehHoje(iso)) return 'Hoje';
        if (new Date(ms(iso) + DIA).toDateString() === new Date().toDateString()) return 'Ontem';
        const mes = d.toLocaleDateString('pt-BR', { month: 'long' });
        return (mes[0].toUpperCase() + mes.slice(1)) + (d.getFullYear() === new Date().getFullYear() ? '' : ` de ${d.getFullYear()}`);
    };
    function desenharRelatos() {
        const seus = relatos.filter(r => meus.has(r.id));
        $('#stTotal').textContent = relatos.length;
        $('#st24h').textContent = relatos.filter(r => Date.now() - ms(r.created_at) < DIA).length;
        $('#stMeus').textContent = seus.length;
        const lista = segRelatos === 'meus' ? seus : relatos;
        const el = $('#listaRelatos');
        if (!lista.length) {
            el.innerHTML = `<p class="vazio">${segRelatos === 'meus' ? 'Você ainda não enviou relatos por aqui. Toque na câmera para registrar.' : 'Nenhum relato ainda.'}</p>`;
            return;
        }
        let grupo = null;
        el.innerHTML = '';
        lista.forEach(r => {
            const g = grupoData(r.created_at);
            if (g !== grupo) { grupo = g; el.insertAdjacentHTML('beforeend', `<span class="eyebrow grupo-data">${g}</span>`); }
            const b = document.createElement('button');
            b.className = 'cartao-base item-relato';
            b.innerHTML = `
                <span class="item-relato__foto">${r.foto_url ? `<img class="miniatura" src="${esc(r.foto_url)}" alt="">` : `<span class="miniatura miniatura--vazia">${IC.camera}</span>`}${glifo(r.categoria)}</span>
                <span class="item-relato__txt"><strong>${CATEGORIAS[r.categoria].rotulo}</strong>
                <span class="eyebrow">${ehHoje(r.created_at) ? data(r.created_at).split(' · ')[1] : data(r.created_at, false)} · ${coords(r.latitude, r.longitude)}</span>
                ${meus.has(r.id) ? '<span class="pilula pilula--brasa">Enviado deste aparelho</span>' : ''}</span>
                ${IC.arrow}`;
            b.onclick = () => abrirDetalhe(r);
            el.appendChild(b);
        });
    }
    $$('#segRelatos button').forEach(b => b.onclick = () => {
        segRelatos = b.dataset.seg;
        $$('#segRelatos button').forEach(x => x.classList.toggle('ativo', x === b));
        desenharRelatos();
    });

    /* ---------- Prevenção / introdução ---------- */
    const listaCategorias = (chaves, campo) => chaves.map(k => `<div class="lista-cat__item">${glifo(k)}<div><strong>${CATEGORIAS[k].rotulo}</strong><small>${CATEGORIAS[k][campo]}</small></div></div>`).join('');
    $('#listaCatPrevencao').innerHTML = listaCategorias(['queima_controlada', 'queimada_irregular', 'incendio_florestal'], 'prevencao');
    $('#listaCatIntro').innerHTML = listaCategorias(Object.keys(CATEGORIAS), 'resumo');
    $('#btnResumoLei').onclick = () => {
        const ul = $('#resumoLei');
        ul.hidden = !ul.hidden;
        $('#btnResumoLei').textContent = ul.hidden ? 'Ler o resumo' : 'Ocultar resumo';
    };

    /* ---------- Filtros ---------- */
    let rascunhoFiltro = null;
    const folha = $('#folhaFiltros');
    const veu = $('#veu');
    const abrirFiltros = () => {
        rascunhoFiltro = { ...filtro, categorias: new Set(filtro.categorias) };
        desenharFiltros();
        veu.hidden = false;
        folha.classList.add('aberta');
        folha.setAttribute('aria-hidden', 'false');
    };
    const fecharFiltros = () => {
        folha.classList.remove('aberta');
        folha.setAttribute('aria-hidden', 'true');
        veu.hidden = true;
    };
    const desenharFiltros = () => {
        const f = rascunhoFiltro;
        $('#swRelatos').checked = f.relatos;
        $('#swFocos').checked = f.focos;
        $$('#segPeriodo button').forEach(b => b.classList.toggle('ativo', String(f.periodo) === b.dataset.periodo));
        $('#datas').hidden = f.periodo !== 'datas';
        $('#tilesCategorias').innerHTML = Object.entries(CATEGORIAS).map(([k, c]) => {
            const total = relatos.filter(r => r.categoria === k && noPeriodo(f, r.created_at)).length;
            return `<button class="tile-cat" role="checkbox" aria-checked="${f.categorias.has(k)}" data-cat="${k}">
                <div>${glifo(k)}<span class="dado">${total}</span></div><strong>${c.rotulo}</strong></button>`;
        }).join('');
        const qtd = relatos.filter(r => incluiRelato(f, r)).length;
        $('#aplicarFiltros').textContent = f.relatos ? `Mostrar ${plural(qtd, 'relato', 'relatos')}` : 'Mostrar só focos do INPE';
    };
    // No desktop, a roda do mouse também rola a fileira de chips.
    $('.chips').addEventListener('wheel', (e) => { if (e.deltaY) { e.currentTarget.scrollLeft += e.deltaY; e.preventDefault(); } }, { passive: false });
    $('#chipPeriodo').onclick = abrirFiltros;
    $('#chipCategorias').onclick = abrirFiltros;
    $('#chipInpe').onclick = () => {
        filtro.focos = !filtro.focos;
        desenharMapa();
        if (filtro.focos && diasCarregados < diasNecessarios(filtro)) carregarFocos();
    };
    veu.onclick = fecharFiltros;
    $('#limparFiltros').onclick = () => { rascunhoFiltro = filtroPadrao(); $('#dataInicio').value = ''; $('#dataFim').value = ''; desenharFiltros(); };
    $('#swRelatos').onchange = (e) => { rascunhoFiltro.relatos = e.target.checked; desenharFiltros(); };
    $('#swFocos').onchange = (e) => { rascunhoFiltro.focos = e.target.checked; desenharFiltros(); };
    $('#escalaFiltros').innerHTML = escala(null);
    $$('#segPeriodo button').forEach(b => b.onclick = () => {
        const p = b.dataset.periodo;
        rascunhoFiltro.periodo = p === 'datas' ? 'datas' : +p;
        if (p === 'datas' && !$('#dataInicio').value) {
            const hoje = new Date();
            const iso = (d) => `${d.getFullYear()}-${dois(d.getMonth() + 1)}-${dois(d.getDate())}`;
            $('#dataFim').value = iso(hoje);
            $('#dataInicio').value = iso(new Date(hoje.getTime() - 2 * DIA));
            $('#dataFim').max = $('#dataInicio').max = iso(hoje);
        }
        if (p === 'datas') lerDatas();
        desenharFiltros();
    });
    // Meia-noite local do primeiro e do último dia escolhidos, como no FiltroMapa.kt.
    const lerDatas = () => {
        const local = (v) => { if (!v) return null; const [a, m, d] = v.split('-').map(Number); return new Date(a, m - 1, d).getTime(); };
        let ini = local($('#dataInicio').value);
        let fim = local($('#dataFim').value) ?? ini;
        if (ini != null && fim != null && fim < ini) [ini, fim] = [fim, ini];
        rascunhoFiltro.inicio = ini;
        rascunhoFiltro.fim = fim;
    };
    $('#dataInicio').onchange = $('#dataFim').onchange = () => { lerDatas(); desenharFiltros(); };
    $('#tilesCategorias').onclick = (e) => {
        const t = e.target.closest('[data-cat]');
        if (!t) return;
        const k = t.dataset.cat;
        rascunhoFiltro.categorias.has(k) ? rascunhoFiltro.categorias.delete(k) : rascunhoFiltro.categorias.add(k);
        desenharFiltros();
    };
    $('#aplicarFiltros').onclick = () => {
        filtro = rascunhoFiltro;
        fecharFiltros();
        desenharMapa();
        if (filtro.focos && diasCarregados < diasNecessarios(filtro)) carregarFocos();
    };

    /* ---------- Registro ---------- */
    const rascunho = { foto: null, categoria: null, gps: null, precisao: null, ajustada: null, localizando: false, erro: null };
    const posicao = () => rascunho.ajustada || rascunho.gps;

    $('#opcoesCategoria').innerHTML = Object.entries(CATEGORIAS).map(([k, c]) => `
        <button class="opcao-cat" role="radio" aria-checked="false" data-cat="${k}">
            <span class="opcao-cat__topo">${glifo(k)}<span class="opcao-cat__marca">${IC.check}</span></span>
            <strong>${c.rotulo}</strong><small>${c.resumo}</small>
        </button>`).join('');
    $('#opcoesCategoria').onclick = (e) => {
        const b = e.target.closest('[data-cat]');
        if (!b) return;
        rascunho.categoria = b.dataset.cat;
        atualizarRegistro();
    };

    const atualizarRegistro = () => {
        $$('#opcoesCategoria .opcao-cat').forEach(b => b.setAttribute('aria-checked', b.dataset.cat === rascunho.categoria));

        // Foto
        const temFoto = !!rascunho.foto;
        $('#fotoPreview').hidden = !temFoto;
        $('#fotoAdd').hidden = temFoto;
        $('#refazerFoto').hidden = !temFoto;
        $('#contaFotos').textContent = `${temFoto ? 1 : 0}/1`;

        // Localização (LocationSection do RegistrationScreen.kt)
        const pos = posicao();
        const status = rascunho.ajustada ? 'Ajustada manualmente' + (rascunho.gps ? ` · ${Math.round(distM(rascunho.gps, rascunho.ajustada))} m do GPS` : '')
            : rascunho.gps ? 'Obtida automaticamente' + (rascunho.precisao ? ` · ±${Math.round(rascunho.precisao)} m` : '')
                : rascunho.localizando ? 'Obtendo localização…'
                    : rascunho.erro === 'permissao' ? 'Sem permissão de localização'
                        : rascunho.erro === 'fora' ? 'Você está fora da área do protótipo'
                            : 'Não foi possível obter a localização';
        $('#localStatus').textContent = status;
        $('#localCoords').textContent = pos ? coords(pos[0], pos[1]) : (rascunho.erro === 'fora' ? 'Ajuste no mapa para marcar o foco' : '');
        const icone = $('#localIcone');
        icone.classList.toggle('ok', !!pos);
        icone.classList.toggle('carregando', rascunho.localizando && !pos);
        icone.innerHTML = pos || rascunho.localizando ? IC.pin : IC.pinoff;
        $('#tentarGps').hidden = !!pos || rascunho.localizando;

        // Descrição e envio
        $('#contaDesc').textContent = `${$('#descricao').value.length}/280`;
        $('#contaDesc').classList.toggle('brasa', $('#descricao').value.length >= 260);
        const faltando = [!temFoto && 'foto', !pos && 'localização', !rascunho.categoria && 'o que você está vendo'].filter(Boolean);
        const enviar = $('#enviarRelato');
        if (!enviar.classList.contains('enviando')) enviar.disabled = faltando.length > 0;
        $('#faltando').innerHTML = faltando.length
            ? `${IC.info}<span>Falta: ${faltando.join(', ')}</span>`
            : `${IC.check}<span>Vai direto para o mapa colaborativo</span>`;
    };

    const localizar = async () => {
        rascunho.localizando = true;
        rascunho.erro = null;
        atualizarRegistro();
        const r = await obterGps();
        rascunho.localizando = false;
        if (r.pos && naCobertura(r.pos)) { rascunho.gps = r.pos; rascunho.precisao = r.precisao; }
        else rascunho.erro = r.pos ? 'fora' : r.erro;
        atualizarRegistro();
    };

    const iniciarRegistro = () => {
        Object.assign(rascunho, { foto: null, categoria: null, gps: null, precisao: null, ajustada: null, localizando: false, erro: null });
        $('#descricao').value = '';
        $('#inputFoto').value = '';
        $('.rolagem--form').scrollTop = 0;
        pilha = [...pilha.filter(t => ABAS.includes(t)), 'registro'];
        mostrar();
        atualizarRegistro();
        localizar();
        // Passo 1 do app é a câmera: abre o seletor de foto já no toque.
        $('#inputFoto').click();
    };

    $('#fotoAdd').onclick = () => $('#inputFoto').click();
    $('#refazerFoto').onclick = () => $('#inputFoto').click();
    $('#removerFoto').onclick = () => { rascunho.foto = null; $('#inputFoto').value = ''; atualizarRegistro(); };
    $('#inputFoto').onchange = (e) => {
        const f = e.target.files[0];
        if (!f) return;
        rascunho.foto = f;
        $('#fotoPreview img').src = URL.createObjectURL(f);
        atualizarRegistro();
    };
    $('#tentarGps').onclick = localizar;
    $('#descricao').oninput = atualizarRegistro;

    /* Ajustar localização */
    const aoMoverAjuste = () => {
        const c = mapa.getCenter();
        const centro = [c.lat, c.lng];
        $('#ajusteCoords').textContent = coords(c.lat, c.lng);
        const eb = $('#ajusteDist');
        const d = rascunho.gps ? distM(rascunho.gps, centro) : null;
        eb.textContent = d == null ? 'Sem GPS · posição escolhida no mapa' : d < 5 ? 'Na posição do GPS' : `Ajustada · ${Math.round(d)} m do GPS`;
        eb.classList.toggle('ajustada', d != null && d >= 5);
    };
    $('#ajustarLocal').onclick = () => {
        mapa.setView(posicao() || CENTRO, Math.max(mapa.getZoom(), 15), { animate: false });
        ir('ajuste');
        aoMoverAjuste();
        mapa.on('move', aoMoverAjuste);
    };
    $('#confirmarAjuste').onclick = () => {
        const c = mapa.getCenter();
        if (!naCobertura([c.lat, c.lng])) return toast('Posicione o pino dentro da região de São João da Boa Vista.');
        rascunho.ajustada = [c.lat, c.lng];
        voltar();
        atualizarRegistro();
    };

    $('#enviarRelato').onclick = async () => {
        const pos = posicao();
        if (!rascunho.foto || !pos || !rascunho.categoria) return;
        const btn = $('#enviarRelato');
        const dados = new FormData();
        dados.append('categoria', rascunho.categoria);
        dados.append('latitude', pos[0].toFixed(6));
        dados.append('longitude', pos[1].toFixed(6));
        dados.append('descricao', $('#descricao').value.trim());
        dados.append('ajustada_manualmente', rascunho.ajustada ? '1' : '0');
        dados.append('foto', rascunho.foto);

        btn.disabled = true;
        btn.classList.add('enviando');
        $('span', btn).textContent = 'Enviando…';
        try {
            const r = await fetch(API, { method: 'POST', headers: { 'X-CSRF-TOKEN': CSRF, Accept: 'application/json' }, body: dados });
            if (!r.ok) {
                const erro = await r.json().catch(() => ({}));
                throw new Error(erro.message || 'falha ao enviar');
            }
            const { data: novo } = await r.json();
            meus.add(novo.id);
            armazenar('serra-alerta:meus', [...meus]);
            relatos.unshift(novo);
            if (!filtro.categorias.has(novo.categoria) || !filtro.relatos) { filtro = filtroPadrao(); }
            desenharMapa();
            mapa.setView([novo.latitude, novo.longitude], 15, { animate: false });
            relatoAberto = novo;
            $('#resumoConfirmacao').innerHTML = `
                <img class="miniatura" src="${esc(novo.foto_url)}" alt="">
                <div><strong>${CATEGORIAS[novo.categoria].rotulo}</strong><span class="dado">Hoje · ${data(novo.created_at).split(' · ')[1]}</span></div>
                ${glifo(novo.categoria, 32)}`;
            trocar('confirmacao');
            animarTopo($('#tela-confirmacao canvas'), 900);
            if (window.parent !== window) window.parent.postMessage('serra-alerta:novo-relato', location.origin);
        } catch (e) {
            toast(`Não foi possível enviar: ${e.message}`);
        } finally {
            btn.classList.remove('enviando');
            $('span', btn).textContent = 'Registrar ocorrência';
            atualizarRegistro();
        }
    };

    /* ---------- Splash e introdução (só na demo avulsa) ---------- */
    let paginaIntro = 0;
    const desenharIntro = () => {
        $$('.intro__pagina').forEach(p => { p.hidden = +p.dataset.pagina !== paginaIntro; });
        $('#introPasso').textContent = `0${paginaIntro + 1} / 02`;
        $$('.indicador i').forEach((i, n) => i.classList.toggle('ativo', n === paginaIntro));
        const ultima = paginaIntro === 1;
        $('#introAvancar span').textContent = ultima ? 'Começar' : 'Continuar';
        $('#introAvancar').classList.toggle('ultima', ultima);
        requestAnimationFrame(() => desenharTopos($('#intro')));
    };
    const abrirIntro = () => { paginaIntro = 0; $('#intro').hidden = false; desenharIntro(); };
    const fecharIntro = () => { $('#intro').hidden = true; armazenar('serra-alerta:intro', true); irAba('mapa'); };
    $('#introPular').onclick = fecharIntro;
    $('#introAvancar').onclick = () => { if (paginaIntro === 1) fecharIntro(); else { paginaIntro = 1; desenharIntro(); } };
    $('#reverIntro').onclick = abrirIntro;

    if (!EMBED) {
        const splash = $('#splash');
        splash.hidden = false;
        animarTopo($('canvas', splash), 1100);
        setTimeout(() => {
            if (!armazenado('serra-alerta:intro', false)) abrirIntro();
            splash.classList.add('saindo');
            setTimeout(() => { splash.hidden = true; }, 350);
        }, 1800);
    }

    document.addEventListener('keydown', (e) => {
        if (e.key !== 'Escape') return;
        if (folha.classList.contains('aberta')) fecharFiltros();
        else if (pilha.length > 1) voltar();
    });

    mostrar();
    desenharMapa();
    carregarRelatos();
    carregarFocos();
    setInterval(carregarRelatos, 20000);
    setInterval(desenharStatusFocos, 60000);
})();
