(() => {
    'use strict';

    // Mesmas categorias e cores do app Android (ui/theme/Color.kt).
    const CATEGORIAS = {
        queima_controlada: { rotulo: 'Queima controlada', cor: '#A16207' },
        queimada_irregular: { rotulo: 'Queimada irregular', cor: '#C2410C' },
        incendio_florestal: { rotulo: 'Incêndio florestal', cor: '#991B1B' },
        fumaca_nao_identificada: { rotulo: 'Fumaça não identificada', cor: '#57534E' },
    };
    const nivelFoco = (frp) => (frp == null || frp < 0 ? { rotulo: 'não informada', cor: '#78716C' }
        : frp < 10 ? { rotulo: 'baixa', cor: '#EAB308' }
            : frp < 50 ? { rotulo: 'moderada', cor: '#F97316' }
                : frp < 100 ? { rotulo: 'alta', cor: '#DC2626' }
                    : { rotulo: 'extrema', cor: '#7F1D1D' });

    /* ---------- Navegação ---------- */
    const nav = document.getElementById('nav');
    const links = document.getElementById('navLinks');
    const aoRolar = () => nav.classList.toggle('rolado', window.scrollY > 40);
    window.addEventListener('scroll', aoRolar, { passive: true });
    aoRolar();
    document.getElementById('navMenu').addEventListener('click', () => links.classList.toggle('aberto'));
    links.querySelectorAll('a').forEach(a => a.addEventListener('click', () => links.classList.remove('aberto')));

    /* ---------- Revelar ao rolar + contadores ---------- */
    const contar = (el) => {
        const alvo = parseFloat(el.dataset.contar);
        const casas = parseInt(el.dataset.decimais || '0', 10);
        const inicio = performance.now();
        const dur = 1400;
        const passo = (t) => {
            const p = Math.min((t - inicio) / dur, 1);
            const v = alvo * (1 - Math.pow(1 - p, 3));
            el.textContent = v.toLocaleString('pt-BR', { minimumFractionDigits: casas, maximumFractionDigits: casas });
            if (p < 1) requestAnimationFrame(passo);
        };
        requestAnimationFrame(passo);
    };

    const obs = new IntersectionObserver((entradas) => {
        entradas.forEach(e => {
            if (!e.isIntersecting) return;
            e.target.classList.add('revelado');
            e.target.querySelectorAll('[data-contar]').forEach(contar);
            obs.unobserve(e.target);
        });
    }, { threshold: 0.15 });
    document.querySelectorAll('.revelar').forEach((el, i) => {
        el.style.transitionDelay = `${(i % 4) * 70}ms`;
        obs.observe(el);
    });

    /* ---------- Tooltip dos gráficos ---------- */
    const tip = document.getElementById('tooltip');
    document.querySelectorAll('.barra').forEach(b => {
        const mostrar = () => {
            const r = b.querySelector('.barra__coluna').getBoundingClientRect();
            tip.textContent = b.dataset.tip;
            tip.style.left = `${r.left + r.width / 2}px`;
            tip.style.top = `${r.top}px`;
            tip.hidden = false;
        };
        const esconder = () => { tip.hidden = true; };
        b.addEventListener('mouseenter', mostrar);
        b.addEventListener('focus', mostrar);
        b.addEventListener('mouseleave', esconder);
        b.addEventListener('blur', esconder);
    });
    window.addEventListener('scroll', () => { tip.hidden = true; }, { passive: true });

    /* ---------- Mapa público ---------- */
    const el = document.getElementById('mapaPublico');
    if (!el || typeof L === 'undefined') return;

    const mapa = L.map(el, { scrollWheelZoom: false, zoomControl: true, zoomSnap: 0.5 }).setView([-22.0, -46.84], 11);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(mapa);
    mapa.on('click', () => mapa.scrollWheelZoom.enable());

    // Destaque aproximado da Serra da Paulista (área prioritária).
    L.circle([-21.928, -46.735], {
        radius: 2600, color: '#2F6B4F', weight: 1.5, dashArray: '6 6', fillColor: '#2F6B4F', fillOpacity: 0.08,
    }).addTo(mapa).bindTooltip('Serra da Paulista · área prioritária', { direction: 'top' });

    const camadaFocos = L.layerGroup().addTo(mapa);
    const camada = L.layerGroup().addTo(mapa);
    const lista = document.getElementById('listaRelatos');
    let relatos = [];
    let focos = [];
    let filtro = '';
    let mostrarFocos = true;
    let idsConhecidos = null;
    const marcadores = new Map();

    const tempo = (iso) => {
        const min = Math.round((Date.now() - new Date(iso).getTime()) / 60000);
        if (min < 1) return 'agora';
        if (min < 60) return `há ${min} min`;
        if (min < 1440) return `há ${Math.floor(min / 60)} h`;
        return `há ${Math.floor(min / 1440)} d`;
    };
    const esc = (s) => String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));

    // Chama dos marcadores do app; focos do INPE levam o selo azul-petróleo.
    const CHAMA = 'M12 2.5C12.9 5.8 17.6 8 17.6 13.6L17.6 14A5.6 5.6 0 0 1 6.4 14C6.4 11.2 7.8 9.3 9.3 8C9.5 9.9 10.4 11.1 11.6 11.7C11.1 8.4 11.1 5.3 12 2.5Z';
    const NUCLEO = 'M12 12.2C12.5 13.9 14.7 14.9 14.7 16.9A2.7 2.7 0 0 1 9.3 16.9C9.3 15.6 10 14.8 10.7 14.3C10.9 15 11.3 15.4 11.8 15.6C11.6 14.5 11.6 13.3 12 12.2Z';
    const chama = (cor, { selo = false, recente = false } = {}) => L.divIcon({
        className: '',
        html: `<svg class="chama ${recente ? 'chama--recente' : ''}" viewBox="0 0 24 24">
            <path d="${CHAMA}" fill="${cor}" stroke="#fff" stroke-width="2.4" stroke-linejoin="round" paint-order="stroke"/>
            <path d="${NUCLEO}" fill="#FFE7A3"/>
            ${selo ? '<circle cx="18.24" cy="17.76" r="3.3" fill="#fff"/><circle cx="18.24" cy="17.76" r="2.1" fill="#1F5F73"/>' : ''}</svg>`,
        iconSize: [34, 34],
        iconAnchor: [17, 34 * 19.6 / 24],
        popupAnchor: [0, -24],
    });
    const icone = (r) => chama(CATEGORIAS[r.categoria].cor, { recente: Date.now() - new Date(r.created_at) < 3600000 });

    const popup = (r) => `
        <div class="popup">
            <span class="popup__tipo"><i class="ponto" style="background:${CATEGORIAS[r.categoria].cor}"></i>${CATEGORIAS[r.categoria].rotulo}</span>
            <div style="margin-top:6px">${esc(r.descricao) || '<em>Sem descrição</em>'}</div>
            ${r.foto_url ? `<img class="popup__img" src="${esc(r.foto_url)}" alt="Foto do relato">` : ''}
            <small>${tempo(r.created_at)} · ${r.latitude.toFixed(4)}, ${r.longitude.toFixed(4)}</small>
        </div>`;

    const popupFoco = (f) => {
        const n = nivelFoco(f.frp);
        return `
        <div class="popup">
            <span class="popup__tipo popup__inpe"><i class="ponto" style="background:${n.cor}"></i>Foco de calor · INPE</span>
            <div style="margin-top:6px">Intensidade ${n.rotulo}${f.frp != null ? ` · FRP ${f.frp.toLocaleString('pt-BR')} MW` : ''}<br>${esc(f.municipio)} · satélite ${esc(f.satelite)}</div>
            <small>Detectado ${tempo(f.data_deteccao)} · ${f.latitude.toFixed(4)}, ${f.longitude.toFixed(4)}</small>
        </div>`;
    };

    const desenhar = () => {
        camada.clearLayers();
        marcadores.clear();
        camadaFocos.clearLayers();
        if (mostrarFocos) {
            focos.forEach(f => L.marker([f.latitude, f.longitude], { icon: chama(nivelFoco(f.frp).cor, { selo: true }) }).bindPopup(popupFoco(f)).addTo(camadaFocos));
        }
        const visiveis = filtro ? relatos.filter(r => r.categoria === filtro) : relatos;

        visiveis.forEach(r => {
            const m = L.marker([r.latitude, r.longitude], { icon: icone(r) }).bindPopup(popup(r));
            m.addTo(camada);
            marcadores.set(r.id, m);
        });

        lista.innerHTML = visiveis.length ? '' : '<li class="vazio">Nenhum relato para este filtro.</li>';
        visiveis.slice(0, 30).forEach(r => {
            const li = document.createElement('li');
            if (idsConhecidos && !idsConhecidos.has(r.id)) li.classList.add('novo');
            li.innerHTML = `
                <div class="item__topo"><i class="ponto" style="background:${CATEGORIAS[r.categoria].cor}"></i>
                    <span class="item__tipo">${CATEGORIAS[r.categoria].rotulo}</span><span>· ${tempo(r.created_at)}</span></div>
                <div class="item__desc">${esc(r.descricao) || 'Sem descrição'}</div>`;
            li.addEventListener('click', () => {
                mapa.flyTo([r.latitude, r.longitude], 14, { duration: 0.8 });
                marcadores.get(r.id)?.openPopup();
            });
            lista.appendChild(li);
        });

        document.querySelectorAll('[data-cont]').forEach(b => {
            const t = b.dataset.cont;
            b.textContent = t ? relatos.filter(r => r.categoria === t).length : relatos.length;
        });
        document.getElementById('contFocos').textContent = focos.length;
    };

    const carregar = async () => {
        try {
            const resp = await fetch(el.dataset.api, { headers: { Accept: 'application/json' } });
            const json = await resp.json();
            const anterior = idsConhecidos;
            relatos = json.data;
            desenhar();
            idsConhecidos = new Set(relatos.map(r => r.id));
            // Relato novo vindo da demonstração: leva o mapa até ele.
            const novo = anterior && relatos.find(r => !anterior.has(r.id));
            if (novo) {
                mapa.flyTo([novo.latitude, novo.longitude], 14, { duration: 1 });
                setTimeout(() => marcadores.get(novo.id)?.openPopup(), 1100);
            }
        } catch (e) {
            lista.innerHTML = '<li class="vazio">Não foi possível carregar os relatos.</li>';
        }
    };

    document.querySelectorAll('#filtrosMapa .filtro[data-categoria]').forEach(b => b.addEventListener('click', () => {
        document.querySelectorAll('#filtrosMapa .filtro[data-categoria]').forEach(x => x.classList.toggle('ativo', x === b));
        filtro = b.dataset.categoria;
        desenhar();
    }));
    const botaoInpe = document.getElementById('filtroInpe');
    botaoInpe.addEventListener('click', () => {
        mostrarFocos = !mostrarFocos;
        botaoInpe.classList.toggle('ativo', mostrarFocos);
        botaoInpe.setAttribute('aria-pressed', mostrarFocos);
        desenhar();
    });

    // Focos de calor oficiais (INPE/BDQueimadas) dos últimos 7 dias, via proxy do site.
    const status = document.getElementById('statusFocos');
    const carregarFocos = async () => {
        try {
            const resp = await fetch(`${el.dataset.focos}?dias=7`, { headers: { Accept: 'application/json' } });
            const json = await resp.json();
            if (!resp.ok || !json.disponivel) throw new Error();
            focos = json.data;
            status.classList.remove('falhou');
            status.querySelector('span').textContent = `${focos.length} ${focos.length === 1 ? 'foco de calor detectado' : 'focos de calor detectados'} por satélite na região nos últimos 7 dias · INPE/BDQueimadas`;
            desenhar();
        } catch (e) {
            status.classList.add('falhou');
            status.querySelector('span').textContent = 'Focos de calor do INPE indisponíveis no momento.';
        }
    };

    carregar();
    carregarFocos();
    // Versão estática: o dado ao vivo do INPE chega depois do snapshot.
    window.addEventListener('serra-alerta:focos', carregarFocos);
    setInterval(carregar, 20000);
    // A demonstração embutida avisa quando um relato é enviado.
    window.addEventListener('message', (e) => {
        if (e.origin === location.origin && e.data === 'serra-alerta:novo-relato') carregar();
    });
})();
