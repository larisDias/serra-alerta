(() => {
    'use strict';

    const TIPOS = {
        controlada: { rotulo: 'Queimada controlada', cor: '#E8A33D' },
        irregular: { rotulo: 'Queimada irregular', cor: '#E4572E' },
        incendio: { rotulo: 'Incêndio florestal', cor: '#A61E1E' },
    };
    const SINAIS = { fumaca: 'Fumaça', fogo: 'Fogo' };

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

    const mapa = L.map(el, { scrollWheelZoom: false, zoomControl: true }).setView([-21.965, -46.80], 12);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(mapa);
    mapa.on('click', () => mapa.scrollWheelZoom.enable());

    // Destaque aproximado da Serra da Paulista (área prioritária).
    L.circle([-21.928, -46.735], {
        radius: 2600, color: '#2F6B4F', weight: 1.5, dashArray: '6 6', fillColor: '#2F6B4F', fillOpacity: 0.08,
    }).addTo(mapa).bindTooltip('Serra da Paulista · área prioritária', { direction: 'top' });

    const camada = L.layerGroup().addTo(mapa);
    const lista = document.getElementById('listaRelatos');
    let relatos = [];
    let filtro = '';
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

    const icone = (r) => L.divIcon({
        className: '',
        html: `<div class="marcador ${Date.now() - new Date(r.created_at) < 3600000 ? 'marcador--recente' : ''}" style="--cor:${TIPOS[r.tipo].cor}"><i></i><b></b></div>`,
        iconSize: [26, 26],
        iconAnchor: [13, 13],
        popupAnchor: [0, -12],
    });

    const popup = (r) => `
        <div class="popup">
            <span class="popup__tipo"><i class="ponto" style="background:${TIPOS[r.tipo].cor}"></i>${TIPOS[r.tipo].rotulo}</span>
            <div style="margin-top:6px">${esc(r.descricao) || '<em>Sem descrição</em>'}</div>
            ${r.foto_url ? `<img class="popup__img" src="${esc(r.foto_url)}" alt="Foto do relato">` : ''}
            <small>${SINAIS[r.sinal]} · ${tempo(r.created_at)} · ${r.latitude.toFixed(4)}, ${r.longitude.toFixed(4)}</small>
        </div>`;

    const desenhar = () => {
        camada.clearLayers();
        marcadores.clear();
        const visiveis = filtro ? relatos.filter(r => r.tipo === filtro) : relatos;

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
                <div class="item__topo"><i class="ponto" style="background:${TIPOS[r.tipo].cor}"></i>
                    <span class="item__tipo">${TIPOS[r.tipo].rotulo}</span><span>· ${tempo(r.created_at)}</span></div>
                <div class="item__desc">${esc(r.descricao) || SINAIS[r.sinal]}</div>`;
            li.addEventListener('click', () => {
                mapa.flyTo([r.latitude, r.longitude], 14, { duration: 0.8 });
                marcadores.get(r.id)?.openPopup();
            });
            lista.appendChild(li);
        });

        document.querySelectorAll('[data-cont]').forEach(b => {
            const t = b.dataset.cont;
            b.textContent = t ? relatos.filter(r => r.tipo === t).length : relatos.length;
        });
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

    document.querySelectorAll('#filtrosMapa .filtro').forEach(b => b.addEventListener('click', () => {
        document.querySelectorAll('#filtrosMapa .filtro').forEach(x => x.classList.toggle('ativo', x === b));
        filtro = b.dataset.tipo;
        desenhar();
    }));

    carregar();
    setInterval(carregar, 20000);
    // A demonstração embutida avisa quando um relato é enviado.
    window.addEventListener('message', (e) => {
        if (e.origin === location.origin && e.data === 'serra-alerta:novo-relato') carregar();
    });
})();
