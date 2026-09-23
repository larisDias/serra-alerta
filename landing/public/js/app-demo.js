(() => {
    'use strict';

    const $ = (s) => document.querySelector(s);
    const $$ = (s) => document.querySelectorAll(s);
    const IC = window.SERRA_ICONES;

    const TIPOS = {
        controlada: { rotulo: 'Controlada', cor: '#E8A33D' },
        irregular: { rotulo: 'Irregular', cor: '#E4572E' },
        incendio: { rotulo: 'Incêndio', cor: '#A61E1E' },
    };
    const SINAIS = { fumaca: 'Fumaça', fogo: 'Fogo' };
    const CENTRO = [-21.9694, -46.7981];
    const SERRA = [-21.928, -46.735];
    const RAIO_COBERTURA_KM = 45;
    const REFERENCIAS = [
        ['Serra da Paulista', SERRA],
        ['Centro', CENTRO],
        ['Fazenda Cachoeira', [-21.945, -46.766]],
        ['Rodovia SP-342', [-21.956, -46.756]],
        ['Córrego da Aliança', [-21.988, -46.778]],
        ['Ribeirão dos Porcos', [-21.993, -46.870]],
        ['Córrego São Pedro', [-22.012, -46.845]],
        ['Ribeirão do Paraíso', [-21.930, -46.805]],
    ];

    const app = $('#app');
    const API = app.dataset.api;
    const CSRF = document.querySelector('meta[name="csrf-token"]').content;

    /* ---------- Utilidades ---------- */
    const esc = (s) => String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
    const tempo = (iso) => {
        const min = Math.round((Date.now() - new Date(iso).getTime()) / 60000);
        if (min < 1) return 'agora';
        if (min < 60) return `há ${min} min`;
        if (min < 1440) return `há ${Math.floor(min / 60)} h`;
        return `há ${Math.floor(min / 1440)} d`;
    };
    const distKm = (a, b) => L.latLng(a).distanceTo(L.latLng(b)) / 1000;
    const referencia = (p) => REFERENCIAS.reduce((m, r) => (distKm(p, r[1]) < distKm(p, m[1]) ? r : m))[0];
    const coords = (lat, lon) => `${lat.toFixed(5)}, ${lon.toFixed(5)}`;

    let toastTimer;
    const toast = (msg) => {
        const t = $('#toast');
        t.textContent = msg;
        t.hidden = false;
        clearTimeout(toastTimer);
        toastTimer = setTimeout(() => { t.hidden = true; }, 3200);
    };

    /* ---------- Mapa ---------- */
    const CAMADAS = {
        padrao: L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }),
        relevo: L.tileLayer('https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png', { maxZoom: 17, subdomains: 'abc' }),
        satelite: L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', { maxZoom: 18 }),
    };
    const mapa = L.map('mapa', { zoomControl: false, attributionControl: true }).setView(CENTRO, 13);
    let camadaAtual = CAMADAS.padrao.addTo(mapa);
    const grupo = L.layerGroup().addTo(mapa);
    let marcadorEu = null;

    $('#zoomMais').onclick = () => mapa.zoomIn();
    $('#zoomMenos').onclick = () => mapa.zoomOut();

    const menuCamadas = $('#menuCamadas');
    $('#btnCamadas').onclick = (e) => { e.stopPropagation(); menuCamadas.hidden = !menuCamadas.hidden; };
    document.addEventListener('click', () => { menuCamadas.hidden = true; });
    menuCamadas.querySelectorAll('button').forEach(b => b.onclick = () => {
        mapa.removeLayer(camadaAtual);
        camadaAtual = CAMADAS[b.dataset.camada].addTo(mapa);
        camadaAtual.bringToBack();
        menuCamadas.querySelectorAll('button').forEach(x => x.classList.toggle('ativo', x === b));
    });

    /* ---------- Relatos ---------- */
    let relatos = [];
    let filtro = '';

    const icone = (r) => L.divIcon({
        className: '',
        html: `<div class="mk ${Date.now() - new Date(r.created_at) < 3600000 ? 'recente' : ''}" style="--c:${TIPOS[r.tipo].cor}"><i></i><b></b></div>`,
        iconSize: [30, 30],
        iconAnchor: [15, 15],
    });

    const desenhar = () => {
        grupo.clearLayers();
        (filtro ? relatos.filter(r => r.tipo === filtro) : relatos).forEach(r => {
            L.marker([r.latitude, r.longitude], { icon: icone(r) }).on('click', () => abrirDetalhe(r)).addTo(grupo);
        });
        $('#totalRelatos').textContent = relatos.length;
        $$('[data-cont]').forEach(b => {
            const t = b.dataset.cont;
            b.textContent = t ? relatos.filter(r => r.tipo === t).length : relatos.length;
        });
        desenharAreas();
    };

    const carregar = async () => {
        try {
            const r = await fetch(API, { headers: { Accept: 'application/json' } });
            relatos = (await r.json()).data;
            desenhar();
        } catch (e) {
            toast('Sem conexão com o servidor de relatos.');
        }
    };

    $$('#chips .chip').forEach(c => c.onclick = () => {
        filtro = c.dataset.tipo === filtro ? '' : c.dataset.tipo;
        $$('#chips .chip').forEach(x => x.classList.toggle('ativo', x.dataset.tipo === filtro));
        desenhar();
    });

    /* ---------- Detalhe ---------- */
    const sheet = $('#sheet');
    const veu = $('#veu');
    const abrirDetalhe = (r) => {
        const t = TIPOS[r.tipo];
        $('#sheetConteudo').innerHTML = `
            <span class="det__tipo" style="--c:${t.cor}"><i class="ponto" style="background:${t.cor}"></i>${t.rotulo}</span>
            <span class="det__meta">${SINAIS[r.sinal]} · ${tempo(r.created_at)}</span>
            <h3>Próximo a ${esc(referencia([r.latitude, r.longitude]))}</h3>
            <p>${esc(r.descricao) || `${SINAIS[r.sinal]} avistada.`}</p>
            ${r.foto_url ? `<img class="det__foto" src="${esc(r.foto_url)}" alt="Foto do relato">` : ''}
            <div class="det__linha">${IC.pin}${coords(r.latitude, r.longitude)}</div>
            <div class="det__linha">${IC.clock}Registrado ${tempo(r.created_at)}</div>
            <div class="det__aviso">${IC.info}<span>Alerta preliminar enviado pela comunidade. Aguarda validação da Defesa Civil / Bombeiros.</span></div>
            <div class="det__botoes"><a href="tel:193">${IC.phone}Bombeiros 193</a><a href="tel:199">Defesa Civil 199</a></div>`;
        veu.hidden = false;
        sheet.classList.add('aberto');
        sheet.setAttribute('aria-hidden', 'false');
    };
    const fecharDetalhe = () => {
        sheet.classList.remove('aberto');
        sheet.setAttribute('aria-hidden', 'true');
        veu.hidden = true;
    };
    veu.onclick = fecharDetalhe;

    /* ---------- Localização ---------- */
    const obterGps = () => new Promise((ok) => {
        if (!navigator.geolocation) return ok(null);
        navigator.geolocation.getCurrentPosition(
            (p) => ok([p.coords.latitude, p.coords.longitude]),
            () => ok(null),
            { enableHighAccuracy: true, timeout: 8000, maximumAge: 30000 },
        );
    });

    $('#btnLocalizar').onclick = async () => {
        const p = await obterGps();
        if (!p) return toast('Não foi possível obter o GPS agora.');
        if (distKm(p, CENTRO) > RAIO_COBERTURA_KM) toast('Você está fora da área do protótipo (São João da Boa Vista).');
        if (marcadorEu) mapa.removeLayer(marcadorEu);
        marcadorEu = L.marker(p, { icon: L.divIcon({ className: '', html: '<div class="eu"></div>', iconSize: [22, 22], iconAnchor: [11, 11] }) }).addTo(mapa);
        mapa.flyTo(p, 15, { duration: 0.8 });
    };

    /* ---------- Marcar no mapa ---------- */
    const modoMarcar = (ativo) => {
        $('#btnMarcar').classList.toggle('ativo', ativo);
        $('#pinoCentral').hidden = !ativo;
        $('#painelMarcar').hidden = !ativo;
        $('#barra').hidden = ativo;
        $('#aviso').hidden = ativo;
        $('#chips').hidden = ativo;
    };
    $('#btnMarcar').onclick = () => modoMarcar($('#pinoCentral').hidden);
    $('#cancelarMarcar').onclick = () => modoMarcar(false);
    $('#confirmarMarcar').onclick = () => {
        const c = mapa.getCenter();
        modoMarcar(false);
        abrirReportar([c.lat, c.lng], 'Marcado no mapa');
    };

    /* ---------- Abas ---------- */
    const trocarAba = (aba) => {
        $('#areas').hidden = aba !== 'areas';
        $$('.barra__item').forEach(b => b.classList.toggle('ativo', b.dataset.aba === aba));
        if (aba === 'mapa') setTimeout(() => mapa.invalidateSize(), 0);
    };
    $$('.barra__item').forEach(b => b.onclick = () => trocarAba(b.dataset.aba));

    /* ---------- Áreas (núcleos) ---------- */
    const CELULA = 0.025;
    function desenharAreas() {
        const grupos = {};
        relatos.forEach(r => {
            const k = `${Math.floor(r.latitude / CELULA)}:${Math.floor(r.longitude / CELULA)}`;
            (grupos[k] ||= []).push(r);
        });
        const nucleos = Object.values(grupos).map(g => {
            const c = [g.reduce((s, r) => s + r.latitude, 0) / g.length, g.reduce((s, r) => s + r.longitude, 0) / g.length];
            const cont = {};
            g.forEach(r => { cont[r.tipo] = (cont[r.tipo] || 0) + 1; });
            const pred = Object.entries(cont).sort((a, b) => b[1] - a[1])[0][0];
            const recente = g.reduce((m, r) => (new Date(r.created_at) > new Date(m) ? r.created_at : m), g[0].created_at);
            return { nome: referencia(c), centro: c, qtd: g.length, pred, recente };
        }).sort((a, b) => b.qtd - a.qtd);
        const max = nucleos[0]?.qtd || 1;

        $('#resTotal').textContent = relatos.length;
        $('#res24h').textContent = relatos.filter(r => Date.now() - new Date(r.created_at) < 86400000).length;
        $('#resNucleos').textContent = nucleos.length;

        const lista = $('#listaNucleos');
        lista.innerHTML = '';
        nucleos.forEach(n => {
            const b = document.createElement('button');
            b.className = 'nucleo';
            b.innerHTML = `
                <div class="nucleo__topo"><div><strong>${esc(n.nome)}</strong>
                <small>${n.qtd} relato(s) · mais recente ${tempo(n.recente)}</small></div>${IC.arrow}</div>
                <div class="nucleo__barra" style="--c:${TIPOS[n.pred].cor}"><span style="width:${Math.max(8, n.qtd / max * 100)}%"></span></div>
                <small><i class="ponto" style="background:${TIPOS[n.pred].cor}"></i> Predomínio: ${TIPOS[n.pred].rotulo}</small>`;
            b.onclick = () => { trocarAba('mapa'); mapa.flyTo(n.centro, 14.5, { duration: 0.7 }); };
            lista.appendChild(b);
        });
    }
    $('#verSerra').onclick = () => { trocarAba('mapa'); mapa.flyTo(SERRA, 14, { duration: 0.7 }); };

    /* ---------- Reportar ---------- */
    const form = {
        sinal: 'fumaca',
        tipo: null,
        foto: null,
        local: null,
    };
    const btnEnviar = $('#enviarRelato');
    const atualizarEnviar = () => {
        btnEnviar.disabled = !form.tipo || !form.local;
        btnEnviar.textContent = form.tipo ? 'Enviar relato' : 'Escolha o tipo para enviar';
    };

    const definirLocal = (p, origem) => {
        form.local = p;
        $('#localTitulo').textContent = `Próximo a ${referencia(p)}`;
        $('#localCoords').textContent = coords(p[0], p[1]);
        $('#localOrigem').textContent = origem;
        atualizarEnviar();
    };

    const buscarLocal = async () => {
        const btn = $('#atualizarGps');
        btn.classList.add('girando');
        $('#localTitulo').textContent = 'Obtendo sua localização…';
        const p = await obterGps();
        const c = mapa.getCenter();
        if (p && distKm(p, CENTRO) <= RAIO_COBERTURA_KM) definirLocal(p, 'GPS do aparelho');
        else if (p) definirLocal([c.lat, c.lng], 'Fora da área do protótipo — usando o centro do mapa');
        else definirLocal([c.lat, c.lng], 'GPS indisponível — usando o centro do mapa');
        btn.classList.remove('girando');
    };

    const limparFoto = () => {
        form.foto = null;
        $('#inputFoto').value = '';
        $('#previewFoto').hidden = true;
        $('#removerFoto').hidden = true;
        $('.foto__vazio').hidden = false;
        $('#areaFoto').classList.remove('com-foto');
    };

    function abrirReportar(localManual, origem) {
        form.sinal = 'fumaca';
        form.tipo = null;
        form.local = null;
        $$('.sinal').forEach(s => s.classList.toggle('ativo', s.dataset.sinal === 'fumaca'));
        $$('input[name="tipo"]').forEach(i => { i.checked = false; });
        $('#descricao').value = '';
        $('#contaDesc').textContent = '0';
        limparFoto();
        $('#reportar').hidden = false;
        if (localManual) definirLocal(localManual, origem);
        else buscarLocal();
        atualizarEnviar();
    }
    const fecharReportar = () => { $('#reportar').hidden = true; };

    $('#btnReportar').onclick = () => abrirReportar(null);
    $('#fecharReportar').onclick = fecharReportar;
    $('#atualizarGps').onclick = buscarLocal;

    $$('.sinal').forEach(s => s.onclick = () => {
        form.sinal = s.dataset.sinal;
        $$('.sinal').forEach(x => x.classList.toggle('ativo', x === s));
    });
    $$('input[name="tipo"]').forEach(i => i.onchange = () => { form.tipo = i.value; atualizarEnviar(); });
    $('#descricao').oninput = (e) => { $('#contaDesc').textContent = e.target.value.length; };

    $('#inputFoto').onchange = (e) => {
        const f = e.target.files[0];
        if (!f) return;
        form.foto = f;
        const img = $('#previewFoto');
        img.src = URL.createObjectURL(f);
        img.hidden = false;
        $('#removerFoto').hidden = false;
        $('.foto__vazio').hidden = true;
        $('#areaFoto').classList.add('com-foto');
    };
    $('#removerFoto').onclick = (e) => { e.preventDefault(); e.stopPropagation(); limparFoto(); };

    btnEnviar.onclick = async () => {
        if (!form.tipo || !form.local) return;
        const dados = new FormData();
        dados.append('tipo', form.tipo);
        dados.append('sinal', form.sinal);
        dados.append('latitude', form.local[0].toFixed(6));
        dados.append('longitude', form.local[1].toFixed(6));
        dados.append('descricao', $('#descricao').value.trim());
        if (form.foto) dados.append('foto', form.foto);

        btnEnviar.disabled = true;
        btnEnviar.textContent = 'Enviando…';
        try {
            const r = await fetch(API, {
                method: 'POST',
                headers: { 'X-CSRF-TOKEN': CSRF, Accept: 'application/json' },
                body: dados,
            });
            if (!r.ok) {
                const erro = await r.json().catch(() => ({}));
                throw new Error(erro.message || 'Falha ao enviar');
            }
            const { data } = await r.json();
            relatos.unshift(data);
            filtro = '';
            $$('#chips .chip').forEach(x => x.classList.toggle('ativo', x.dataset.tipo === ''));
            desenhar();
            fecharReportar();
            trocarAba('mapa');
            mapa.flyTo([data.latitude, data.longitude], 15, { duration: 0.8 });
            toast('Relato enviado! Ele já aparece no mapa.');
            if (window.parent !== window) window.parent.postMessage('serra-alerta:novo-relato', location.origin);
        } catch (e) {
            toast(`Não foi possível enviar: ${e.message}`);
            atualizarEnviar();
        }
    };

    document.addEventListener('keydown', (e) => {
        if (e.key !== 'Escape') return;
        if (sheet.classList.contains('aberto')) fecharDetalhe();
        else if (!$('#reportar').hidden) fecharReportar();
        else if (!$('#pinoCentral').hidden) modoMarcar(false);
    });

    carregar();
    setInterval(carregar, 20000);
})();
