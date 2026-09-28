@php
    $embed = request()->boolean('embed');
    // Ícones em linha (estilo Lucide), equivalentes aos Material Icons Rounded do app.
    $i = [
        'flame' => '<path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"/>',
        'settings' => '<path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/><circle cx="12" cy="12" r="3"/>',
        'clock' => '<circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/>',
        'tune' => '<path d="M21 4h-7M10 4H3M21 12h-9M8 12H3M21 20h-5M12 20H3M14 2v4M8 10v4M16 18v4"/>',
        'satellite' => '<path d="M13 7 9 3 5 7l4 4"/><path d="m17 11 4 4-4 4-4-4"/><path d="m8 12 4 4 6-6-4-4Z"/><path d="m16 8 3-3"/><path d="M9 21a6 6 0 0 0-6-6"/>',
        'plus' => '<path d="M12 5v14M5 12h14"/>',
        'minus' => '<path d="M5 12h14"/>',
        'locate' => '<circle cx="12" cy="12" r="7"/><circle cx="12" cy="12" r="2.5"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3"/>',
        'map' => '<path d="M3 6l6-3 6 3 6-3v15l-6 3-6-3-6 3z"/><path d="M9 3v15M15 6v15"/>',
        'list' => '<path d="M3 12h.01M3 18h.01M3 6h.01M8 12h13M8 18h13M8 6h13"/>',
        'sprout' => '<path d="M7 20h10"/><path d="M10 20c5.5-2.5.8-6.4 3-10"/><path d="M9.5 9.4c1.1.8 1.8 2.2 2.3 3.7-2 .4-3.5.4-4.8-.3-1.2-.6-2.3-1.9-3-4.2 2.8-.5 4.4 0 5.5.8z"/><path d="M14.1 6a7 7 0 0 0-1.1 4c1.9-.1 3.3-.6 4.3-1.4 1-1 1.6-2.3 1.7-4.6-2.7.1-4 1-4.9 2z"/>',
        'phone' => '<path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72c.13.96.36 1.9.7 2.81a2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45c.91.34 1.85.57 2.81.7A2 2 0 0 1 22 16.92z"/>',
        'camera' => '<path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"/><circle cx="12" cy="13" r="3"/>',
        'back' => '<path d="m12 19-7-7 7-7"/><path d="M19 12H5"/>',
        'arrow' => '<path d="M5 12h14"/><path d="m12 5 7 7-7 7"/>',
        'check' => '<path d="M20 6 9 17l-5-5"/>',
        'close' => '<path d="M18 6 6 18M6 6l12 12"/>',
        'pin' => '<path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/>',
        'pinoff' => '<path d="M12.75 7.09a3 3 0 0 1 2.16 2.16"/><path d="M17.07 17.07c-1.63 2.17-3.53 3.91-4.47 4.73a1 1 0 0 1-1.2 0C9.54 20.19 4 14.99 4 10a8 8 0 0 1 1.43-4.57"/><path d="m2 2 20 20"/><path d="M8.48 2.82A8 8 0 0 1 20 10c0 1.18-.31 2.38-.81 3.53"/><path d="M9.13 9.13a3 3 0 0 0 3.74 3.74"/>',
        'refresh' => '<path d="M21 12a9 9 0 1 1-2.64-6.36L21 8"/><path d="M21 3v5h-5"/>',
        'info' => '<circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/>',
        'share' => '<path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"/><path d="m16 6-4-4-4 4"/><path d="M12 2v13"/>',
        'warning' => '<path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3"/><path d="M12 9v4M12 17h.01"/>',
        'cloudoff' => '<path d="m2 2 20 20"/><path d="M5.78 5.78A7 7 0 0 0 9 19h8.5a4.5 4.5 0 0 0 1.31-.19"/><path d="M21.53 16.5A4.5 4.5 0 0 0 17.5 10h-1.79A7 7 0 0 0 10 5.07"/>',
        'bulb' => '<path d="M15 14c.2-1 .7-1.7 1.5-2.5 1-.9 1.5-2.2 1.5-3.5A6 6 0 0 0 6 8c0 1 .2 2.2 1.5 3.5.7.7 1.3 1.5 1.5 2.5"/><path d="M9 18h6M10 22h4"/>',
        'gps' => '<circle cx="12" cy="12" r="10"/><path d="M22 12h-4M6 12H2M12 6V2M12 22v-4"/>',
        'noaccount' => '<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="m17 8 5 5M22 8l-5 5"/>',
        'tractor' => '<path d="m10 11 11 .9a1 1 0 0 1 .8 1.1l-.67 4.16a1 1 0 0 1-.99.84H20"/><path d="M16 18h-5"/><path d="M18 5a1 1 0 0 0-1 1v5.57"/><path d="M3 4h8.13a1 1 0 0 1 .99.86L13 11.25"/><path d="M4 11V4"/><path d="M8 10.1V4"/><circle cx="18" cy="18" r="2"/><circle cx="7" cy="15" r="5"/>',
        'trees' => '<path d="M10 10v.2A3 3 0 0 1 8.9 16H5a3 3 0 0 1-1-5.8V10a3 3 0 0 1 6 0Z"/><path d="M7 16v6M13 19v3"/><path d="M12 19h8.3a1 1 0 0 0 .7-1.7L18 14h.3a1 1 0 0 0 .7-1.7L16 9h.2a1 1 0 0 0 .8-1.7L13 3l-1.4 1.5"/>',
        'cloud' => '<path d="M17.5 19H9a7 7 0 1 1 6.71-9h1.79a4.5 4.5 0 1 1 0 9Z"/>',
        'trash' => '<path d="M3 6h18"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"/><path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/><path d="M10 11v6M14 11v6"/>',
        'lock' => '<rect width="18" height="11" x="3" y="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>',
    ];
    $ic = fn ($n) => '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">'.$i[$n].'</svg>';
    $iconesJs = collect(['tractor', 'flame', 'trees', 'cloud', 'check', 'pin', 'pinoff', 'arrow', 'info', 'satellite', 'camera', 'list', 'close', 'trash'])->mapWithKeys(fn ($n) => [$n => $ic($n)])->all();
    $equipe = ['André Lyra Fernandes', 'Gabriel Maia Miguel', 'Larissa Gabriela Sant’Angelo Dias', 'Mariana Peixoto Chahud', 'Victoria Carolina Ferreira da Silva'];
@endphp
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <meta name="theme-color" content="#161311">
    <title>Serra Alerta — app</title>
    <link rel="icon" href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 32 32'%3E%3Crect width='32' height='32' rx='8' fill='%23161311'/%3E%3Cpath fill='%23FF7A45' d='M16 5s8 6 8 13a8 8 0 0 1-16 0c0-4 2-6 4-8 0 3 2 5 4 5-1-4 0-7 0-10z'/%3E%3C/svg%3E">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css">
    <link rel="stylesheet" href="{{ asset('css/app-demo.css') }}">
</head>
<body class="{{ $embed ? 'embed' : 'avulso' }}">

@unless ($embed)
    <aside class="apresentacao">
        <a href="{{ route('landing') }}" class="voltar">← Voltar ao site</a>
        <h1>Serra <span>Alerta</span></h1>
        <p>Demonstração navegável do aplicativo. O app nativo é feito em Kotlin + Jetpack Compose; esta versão web reproduz as mesmas telas e envia os relatos para o mapa colaborativo do site.</p>
        <ol>
            <li>Toque numa chama para abrir um relato, ou num foco com selo azul para ver o dado do INPE.</li>
            <li>Use <strong>Últimos 7 dias</strong> e <strong>Satélite INPE</strong> para filtrar o mapa.</li>
            <li>Toque na <strong>câmera</strong> no centro da barra: foto, o que você vê, localização.</li>
            <li>Veja <strong>Relatos</strong>, <strong>Prevenção</strong> e os atalhos de <strong>193</strong>.</li>
        </ol>
    </aside>
@endunless

<div class="tela">
<div class="app" id="app"
     data-api="{{ route('api.relatos.index') }}"
     data-focos="{{ route('api.focos') }}"
     data-embed="{{ $embed ? 1 : 0 }}">

    <div id="mapa" class="mapa"></div>

    {{-- ================= MAPA ================= --}}
    <div class="camada-mapa" id="camadaMapa">
        <div class="topo">
            <header class="cartao cabecalho">
                <span class="logo">{!! $ic('flame') !!}</span>
                <div class="cabecalho__txt">
                    <strong>Serra Alerta</strong>
                    <span class="eyebrow">São João da Boa Vista · SP</span>
                </div>
                <button class="btn-circulo sem-borda" data-ir="config" aria-label="Configurações">{!! $ic('settings') !!}</button>
            </header>
            <div class="chips">
                <button class="chip ativo" id="chipPeriodo">{!! $ic('clock') !!}<span>Últimos 7 dias</span></button>
                <button class="chip" id="chipCategorias">{!! $ic('tune') !!}<span>Todas as categorias</span></button>
                <button class="chip chip--inpe ativo" id="chipInpe">{!! $ic('satellite') !!}<span>Satélite INPE</span></button>
            </div>
            <button class="status-focos" id="statusFocos"><i class="status-focos__ponto"></i><span>Focos INPE não carregados</span></button>
        </div>

        <div class="controles">
            <div class="grupo">
                <button id="zoomMais" aria-label="Aproximar">{!! $ic('plus') !!}</button>
                <button id="zoomMenos" aria-label="Afastar">{!! $ic('minus') !!}</button>
            </div>
            <button class="solo" id="btnLocalizar" aria-label="Centralizar na minha localização">{!! $ic('locate') !!}</button>
        </div>

        <div class="legenda" id="legenda"></div>
    </div>

    {{-- ================= DOCK ================= --}}
    <nav class="dock" id="dock">
        <button class="dock__item ativo" data-aba="mapa">{!! $ic('map') !!}<span>Mapa</span><i></i></button>
        <button class="dock__item" data-aba="relatos">{!! $ic('list') !!}<span>Relatos</span><i></i></button>
        <button class="dock__registrar" data-registrar aria-label="Registrar ocorrência">{!! $ic('camera') !!}</button>
        <button class="dock__item" data-aba="prevencao">{!! $ic('sprout') !!}<span>Prevenção</span><i></i></button>
        <button class="dock__item dock__item--sos" data-aba="emergencia">{!! $ic('phone') !!}<span>193</span><i></i></button>
    </nav>

    {{-- ================= RELATOS ================= --}}
    <section class="tela-app com-dock" id="tela-relatos" hidden>
        <header class="cabeca"><span class="eyebrow">Base colaborativa da demonstração</span><h1>Relatos</h1></header>
        <div class="rolagem">
            <div class="cartao-base estatisticas">
                <div><strong id="stTotal">0</strong><small>relatos</small></div>
                <div><strong id="st24h">0</strong><small>últimas 24 h</small></div>
                <div><strong id="stMeus" class="brasa">0</strong><small>seus</small></div>
            </div>
            <div class="segmentado" id="segRelatos">
                <button class="ativo" data-seg="todos">Todos</button>
                <button data-seg="meus">Enviados daqui</button>
            </div>
            <div id="listaRelatos"></div>
        </div>
    </section>

    {{-- ================= PREVENÇÃO ================= --}}
    <section class="tela-app com-dock" id="tela-prevencao" hidden>
        <header class="cabeca"><span class="eyebrow">Manejo do fogo e boas práticas</span><h1>Prevenção</h1></header>
        <div class="rolagem">
            <div class="bloco-escuro">
                <canvas class="topo-art" data-cor="#FF7A45" data-centro=".85,.2" data-niveis="10"></canvas>
                <span class="eyebrow brasa-viva">Na estiagem</span>
                <p class="bloco-escuro__titulo">Pasto e vegetação secos espalham o fogo em minutos.</p>
            </div>
            <span class="eyebrow secao">Boas práticas</span>
            <div class="cartao-base lista-num">
                @foreach ([
                    ['Não queime lixo nem restos de poda', 'Leve para a coleta ou para o ecoponto do município.'],
                    ['Não descarte bitucas em estradas', 'Margens de rodovias e pastagens concentram os focos.'],
                    ['Mantenha aceiros limpos', 'Faixas sem vegetação ao redor de casas, cercas e plantações freiam o avanço do fogo.'],
                    ['Não solte balões', 'Um balão pode cair aceso a quilômetros de distância.'],
                ] as $n => [$titulo, $texto])
                    <div class="lista-num__item"><b>0{{ $n + 1 }}</b><div><strong>{{ $titulo }}</strong><small>{{ $texto }}</small></div></div>
                @endforeach
            </div>
            <span class="eyebrow secao">Controlada × irregular</span>
            <div class="cartao-base lista-cat" id="listaCatPrevencao"></div>
            <div class="cartao-base lei">
                <span class="eyebrow inpe">Lei Federal nº 14.944/2024</span>
                <strong>Política Nacional de Manejo Integrado do Fogo</strong>
                <p>Organiza a prevenção, o uso autorizado do fogo e o combate aos incêndios, envolvendo poder público, produtores e comunidade.</p>
                <ul id="resumoLei" hidden>
                    <li>Diferencia o uso autorizado do fogo, como a queima controlada, do fogo sem controle.</li>
                    <li>Prevê planos de manejo com ações de prevenção, preparo, combate e recuperação das áreas atingidas.</li>
                    <li>Reconhece o papel das brigadas, inclusive comunitárias e voluntárias, e a cooperação entre União, estados e municípios.</li>
                    <li>Mantém a exigência de autorização do órgão ambiental para queimas controladas.</li>
                </ul>
                <button class="link-inpe" id="btnResumoLei">Ler o resumo</button>
            </div>
            <div class="cartao-brasa">
                <strong>Viu fumaça? Registre e avise</strong>
                <p>Os primeiros minutos decidem o tamanho do incêndio.</p>
                <button class="botao botao--claro" data-registrar>{!! $ic('camera') !!}Registrar ocorrência</button>
                <span class="eyebrow">Sem cadastro, direto no mapa</span>
            </div>
        </div>
    </section>

    {{-- ================= EMERGÊNCIA ================= --}}
    <section class="tela-app tela-app--escura com-dock" id="tela-emergencia" hidden>
        <header class="cabeca"><span class="eyebrow">Ligação direta, com um toque</span><h1>Emergência</h1></header>
        <div class="rolagem">
            <a class="cartao-193" href="tel:193">
                <span class="eyebrow">Incêndios e resgates</span>
                <span class="cartao-193__linha"><b>193</b><i>{!! $ic('phone') !!}</i></span>
                <strong>Corpo de Bombeiros</strong>
            </a>
            @foreach ([['199', 'Defesa Civil', 'Riscos e desastres ambientais'], ['190', 'Polícia Militar', 'Emergências policiais']] as [$num, $nome, $desc])
                <a class="cartao-tel" href="tel:{{ $num }}"><b>{{ $num }}</b><span><strong>{{ $nome }}</strong><small>{{ $desc }}</small></span><i>{!! $ic('phone') !!}</i></a>
            @endforeach
            <div class="cartao-escuro">
                <span class="eyebrow">O que informar na ligação</span>
                @foreach (['Onde você está e um ponto de referência', 'O que está queimando: pasto, mata, lixo', 'Para onde a fumaça e o vento seguem', 'Se há pessoas, animais ou casas em risco'] as $n => $t)
                    <div class="lista-num__item"><b>0{{ $n + 1 }}</b><span>{{ $t }}</span></div>
                @endforeach
            </div>
            <p class="nota-escura">Ligações gratuitas, inclusive sem crédito. Depois, registre a ocorrência no app.</p>
        </div>
    </section>

    {{-- ================= REGISTRO (passo 2) ================= --}}
    <section class="tela-app" id="tela-registro" hidden>
        <header class="cabeca cabeca--voltar">
            <button class="btn-circulo" data-voltar aria-label="Voltar">{!! $ic('back') !!}</button>
            <span class="eyebrow">Nova ocorrência · sem login</span>
            <h1>Nova ocorrência</h1>
        </header>
        <div class="rolagem rolagem--form">
            <div class="secao-form">
                <div class="secao-form__topo"><span class="eyebrow">Fotos · ao menos 1, até 3</span><span class="eyebrow" id="contaFotos">0/3</span></div>
                <input type="file" accept="image/*" capture="environment" id="inputFoto" multiple hidden>
                <div class="fotos" id="fotos"></div>
            </div>
            <div class="secao-form">
                <span class="eyebrow">O que você está vendo?</span>
                <div class="categorias" id="opcoesCategoria" role="radiogroup"></div>
            </div>
            <div class="secao-form">
                <span class="eyebrow">Localização</span>
                <div class="cartao-base local">
                    <div class="local__linha">
                        <span class="local__icone" id="localIcone">{!! $ic('pin') !!}</span>
                        <div><strong id="localStatus">Obtendo localização…</strong><small class="dado" id="localCoords"></small></div>
                    </div>
                    <div class="local__botoes">
                        <button class="botao botao--carvao botao--baixo" id="tentarGps" hidden>Tentar de novo</button>
                        <button class="botao botao--contorno botao--baixo" id="ajustarLocal">{!! $ic('map') !!}Ajustar no mapa</button>
                    </div>
                </div>
            </div>
            <div class="secao-form">
                <div class="secao-form__topo"><span class="eyebrow">Descrição · opcional</span><span class="eyebrow" id="contaDesc">0/280</span></div>
                <textarea id="descricao" maxlength="280" rows="3" placeholder="Ex.: fumaça escura perto da estrada, vento para a mata"></textarea>
            </div>
        </div>
        <footer class="rodape-acao">
            <button class="botao botao--brasa" id="enviarRelato" disabled>{!! $ic('flame') !!}<span>Registrar ocorrência</span></button>
            <small id="faltando">{!! $ic('info') !!}<span>Falta: foto, o que você está vendo</span></small>
        </footer>
    </section>

    {{-- ================= AJUSTAR LOCALIZAÇÃO ================= --}}
    <section class="ajuste" id="tela-ajuste" hidden>
        <div class="ajuste__topo">
            <button class="btn-circulo" data-voltar aria-label="Voltar">{!! $ic('back') !!}</button>
            <div class="cartao"><strong>Ajustar localização</strong><small>Arraste o mapa para posicionar o pino</small></div>
        </div>
        <div class="pino-central">{!! $ic('pin') !!}</div>
        <div class="folha">
            <span class="eyebrow" id="ajusteDist">Sem GPS · posição escolhida no mapa</span>
            <strong class="dado dado--grande" id="ajusteCoords">—</strong>
            <p class="dica">{!! $ic('bulb') !!}<span>Ajuste quando o foco estiver longe de você: coloque o pino onde está a fumaça, não onde você está.</span></p>
            <button class="botao botao--carvao" id="confirmarAjuste">{!! $ic('check') !!}Confirmar posição</button>
        </div>
    </section>

    {{-- ================= CONFIRMAÇÃO ================= --}}
    <section class="tela-app" id="tela-confirmacao" hidden>
        <div class="confirmacao__topo">
            <canvas class="topo-art" data-cor="#FF7A45" data-centro=".2,.45" data-niveis="12"></canvas>
            <span class="check">{!! $ic('check') !!}</span>
            <span class="eyebrow">Enviado ao mapa colaborativo</span>
            <h1>Relato registrado</h1>
            <p>Sua ocorrência já aparece no mapa. Compartilhe agora com a Defesa Civil ou sua brigada para acelerar a resposta.</p>
        </div>
        <div class="rolagem">
            <div class="cartao-base resumo-relato" id="resumoConfirmacao"></div>
            <div class="cartao-risco">
                <strong>{!! $ic('warning') !!}Há risco a pessoas, animais ou casas?</strong>
                <a class="botao botao--perigo botao--medio" href="tel:193">{!! $ic('phone') !!}Ligar para os Bombeiros · 193</a>
            </div>
        </div>
        <footer class="rodape-acao rodape-acao--limpo">
            <button class="botao botao--brasa" data-compartilhar>{!! $ic('share') !!}Compartilhar relato</button>
            <button class="botao-texto" data-ir-mapa>Voltar ao mapa</button>
        </footer>
    </section>

    {{-- ================= DETALHE DO RELATO ================= --}}
    <section class="tela-app" id="tela-detalhe" hidden>
        <div class="detalhe__foto" id="detalheFoto"></div>
        <div class="detalhe__barra">
            <button class="btn-circulo btn-circulo--vidro" data-voltar aria-label="Voltar">{!! $ic('back') !!}</button>
            <button class="btn-circulo btn-circulo--vidro" id="excluirRelato" aria-label="Excluir relato" hidden>{!! $ic('trash') !!}</button>
        </div>
        <div class="rolagem detalhe__rolagem">
            <div class="detalhe__folha" id="detalheConteudo"></div>
        </div>
        <footer class="rodape-acao rodape-acao--linha">
            <a class="botao botao--perigo" href="tel:193">{!! $ic('phone') !!}193</a>
            <button class="botao botao--brasa" data-compartilhar>{!! $ic('share') !!}Compartilhar</button>
        </footer>
    </section>

    {{-- ================= FOCO OFICIAL (INPE) ================= --}}
    <section class="tela-app" id="tela-foco" hidden>
        <header class="cabeca cabeca--voltar">
            <button class="btn-circulo" data-voltar aria-label="Voltar">{!! $ic('back') !!}</button>
            <span class="eyebrow">INPE · BDQueimadas</span>
            <h1>Foco oficial</h1>
        </header>
        <div class="rolagem" id="focoConteudo"></div>
        <footer class="rodape-acao rodape-acao--limpo">
            <button class="botao botao--carvao" data-registrar>{!! $ic('camera') !!}Registrar o que estou vendo</button>
        </footer>
    </section>

    {{-- ================= CONFIGURAÇÕES ================= --}}
    <section class="tela-app" id="tela-config" hidden>
        <header class="cabeca cabeca--voltar">
            <button class="btn-circulo" data-voltar aria-label="Voltar">{!! $ic('back') !!}</button>
            <span class="eyebrow">Preferências deste navegador</span>
            <h1>Configurações</h1>
        </header>
        <div class="rolagem">
            <span class="eyebrow secao">Localização</span>
            <div class="cartao-base">
                <label class="camada-linha"><span class="camada-linha__marca">{!! $ic('gps') !!}</span><span><strong>GPS de alta precisão ao registrar</strong><small>Desligado, economiza bateria e pode ser menos exato</small></span><input type="checkbox" class="switch" id="cfgGps" checked></label>
            </div>
            <span class="eyebrow secao">Dados</span>
            <div class="cartao-base">
                <label class="camada-linha"><span class="camada-linha__marca inpe">{!! $ic('satellite') !!}</span><span><strong>Atualizar focos do INPE ao abrir o mapa</strong><small id="cfgUltimaAtualizacao">Última atualização: nunca</small></span><input type="checkbox" class="switch" id="cfgFocos" checked></label>
                <hr>
                <div class="camada-linha"><span class="camada-linha__marca">{!! $ic('list') !!}</span><span><strong>Histórico deste aparelho</strong><small id="cfgHistorico">0 relatos enviados daqui</small></span></div>
                <hr>
                <button class="camada-linha camada-linha--acao" id="cfgLimpar"><span class="camada-linha__marca perigo">{!! $ic('trash') !!}</span><span><strong>Limpar histórico deste aparelho</strong><small>Exclui da base os relatos enviados daqui</small></span></button>
            </div>
            <span class="eyebrow secao">Privacidade e projeto</span>
            <div class="cartao-base">
                <button class="camada-linha camada-linha--acao" id="cfgPrivacidade"><span class="camada-linha__marca">{!! $ic('lock') !!}</span><span><strong>Política de privacidade</strong><small>Nenhum dado pessoal é coletado</small></span></button>
                <p class="cfg-texto" id="cfgPrivacidadeTexto" hidden>O Serra Alerta não exige conta e não coleta dados pessoais identificáveis. Nesta demonstração, fotos, localização e descrições dos relatos vão para a base do site, que é apagada antes das apresentações. Focos oficiais são consultados de fonte pública do INPE/BDQueimadas.</p>
                <hr>
                <button class="camada-linha camada-linha--acao" data-ir="sobre"><span class="camada-linha__marca">{!! $ic('info') !!}</span><span><strong>Sobre o projeto</strong><small>IFSP · São João da Boa Vista</small></span></button>
                <hr>
                <button class="camada-linha camada-linha--acao" id="reverIntro"><span class="camada-linha__marca">{!! $ic('refresh') !!}</span><span><strong>Rever introdução</strong><small>Mostra de novo as telas de boas-vindas</small></span></button>
            </div>
        </div>
    </section>

    {{-- ================= SOBRE ================= --}}
    <section class="tela-app" id="tela-sobre" hidden>
        <header class="cabeca cabeca--voltar">
            <button class="btn-circulo" data-voltar aria-label="Voltar">{!! $ic('back') !!}</button>
            <span class="eyebrow">Projeto de extensão · Ciência da Computação</span>
            <h1>Sobre o projeto</h1>
        </header>
        <div class="rolagem">
            <div class="bloco-escuro">
                <canvas class="topo-art" data-cor="#FF7A45" data-centro=".8,.3" data-niveis="12"></canvas>
                <p class="marca-grande">Serra<br><span>Alerta</span></p>
                <p>Uma rede de vigilância colaborativa que alerta os focos de fogo antes que atinjam proporções incontroláveis, com a comunidade da Serra da Paulista como aliada.</p>
            </div>
            <div class="cartao-base sobre">
                <span class="eyebrow">Instituição</span>
                <strong>Instituto Federal de São Paulo</strong>
                <small>Campus São João da Boa Vista</small>
            </div>
            <div class="cartao-base sobre">
                <span class="eyebrow">Equipe</span>
                @foreach ($equipe as $nome)
                    @php($p = explode(' ', $nome))
                    <div class="membro"><b>{{ mb_substr($p[0], 0, 1) }}{{ mb_substr(end($p), 0, 1) }}</b>{{ $nome }}</div>
                @endforeach
            </div>
            <div class="cartao-base sobre"><span class="eyebrow">Orientação</span><strong>Prof. Elias Mendes Oliveira</strong></div>
            <div class="cartao-base sobre">
                <span class="eyebrow">Dados e créditos</span>
                <span>Focos de calor: INPE · BDQueimadas</span>
                <span>Mapa: © colaboradores do OpenStreetMap</span>
            </div>
            <div class="cartao-base sobre">
                <span class="eyebrow">Privacidade</span>
                <small>O Serra Alerta não exige conta e não coleta dados pessoais identificáveis. No app Android, fotos, localização e descrições ficam no próprio aparelho; nesta demonstração, os relatos vão para a base do site.</small>
            </div>
        </div>
    </section>

    {{-- ================= FILTROS (folha) ================= --}}
    <div class="veu" id="veu" hidden></div>
    <section class="folha-filtros" id="folhaFiltros" aria-hidden="true">
        <span class="alca"></span>
        <div class="folha-filtros__topo"><h2>Filtros e camadas</h2><button class="link brasa" id="limparFiltros">Limpar</button></div>
        <div class="folha-filtros__corpo">
            <span class="eyebrow secao">Camadas</span>
            <div class="cartao-base">
                <label class="camada-linha"><span class="camada-linha__marca brasa">{!! $ic('flame') !!}</span><span><strong>Relatos da comunidade</strong><small>Cor da chama = categoria</small></span><input type="checkbox" class="switch" id="swRelatos" checked></label>
                <hr>
                <label class="camada-linha"><span class="camada-linha__marca inpe">{!! $ic('satellite') !!}</span><span><strong>Focos de calor oficiais</strong><small>INPE · BDQueimadas · requer internet</small></span><input type="checkbox" class="switch" id="swFocos" checked></label>
                <div class="escala-bloco"><span class="eyebrow">Intensidade do foco · FRP em MW</span><div class="escala" id="escalaFiltros"></div></div>
            </div>
            <span class="eyebrow secao">Período</span>
            <div class="segmentado" id="segPeriodo">
                <button data-periodo="1">24 h</button>
                <button data-periodo="7" class="ativo">7 dias</button>
                <button data-periodo="30">30 dias</button>
                <button data-periodo="datas">Datas</button>
            </div>
            <div class="datas" id="datas" hidden>
                <label>De <input type="date" id="dataInicio"></label>
                <label>Até <input type="date" id="dataFim"></label>
            </div>
            <span class="eyebrow secao">Categorias</span>
            <div class="categorias categorias--filtro" id="tilesCategorias"></div>
        </div>
        <div class="folha-filtros__rodape"><button class="botao botao--carvao" id="aplicarFiltros">Mostrar relatos</button></div>
    </section>

    {{-- ================= SPLASH + INTRODUÇÃO ================= --}}
    <section class="splash" id="splash" hidden>
        <canvas class="topo-art" data-cor="#FF7A45" data-centro=".7,.34" data-niveis="18"></canvas>
        <span class="eyebrow">IFSP · São João da Boa Vista</span>
        <div class="splash__marca"><b>Serra</b><b>Alerta</b><span>Monitoramento colaborativo de queimadas</span></div>
        <div class="splash__rodape"><span class="eyebrow">21°58′S · 46°47′W</span><span class="eyebrow">Serra da Paulista</span></div>
    </section>

    <section class="intro" id="intro" hidden>
        <div class="intro__topo"><span class="eyebrow" id="introPasso">01 / 02</span><button class="botao-texto" id="introPular">Pular</button></div>
        <div class="intro__pagina" data-pagina="0">
            <div class="intro__arte">
                <canvas class="topo-art" data-cor="#FF7A45" data-centro=".55,.55" data-niveis="12"></canvas>
                <span class="pilula pilula--clara"><i class="glifo glifo--p" style="--c:#C2410C">{!! $ic('flame') !!}</i>Relato · agora</span>
                <span class="pilula pilula--inpe">{!! $ic('satellite') !!}Foco INPE</span>
            </div>
            <h2>Viu fumaça ou fogo? Registre em segundos.</h2>
            <p>Moradores, produtores e visitantes formam uma rede de vigilância da Serra da Paulista. Seu relato aparece no mapa junto aos focos de calor do INPE.</p>
            <div class="recursos">
                <span class="cartao-base">{!! $ic('camera') !!}Foto</span>
                <span class="cartao-base">{!! $ic('gps') !!}GPS automático</span>
                <span class="cartao-base">{!! $ic('noaccount') !!}Sem cadastro</span>
                <span class="cartao-base">{!! $ic('cloudoff') !!}Funciona offline</span>
            </div>
        </div>
        <div class="intro__pagina" data-pagina="1" hidden>
            <h2>Um relato é um alerta, não uma confirmação.</h2>
            <p>Fumaça ou calor nem sempre é incêndio. Cada ocorrência é um aviso preliminar para que Defesa Civil e Bombeiros verifiquem. Ao registrar, escolha o que melhor descreve o que você vê:</p>
            <div class="cartao-base lista-cat" id="listaCatIntro"></div>
            <a class="cartao-escuro cartao-escuro--link" href="tel:193"><i>{!! $ic('phone') !!}</i><span>Se houver risco a pessoas, animais ou casas, <b>ligue 193</b> antes de registrar.</span></a>
        </div>
        <div class="intro__rodape">
            <div class="indicador"><i class="ativo"></i><i></i></div>
            <button class="botao botao--carvao" id="introAvancar"><span>Continuar</span>{!! $ic('arrow') !!}</button>
        </div>
    </section>

    <div class="toast" id="toast" hidden></div>
</div>
</div>

<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script>
    window.SERRA_ICONES = @json($iconesJs);
</script>
<script src="{{ asset('js/app-demo.js') }}"></script>
</body>
</html>
