@php
    $embed = request()->boolean('embed');
    $i = [
        'flame' => '<path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"/>',
        'layers' => '<path d="m12 2 10 5-10 5L2 7z"/><path d="m2 17 10 5 10-5"/><path d="m2 12 10 5 10-5"/>',
        'plus' => '<path d="M12 5v14M5 12h14"/>',
        'minus' => '<path d="M5 12h14"/>',
        'locate' => '<circle cx="12" cy="12" r="7"/><circle cx="12" cy="12" r="2.5"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3"/>',
        'pencil' => '<path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z"/>',
        'info' => '<circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/>',
        'map' => '<path d="M3 6l6-3 6 3 6-3v15l-6 3-6-3-6 3z"/><path d="M9 3v15M15 6v15"/>',
        'target' => '<circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/>',
        'close' => '<path d="M18 6 6 18M6 6l12 12"/>',
        'cloud' => '<path d="M17.5 19H9a7 7 0 1 1 6.71-9h1.79a4.5 4.5 0 1 1 0 9Z"/>',
        'camera' => '<path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"/><circle cx="12" cy="13" r="3"/>',
        'pin' => '<path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/>',
        'refresh' => '<path d="M21 12a9 9 0 1 1-2.64-6.36L21 8"/><path d="M21 3v5h-5"/>',
        'phone' => '<path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72c.13.96.36 1.9.7 2.81a2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45c.91.34 1.85.57 2.81.7A2 2 0 0 1 22 16.92z"/>',
        'clock' => '<circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/>',
        'arrow' => '<path d="M5 12h14"/><path d="m12 5 7 7-7 7"/>',
        'tree' => '<path d="M12 22v-7"/><path d="M17 8A5 5 0 0 0 7 8a4 4 0 0 0-2 7.46A4 4 0 0 0 8 22h8a4 4 0 0 0 3-6.54A4 4 0 0 0 17 8Z"/>',
    ];
    $ic = fn ($n) => '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">'.$i[$n].'</svg>';
@endphp
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <meta name="theme-color" content="#FAF7F2">
    <title>Serra Alerta — app</title>
    <link rel="icon" href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 32 32'%3E%3Crect width='32' height='32' rx='8' fill='%23D9482B'/%3E%3Cpath fill='white' d='M16 5s8 6 8 13a8 8 0 0 1-16 0c0-4 2-6 4-8 0 3 2 5 4 5-1-4 0-7 0-10z'/%3E%3C/svg%3E">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css">
    <link rel="stylesheet" href="{{ asset('css/app-demo.css') }}">
</head>
<body class="{{ $embed ? 'embed' : 'avulso' }}">

@unless ($embed)
    <aside class="apresentacao">
        <a href="{{ route('landing') }}" class="voltar">← Voltar ao site</a>
        <h1>Serra Alerta</h1>
        <p>Protótipo navegável do aplicativo. O app nativo é desenvolvido em Kotlin + Jetpack Compose; esta versão web reproduz o mesmo fluxo para demonstração.</p>
        <ol>
            <li>Toque em um marcador para ver os detalhes do relato.</li>
            <li>Use os filtros para separar controladas, irregulares e incêndios.</li>
            <li>Toque em <strong>Reportar foco</strong> e envie um relato.</li>
            <li>Abra <strong>Áreas</strong> para ver onde os relatos se concentram.</li>
        </ol>
    </aside>
@endunless

<div class="tela">
<div class="app" id="app" data-api="{{ route('api.relatos.index') }}">

    <div id="mapa" class="mapa"></div>

    {{-- Cabeçalho --}}
    <div class="topo">
        <header class="cartao cabecalho">
            <span class="logo">{!! $ic('flame') !!}</span>
            <div class="cabecalho__txt">
                <strong>Serra Alerta</strong>
                <small><span id="totalRelatos">–</span> relatos · São João da Boa Vista</small>
            </div>
            <button class="btn-quadrado" id="btnCamadas" aria-label="Camadas do mapa">{!! $ic('layers') !!}</button>
            <div class="menu-camadas" id="menuCamadas" hidden>
                <button data-camada="padrao" class="ativo">Padrão</button>
                <button data-camada="relevo">Relevo</button>
                <button data-camada="satelite">Satélite</button>
            </div>
        </header>
        <div class="chips" id="chips">
            <button class="chip ativo" data-tipo="">Todos <b data-cont="">–</b></button>
            <button class="chip" data-tipo="controlada"><i class="ponto controlada"></i>Controlada <b data-cont="controlada">–</b></button>
            <button class="chip" data-tipo="irregular"><i class="ponto irregular"></i>Irregular <b data-cont="irregular">–</b></button>
            <button class="chip" data-tipo="incendio"><i class="ponto incendio"></i>Incêndio <b data-cont="incendio">–</b></button>
        </div>
        <span class="credito">© OpenStreetMap</span>
    </div>

    {{-- Controles do mapa --}}
    <div class="controles">
        <div class="grupo">
            <button id="zoomMais" aria-label="Aproximar">{!! $ic('plus') !!}</button>
            <button id="zoomMenos" aria-label="Afastar">{!! $ic('minus') !!}</button>
        </div>
        <button class="solo" id="btnLocalizar" aria-label="Minha localização">{!! $ic('locate') !!}</button>
        <button class="solo" id="btnMarcar" aria-label="Marcar local no mapa">{!! $ic('pencil') !!}</button>
    </div>

    <div class="aviso" id="aviso">{!! $ic('info') !!}<span>Relato é um alerta preliminar — não confirma um incêndio.</span></div>

    {{-- Modo marcar no mapa --}}
    <div class="pino-central" id="pinoCentral" hidden>{!! $ic('pin') !!}</div>
    <div class="painel-marcar" id="painelMarcar" hidden>
        <strong>Marcar local do foco</strong>
        <p>Arraste o mapa até o ponto onde você vê fumaça ou fogo.</p>
        <div class="linha-botoes">
            <button class="btn btn--contorno" id="cancelarMarcar">Cancelar</button>
            <button class="btn btn--brasa" id="confirmarMarcar">Usar este local</button>
        </div>
    </div>

    {{-- Barra inferior --}}
    <nav class="barra" id="barra">
        <button class="barra__item ativo" data-aba="mapa"><span>{!! $ic('map') !!}</span>Mapa</button>
        <button class="barra__reportar" id="btnReportar">{!! $ic('flame') !!} Reportar foco</button>
        <button class="barra__item" data-aba="areas"><span>{!! $ic('target') !!}</span>Áreas</button>
    </nav>

    {{-- Aba Áreas --}}
    <section class="areas" id="areas" hidden>
        <h2>Áreas de ocorrência</h2>
        <p class="sub">Onde os relatos da comunidade se concentram.</p>
        <div class="resumo">
            <div><strong id="resTotal">0</strong><small>relatos</small></div>
            <div><strong id="res24h">0</strong><small>últimas 24 h</small></div>
            <div><strong id="resNucleos">0</strong><small>núcleos</small></div>
        </div>
        <h3 class="titulo-lista">Núcleos com mais relatos</h3>
        <div id="listaNucleos"></div>
        <div class="prioritaria">
            <strong>{!! $ic('tree') !!} Área prioritária</strong>
            <p>A Serra da Paulista integra as Áreas Prioritárias para a Biodiversidade de importância “Extremamente Alta” (MMA) e a Reserva da Biosfera da Mata Atlântica.</p>
            <button id="verSerra">Ver Serra da Paulista no mapa</button>
        </div>
    </section>

    {{-- Tela Reportar --}}
    <section class="reportar" id="reportar" hidden>
        <div class="reportar__topo">
            <button class="icone" id="fecharReportar" aria-label="Fechar">{!! $ic('close') !!}</button>
            <h2>Reportar foco</h2>
            <span class="selo-verde">Sem cadastro</span>
        </div>
        <form class="reportar__corpo" id="formRelato" novalidate>
            <div class="campo">
                <label>O que você está vendo?</label>
                <div class="sinais">
                    <button type="button" class="sinal ativo" data-sinal="fumaca">{!! $ic('cloud') !!}Fumaça</button>
                    <button type="button" class="sinal" data-sinal="fogo">{!! $ic('flame') !!}Fogo</button>
                </div>
            </div>
            <div class="campo">
                <label>Parece ser…</label>
                <div class="tipos">
                    <label class="tipo" style="--cor:#E8A33D"><input type="radio" name="tipo" value="controlada"><i></i><span><strong>Queimada controlada</strong><small>Queima de manejo, aparentemente sob vigilância</small></span></label>
                    <label class="tipo" style="--cor:#E4572E"><input type="radio" name="tipo" value="irregular"><i></i><span><strong>Queimada irregular</strong><small>Queima de lixo, pasto ou restos sem controle</small></span></label>
                    <label class="tipo" style="--cor:#A61E1E"><input type="radio" name="tipo" value="incendio"><i></i><span><strong>Incêndio florestal</strong><small>Fogo se espalhando em vegetação nativa</small></span></label>
                </div>
            </div>
            <div class="campo">
                <label>Foto (opcional)</label>
                <label class="foto" id="areaFoto">
                    <input type="file" accept="image/*" capture="environment" id="inputFoto" hidden>
                    <span class="foto__vazio">{!! $ic('camera') !!}Tirar ou escolher foto</span>
                    <img id="previewFoto" alt="Pré-visualização da foto" hidden>
                    <button type="button" class="foto__remover" id="removerFoto" hidden aria-label="Remover foto">{!! $ic('close') !!}</button>
                </label>
            </div>
            <div class="campo">
                <label for="descricao">Descrição</label>
                <textarea id="descricao" maxlength="280" rows="3" placeholder="Ex.: fumaça escura atrás da fazenda, vento forte para o norte…"></textarea>
                <small class="contador"><span id="contaDesc">0</span>/280</small>
            </div>
            <div class="campo">
                <label>Localização</label>
                <div class="local">
                    <span class="local__icone">{!! $ic('pin') !!}</span>
                    <div class="local__txt">
                        <strong id="localTitulo">Obtendo sua localização…</strong>
                        <small id="localCoords"></small>
                        <small id="localOrigem"></small>
                    </div>
                    <button type="button" class="icone" id="atualizarGps" aria-label="Atualizar GPS">{!! $ic('refresh') !!}</button>
                </div>
            </div>
            <div class="alerta-193">
                <span>Risco a pessoas ou casas? Ligue já para os Bombeiros.</span>
                <a href="tel:193">{!! $ic('phone') !!}193</a>
            </div>
        </form>
        <div class="reportar__rodape">
            <button class="btn btn--brasa btn--largo" id="enviarRelato" disabled>Escolha o tipo para enviar</button>
        </div>
    </section>

    {{-- Detalhe do relato --}}
    <div class="veu" id="veu" hidden></div>
    <section class="sheet" id="sheet" aria-hidden="true">
        <span class="sheet__alca"></span>
        <div id="sheetConteudo"></div>
    </section>

    <div class="toast" id="toast" hidden></div>
</div>
</div>

<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script>
    window.SERRA_ICONES = { clock: @json($ic('clock')), pin: @json($ic('pin')), phone: @json($ic('phone')), info: @json($ic('info')), arrow: @json($ic('arrow')) };
</script>
<script src="{{ asset('js/app-demo.js') }}"></script>
</body>
</html>
