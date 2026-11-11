@php
    // Ícones em linha (traço 2px, estilo Lucide), os mesmos da landing. Só os usados nesta página.
    $icones = [
        'flame' => '<path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"/>',
        'pin' => '<path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/>',
        'camera' => '<path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"/><circle cx="12" cy="13" r="3"/>',
        'noaccount' => '<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="m17 8 5 5M22 8l-5 5"/>',
        'map' => '<path d="M3 6l6-3 6 3 6-3v15l-6 3-6-3-6 3z"/><path d="M9 3v15M15 6v15"/>',
        'target' => '<circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/>',
        'phone' => '<rect width="14" height="20" x="5" y="2" rx="2"/><path d="M12 18h.01"/>',
        'call' => '<path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72c.13.96.36 1.9.7 2.81a2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45c.91.34 1.85.57 2.81.7A2 2 0 0 1 22 16.92z"/>',
        'leaf' => '<path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z"/><path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12"/>',
        'droplet' => '<path d="M12 22a7 7 0 0 0 7-7c0-2-1-3.9-3-5.5s-3.5-4-4-6.5c-.5 2.5-2 4.9-4 6.5C6 11.1 5 13 5 15a7 7 0 0 0 7 7z"/>',
        'arrow' => '<path d="M5 12h14"/><path d="m12 5 7 7-7 7"/>',
        'trash' => '<path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>',
        'sun' => '<circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"/>',
        'shield' => '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><path d="m9 12 2 2 4-4"/>',
        'users' => '<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>',
        'info' => '<circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/>',
        'cloudoff' => '<path d="m2 2 20 20"/><path d="M5.78 5.78A7 7 0 0 0 9 19h8.5a4.5 4.5 0 0 0 1.31-.19"/><path d="M21.53 16.5A4.5 4.5 0 0 0 17.5 10h-1.79A7 7 0 0 0 10 5.07"/>',
        'download' => '<path d="M12 3v12"/><path d="m7 10 5 5 5-5"/><path d="M5 21h14"/>',
        'code' => '<path d="m16 18 6-6-6-6"/><path d="m8 6-6 6 6 6"/>',
        'book' => '<path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>',
        'menu' => '<path d="M4 6h16M4 12h16M4 18h16"/>',
        'video' => '<path d="m22 8-6 4 6 4V8Z"/><rect width="14" height="12" x="2" y="6" rx="2" ry="2"/>',
        'play' => '<polygon points="6 3 20 12 6 21 6 3"/>',
        'pause' => '<rect x="5" y="4" width="5" height="16" rx="1"/><rect x="14" y="4" width="5" height="16" rx="1"/>',
        'replay' => '<path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/><path d="M3 3v5h5"/>',
        'volume-on' => '<path d="M11 4.702a.705.705 0 0 0-1.203-.498L6.413 7.587A1.4 1.4 0 0 1 5.416 8H3a1 1 0 0 0-1 1v6a1 1 0 0 0 1 1h2.416a1.4 1.4 0 0 1 .997.413l3.383 3.384A.705.705 0 0 0 11 19.298z"/><path d="M16 9a5 5 0 0 1 0 6"/><path d="M19.364 18.364a9 9 0 0 0 0-12.728"/>',
        'volume-off' => '<path d="M11 4.702a.705.705 0 0 0-1.203-.498L6.413 7.587A1.4 1.4 0 0 1 5.416 8H3a1 1 0 0 0-1 1v6a1 1 0 0 0 1 1h2.416a1.4 1.4 0 0 1 .997.413l3.383 3.384A.705.705 0 0 0 11 19.298z"/><line x1="22" x2="16" y1="9" y2="15"/><line x1="16" x2="22" y1="9" y2="15"/>',
        'monitor' => '<rect width="20" height="14" x="2" y="3" rx="2"/><path d="M8 21h8M12 17v4"/>',
    ];
    $ic = fn (string $nome, string $classe = 'ic') => '<svg class="'.$classe.'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">'.$icones[$nome].'</svg>';
    $maxFocos = max(array_column(array_merge($focos2020, $focos2025), 'valor'));
    $anosFocos = [['ano' => 2020, 'dados' => $focos2020], ['ano' => 2025, 'dados' => $focos2025]];
@endphp
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sobre a ferramenta — Serra Alerta</title>
    <meta name="description" content="Como usar o Serra Alerta, objetivos, funcionalidades, mapas, hipóteses e referências do projeto de monitoramento colaborativo de queimadas na Serra da Paulista, em São João da Boa Vista.">
    <meta name="theme-color" content="#161311">
    <link rel="icon" href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 32 32'%3E%3Crect width='32' height='32' rx='8' fill='%23161311'/%3E%3Cpath fill='%23FF7A45' d='M16 5s8 6 8 13a8 8 0 0 1-16 0c0-4 2-6 4-8 0 3 2 5 4 5-1-4 0-7 0-10z'/%3E%3C/svg%3E">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Bricolage+Grotesque:opsz,wght@12..96,500;12..96,700;12..96,800&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="{{ asset('css/landing.css') }}">
    <link rel="stylesheet" href="{{ asset('css/ferramenta.css') }}">
</head>
<body>

<header class="nav" id="nav">
    <div class="container nav__inner">
        <a href="{{ route('landing') }}" class="marca">
            <span class="marca__logo">{!! $ic('flame') !!}</span>
            <span>Serra Alerta</span>
        </a>
        <nav class="nav__links" id="navLinks" aria-label="Seções desta página">
            <a href="{{ route('landing') }}">Início</a>
            <a href="{{ route('ferramenta') }}" class="ativo" aria-current="page">Sobre a ferramenta</a>
            <a href="#video">Como usar</a>
            <a href="#objetivos">Objetivos</a>
            <a href="#funcionalidades">Funcionalidades</a>
            <a href="#hipoteses">Hipóteses</a>
            <a href="#mapas">Mapas</a>
            <a href="#prevencao">Prevenção</a>
            <a href="#referencias">Referências</a>
        </nav>
        <a href="{{ $apk }}" class="btn btn--brasa btn--sm nav__cta" target="_blank" rel="noopener">Baixar o app</a>
        <button class="nav__menu" id="navMenu" aria-label="Abrir menu">{!! $ic('menu') !!}</button>
    </div>
</header>

<main id="conteudo">

{{-- ===================== HERO ===================== --}}
<section class="hero hero--curto" id="topo">
    <div class="hero__brilho" aria-hidden="true"></div>
    <div class="container hero__texto">
        <span class="selo">{!! $ic('leaf') !!} Projeto de extensão · IFSP São João da Boa Vista</span>
        <h1>Sobre a ferramenta</h1>
        <p class="hero__lead">
            O <strong>Serra Alerta</strong> é um aplicativo colaborativo de monitoramento de queimadas na Serra da Paulista.
            Nesta página você encontra como usar o app, para que ele serve, os mapas da região e as fontes do projeto.
        </p>
        <div class="hero__acoes">
            <a href="{{ $apk }}" class="btn btn--brasa" target="_blank" rel="noopener">{!! $ic('download') !!} Baixar para Android</a>
            {{-- Versão para computador: pronta, escondida até o lançamento. Para exibir, remova o atributo hidden. --}}
            {{-- Continua disabled de propósito: ao exibir, remova também o disabled e o aria-disabled. --}}
            <button type="button" class="btn btn--desativado" disabled aria-disabled="true" aria-hidden="true" hidden>{!! $ic('monitor') !!} Versão para computador</button>
        </div>
        <p class="aviso-apk">Grátis · Android 7.0 ou mais novo · na instalação, permita instalar apps de fontes desconhecidas.</p>
    </div>
</section>

{{-- ===================== COMO USAR ===================== --}}
<section class="secao" id="video">
    <div class="container">
        <div class="cabeca revelar">
            <span class="rotulo">Passo a passo</span>
            <h2>Como usar o Serra Alerta</h2>
            <p>Veja a gravação de tela do app e siga os passos ao lado. Você não precisa de conta para registrar uma ocorrência.</p>
        </div>

        <div class="video-passos">
            <figure class="video-quadro revelar">
                @if ($video)
                    {{-- Player próprio: o JS (ferramenta.js) remove "controls" e liga os controles abaixo. Sem JS, valem os controles nativos. --}}
                    <div class="player" id="player" tabindex="0" role="region" aria-label="Vídeo de demonstração. Espaço reproduz ou pausa.">
                        <video id="videoUso" class="player__video" muted playsinline preload="metadata" controls @if ($poster) poster="{{ asset($poster) }}" @endif aria-label="Demonstração do Serra Alerta: gravação de tela do app, com os toques visíveis. O vídeo começa mudo e tem sons suaves de toque.">
                            <source src="{{ asset($video) }}" type="video/mp4">
                            Seu navegador não reproduz este vídeo. Use o botão “Baixar para Android” para instalar o app.
                        </video>

                        <div class="player__topo">
                            <span class="player__dica" id="dicaSom">Toque para ativar o som</span>
                            <button type="button" class="player__som" id="botaoSom" aria-pressed="false" aria-label="Ativar o som">
                                {!! $ic('volume-off', 'ic player__som-icone player__som-icone--mudo') !!}
                                {!! $ic('volume-on', 'ic player__som-icone player__som-icone--com-som') !!}
                            </button>
                        </div>

                        <button type="button" class="player__play" id="botaoPlay" aria-label="Reproduzir o vídeo">{!! $ic('play', 'ic ic-cheio') !!}</button>
                        <span class="player__flash player__flash--play" aria-hidden="true">{!! $ic('play', 'ic ic-cheio') !!}</span>
                        <span class="player__flash player__flash--pause" aria-hidden="true">{!! $ic('pause', 'ic ic-cheio') !!}</span>

                        <div class="player__barra" id="barra" role="slider" tabindex="0" aria-label="Posição do vídeo" aria-valuemin="0" aria-valuemax="0" aria-valuenow="0" aria-valuetext="0:00">
                            <span class="player__trilho"></span>
                            <span class="player__progresso"></span>
                            <span class="player__cursor"></span>
                        </div>

                        {{-- Estado final (classe player--fim, colocada pelo JS): aparece quando o vídeo termina. --}}
                        <div class="player__fim" role="group" aria-label="Fim do vídeo">
                            <button type="button" class="player__repetir" id="botaoRepetir" aria-label="Assistir de novo">
                                <span class="player__repetir-circulo">{!! $ic('replay') !!}</span>
                                <span class="player__repetir-rotulo">Assistir de novo</span>
                            </button>
                            {{-- Versão para computador: pronta, escondida até o lançamento. Para exibir, remova o atributo hidden. --}}
                            {{-- Continua disabled de propósito: ao exibir, remova também o disabled e o aria-disabled. --}}
                            <button type="button" class="player__app player__app--desativado" disabled aria-disabled="true" aria-hidden="true" hidden>{!! $ic('monitor') !!} Versão para computador</button>
                        </div>
                    </div>
                @else
                    <div class="video-vazio" role="img" aria-label="Vídeo em breve">
                        {!! $ic('video', 'ic video-vazio__icone') !!}
                        <strong>Vídeo em breve</strong>
                        <span>A gravação de tela do app será publicada aqui.</span>
                    </div>
                @endif
                @if ($video)
                    <figcaption>Vídeo de menos de um minuto. Toque no alto-falante para ligar o som.</figcaption>
                @else
                    <figcaption>Gravação de tela do app, com os toques visíveis.</figcaption>
                @endif
            </figure>

            <div class="revelar">
                <h3>Registrar uma ocorrência</h3>
                {{-- data-t: segundo do vídeo em que cada passo começa. data-fim: a partir daqui nenhum passo fica destacado. --}}
                <p class="passos-dica">Toque em um passo para ver o trecho no vídeo.</p>
                <ol class="passos-uso" data-fim="47.7">
                    <li data-t="4.2"><button type="button" class="passos-uso__botao"><strong>Abra o app.</strong> O mapa mostra os relatos da comunidade e os focos de calor do INPE.</button></li>
                    <li data-t="14.4"><button type="button" class="passos-uso__botao"><strong>Toque no botão da câmera</strong>, no centro da barra inferior (“Registrar ocorrência”).</button></li>
                    <li data-t="18.6"><button type="button" class="passos-uso__botao"><strong>Permita o uso da câmera e da localização</strong> quando o app pedir. Tire a foto da fumaça ou das chamas, a uma distância segura. Você pode tirar até 3 fotos.</button></li>
                    <li data-t="21.2"><button type="button" class="passos-uso__botao"><strong>Escolha o tipo</strong> da ocorrência: queima controlada, queimada irregular, incêndio florestal ou fumaça não identificada. Se quiser, escreva uma descrição curta.</button></li>
                    <li data-t="24.7"><button type="button" class="passos-uso__botao"><strong>Confira o local.</strong> Se o pino estiver longe de você, toque em “Ajustar no mapa”, arraste o mapa e toque em “Confirmar posição”.</button></li>
                    <li data-t="27.9"><button type="button" class="passos-uso__botao"><strong>Toque em “Registrar ocorrência”.</strong> Quando aparecer “Relato registrado”, toque em “Compartilhar relato” para enviar à Defesa Civil ou à brigada.</button></li>
                    <li data-t="34.4"><button type="button" class="passos-uso__botao"><strong>Volte ao mapa</strong> e veja o seu relato junto aos focos do INPE.</button></li>
                </ol>
                <p class="aviso aviso--compacto">{!! $ic('info') !!}<span><strong>Sem conta, sem cadastro.</strong> O app não pede login e não coleta dados pessoais identificáveis.</span></p>
            </div>
        </div>
    </div>
</section>

{{-- ===================== OBJETIVOS E PÚBLICO ===================== --}}
<section class="secao secao--areia" id="objetivos">
    <div class="container">
        <div class="cabeca revelar">
            <span class="rotulo">O que é e para quem é</span>
            <h2>Uma rede de vigilância feita pela comunidade.</h2>
            <p>O Serra Alerta transforma moradores, produtores rurais e ecoturistas em olhos atentos ao território, para que o fogo seja comunicado nos primeiros minutos.</p>
        </div>

        <div class="duas-colunas">
            <div class="revelar">
                <p>
                    As queimadas em São João da Boa Vista e na região ameaçam o patrimônio ambiental e a saúde pública.
                    O episódio mais recente registrado pela Defesa Civil mostra o risco: a queima inadequada de lixo se
                    espalhou pelo pasto e destruiu <strong>mais de 85 mil m² de área de preservação permanente</strong>
                    (Defesa Civil; G1, 2025).
                </p>
                <p>
                    O <strong>Serra Alerta</strong> é um aplicativo para celular que reúne a comunidade na fiscalização
                    contínua do território, com ênfase na Serra da Paulista. No mapa, a pessoa vê os relatos e registra
                    focos de fumaça ou fogo, classificados como queima controlada, queimada irregular ou incêndio florestal.
                </p>
                <p>
                    A prioridade é a Serra da Paulista e suas áreas de amortecimento. Os fragmentos florestais do município
                    estão classificados pelo Ministério do Meio Ambiente (Decreto nº 5.092/2004 e Portaria MMA nº 09/2007)
                    como Áreas Prioritárias para a Preservação da Biodiversidade de importância “Extremamente Alta”. A região
                    integra a Reserva da Biosfera da Mata Atlântica e a bacia do Rio Jaguari-Mirim.
                </p>
                <div class="aviso">
                    {!! $ic('info') !!}
                    <p><strong>Um foco registrado não é um incêndio confirmado.</strong> O relato é um alerta preliminar, para que a Defesa Civil e o Corpo de Bombeiros validem os dados e ajam com celeridade.</p>
                </div>
            </div>

            <div class="patrimonio revelar">
                <div class="patrimonio__bloco">
                    <h3>{!! $ic('users') !!} Quando usar, quem usa</h3>
                    <p><strong>Quando:</strong> ao identificar focos de queimadas ou colunas de fumaça em áreas urbanas e rurais, sobretudo em pastagens, margens de rodovias e regiões próximas à vegetação nativa da Serra da Paulista.</p>
                    <p><strong>Quem:</strong> moradores do entorno, ecoturistas e produtores rurais que presenciarem possíveis focos. Os dados também dão um panorama inicial para brigadistas e equipes da Defesa Civil.</p>
                </div>
                <div class="patrimonio__bloco">
                    <h3>{!! $ic('map') !!} Alcance e custos</h3>
                    <p><strong>Alcance:</strong> inicialmente o município de São João da Boa Vista (urbano e rural), com ênfase na Serra da Paulista. A arquitetura permite, no futuro, adaptar o app a outros municípios da região.</p>
                    <p><strong>Custos:</strong> protótipo acadêmico, com custo reduzido. Roda em Android, nos smartphones que as pessoas já têm, e pode usar serviços gratuitos ou de baixo custo.</p>
                </div>
            </div>
        </div>

        <h3 class="subtitulo-bloco">Objetivos</h3>
        <div class="passos">
            <article class="passo revelar">
                <span class="passo__num">1</span>
                <div class="passo__icone">{!! $ic('phone') !!}</div>
                <h3>Protótipo funcional</h3>
                <p>Desenvolver um aplicativo móvel para detectar e registrar, com localização, focos de queimada na região de São João da Boa Vista.</p>
            </article>
            <article class="passo revelar">
                <span class="passo__num">2</span>
                <div class="passo__icone">{!! $ic('map') !!}</div>
                <h3>Mapa em tempo real</h3>
                <p>Mostrar os incidentes reportados em um mapa público, com visualização imediata e sem exigir cadastro ou autenticação.</p>
            </article>
            <article class="passo revelar">
                <span class="passo__num">3</span>
                <div class="passo__icone">{!! $ic('shield') !!}</div>
                <h3>Dados para quem combate</h3>
                <p>Reunir as ocorrências de forma visual, com dados geográficos úteis à Defesa Civil e ao Corpo de Bombeiros, no combate e na prevenção.</p>
            </article>
        </div>

        <div class="porque revelar">
            <h3>Por que importa</h3>
            <ul>
                <li><strong>O problema é socioambiental.</strong> A proliferação de queimadas destrói a fauna e a flora nativas e compromete a segurança.</li>
                <li><strong>A solução é tecnológica.</strong> Um relato com geolocalização e foto enviado na hora dá agilidade nos minutos iniciais do foco.</li>
                <li><strong>O monitoramento se democratiza.</strong> Quem tem um smartphone pode contribuir para uma rede colaborativa de preservação ambiental.</li>
            </ul>
            <p class="porque__destaque">Em resumo: o Serra Alerta transforma a população em uma rede ágil de vigilância, capaz de alertar focos antes que atinjam proporções incontroláveis.</p>
        </div>
    </div>
</section>

{{-- ===================== FUNCIONALIDADES ===================== --}}
<section class="secao" id="funcionalidades">
    <div class="container">
        <div class="cabeca revelar">
            <span class="rotulo">Funcionalidades</span>
            <h2>Simples para quem reporta. Útil para quem combate.</h2>
        </div>

        <div class="dicas dicas--tres">
            <article class="dica revelar">
                {!! $ic('map') !!}
                <h3>Mapa de focos em tempo real</h3>
                <p>Tela principal com mapa OpenStreetMap e marcadores georreferenciados. Toque em um marcador para ver as informações básicas da ocorrência.</p>
            </article>
            <article class="dica revelar">
                {!! $ic('noaccount') !!}
                <h3>Denúncia expressa sem conta</h3>
                <p>Fluxo simples, sem cadastro nem autenticação. Menos etapas, para que mais pessoas participem.</p>
            </article>
            <article class="dica revelar">
                {!! $ic('pin') !!}
                <h3>Geolocalização automática</h3>
                <p>O GPS do celular captura as coordenadas no momento do registro e posiciona a ocorrência no mapa.</p>
            </article>
            <article class="dica revelar">
                {!! $ic('camera') !!}
                <h3>Registro fotográfico e descrição</h3>
                <p>Foto tirada pela câmera do app, com um campo de texto para informações complementares.</p>
            </article>
            <article class="dica revelar">
                {!! $ic('target') !!}
                <h3>Áreas de ocorrência</h3>
                <p>Com a soma dos registros, o sistema pode mostrar regiões com mais ocorrências, para analisar padrões e planejar a prevenção.</p>
            </article>
            <article class="dica revelar">
                {!! $ic('phone') !!}
                <h3>Protótipo testado em campo</h3>
                <p>Testes funcionais e, quando possível, testes em pontos estratégicos da Serra da Paulista: localização, registro, envio de imagens e visualização no mapa.</p>
            </article>
        </div>

        <div class="tec-bloco revelar">
            <h3>Tecnologias do protótipo</h3>
            <p>
                Kotlin com interface em Jetpack Compose, para Android. A localização vem do GPS do celular, por meio do
                FusedLocationProviderClient, e o mapa usa OpenStreetMap, pela biblioteca OSMdroid. Fotos, descrições,
                localização e data e hora ficam salvos no próprio aparelho, em banco SQLite com a biblioteca Room.
                Assim, o fluxo completo pode ser testado sem servidor e sem custo de infraestrutura.
            </p>
            <div class="stack">
                <span>Kotlin</span>
                <span>Jetpack Compose</span>
                <span>OpenStreetMap · OSMdroid</span>
                <span>FusedLocationProviderClient</span>
                <span>Room · SQLite</span>
            </div>
            <p class="tec-bloco__futuro">
                <strong>Próxima etapa:</strong> migrar os dados para uma base centralizada, com API REST e banco de dados
                geográfico, para a visão colaborativa entre diferentes usuários.
            </p>
            <a class="link-codigo" href="{{ $repositorio }}/tree/main/app-android" target="_blank" rel="noopener">{!! $ic('code') !!} Ver o código do app no GitHub</a>
        </div>
    </div>
</section>

{{-- ===================== MAPAS ===================== --}}
<section class="secao secao--areia" id="mapas">
    <div class="container">
        <div class="cabeca revelar">
            <span class="rotulo">Mapas e patrimônio</span>
            <h2>Onde o fogo ameaça</h2>
            <p>A Serra da Paulista concentra áreas prioritárias para a preservação, além de fazendas históricas e cursos d’água que podem ser atingidos pelo fogo.</p>
        </div>

        <div class="duas-colunas">
            <figure class="figura revelar">
                <img src="{{ asset('img/mapas/mapa1-areas-prioritarias.png') }}" width="715" height="453" loading="lazy" alt="Mapa de imagem de satélite de São João da Boa Vista com as Áreas Prioritárias para Preservação. A região da Serra da Paulista aparece em vermelho, na categoria Extremamente Alta, e há manchas em laranja, na categoria Muito Alta.">
                <figcaption>
                    <strong>MAPA 1 — Áreas Prioritárias para Preservação — Categoria de Importância Extremamente Alta.</strong>
                    <span class="figura__fonte">Fonte: Associação Viva São João / Ministério do Meio Ambiente (2018).</span>
                </figcaption>
            </figure>

            <div class="patrimonio revelar">
                <div class="patrimonio__bloco">
                    <h3>{!! $ic('leaf') !!} Fazendas históricas e de relevância ecológica</h3>
                    <div class="tags">
                        @foreach ($fazendas as $f)<span class="tag">{{ $f }}</span>@endforeach
                    </div>
                </div>
                <div class="patrimonio__bloco">
                    <h3>{!! $ic('droplet') !!} Cursos d’água e microbacias estratégicas</h3>
                    <div class="tags">
                        @foreach ($corregos as $c)<span class="tag tag--agua">{{ $c }}</span>@endforeach
                    </div>
                </div>
                <p class="fonte">Patrimônios sob risco iminente de queimadas na área de estudo. Fonte: Associação Viva São João / MMA (2018).</p>
            </div>
        </div>
    </div>
</section>

{{-- ===================== HIPÓTESES E SÍNTESE ===================== --}}
<section class="secao" id="hipoteses">
    <div class="container">
        <div class="cabeca revelar">
            <span class="rotulo">Hipóteses</span>
            <h2>Duas explicações para os focos observados.</h2>
            <p>As hipóteses orientam a análise espacial dos dados. Os mapas e gráficos abaixo mostram o que os satélites registraram em setembro.</p>
        </div>

        <div class="hipoteses">
            <article class="hipotese revelar">
                <div class="hipotese__icone">{!! $ic('sun') !!}</div>
                <div>
                    <span class="rotulo">Hipótese ambiental</span>
                    <h3>A concentração e o alastramento das queimadas estão ligados ao acúmulo de vegetação e pasto seco na estiagem.</h3>
                    <p>O pico de 2020 coincide com o alerta de emergência por umidade relativa crítica. Em 2025, o incêndio de 10/09 avançou rápido pelo capim seco.</p>
                </div>
            </article>
            <article class="hipotese revelar">
                <div class="hipotese__icone">{!! $ic('users') !!}</div>
                <div>
                    <span class="rotulo">Hipótese socioespacial</span>
                    <h3>A concentração de ocorrências em pastagens e margens de estradas decorre de pequenas interferências humanas que crescem sem aviso.</h3>
                    <p>Essas interferências evoluem sem controle nos minutos iniciais, pela falta de comunicação ágil. Os focos surgem em pontos distintos da zona rural, não como a propagação de um único incêndio.</p>
                </div>
            </article>
        </div>

        <h3 class="subtitulo-bloco">Síntese analítica</h3>

        <div class="duas-colunas sintese">
            <figure class="figura revelar">
                <img src="{{ asset('img/mapas/mapa2-focos-setembro-2020.png') }}" width="1014" height="648" loading="lazy" alt="Mapa do município de São João da Boa Vista com os focos de calor de setembro de 2020. Há grande concentração ao norte da sede, com marcadores que vão do amarelo ao vermelho escuro conforme a data da detecção.">
                <figcaption>
                    <strong>MAPA 2 — Focos de calor, setembro de 2020 (332 focos, 1 a 30 de setembro de 2020, sobre o contorno do município e dos limítrofes; gradação do amarelo ao carmim situa cada ocorrência no tempo).</strong>
                    <span class="figura__fonte">Fonte: desenvolvido pelos autores a partir dos dados do IBGE e BDQueimadas/INPE.</span>
                </figcaption>
            </figure>
            <div class="revelar">
                <h3 class="subtitulo-texto">2020: um grande evento</h3>
                <p>
                    Os focos se concentraram entre 8 e 10 de setembro (<strong>264 dos 332</strong>), período que coincide
                    com o alerta de emergência de incêndio florestal emitido pela Defesa Civil estadual. Uma massa de ar
                    seco e quente reduziu a umidade relativa a níveis críticos em boa parte de São Paulo.
                </p>
                <p>
                    <strong>Sustenta a Hipótese ambiental:</strong> o acúmulo de pasto e vegetação seca na estiagem criou
                    condições para o rápido alastramento do fogo. O padrão também é compatível com a Hipótese socioespacial:
                    poucos pontos de ignição, possivelmente ligados a queimadas de pasto ou ao descarte de lixo às margens de vias.
                </p>
                <p>
                    Os focos formam núcleos densos e restritos, no entorno da sede e em uma segunda área ao sul, já em domínio
                    de Cerrado. O tamanho dos marcadores, proporcional à potência radiativa do fogo (FRP), mostra que os focos
                    mais intensos estavam nesses núcleos: incêndios em propagação avançada, e não pequenas queimadas controladas.
                </p>
            </div>
        </div>

        <div class="duas-colunas sintese">
            <figure class="figura revelar">
                <img src="{{ asset('img/mapas/mapa3-focos-setembro-2025.png') }}" width="1092" height="705" loading="lazy" alt="Mapa do município de São João da Boa Vista com os 38 focos de calor de setembro de 2025, dispersos em núcleos a oeste e ao sul. O círculo tracejado marca a área do incêndio de 10 de setembro de 2025, de cerca de 12,8 hectares.">
                <figcaption>
                    <strong>MAPA 3 — Focos de calor em São João da Boa Vista, setembro de 2025 (38 focos, 1 a 30 de setembro de 2025, BDQueimadas/INPE).</strong>
                    <span class="figura__fonte">Fonte: desenvolvido pelos autores a partir dos dados do IBGE, BDQueimadas/INPE e Prefeitura Municipal de São João da Boa Vista.</span>
                </figcaption>
            </figure>
            <div class="revelar">
                <h3 class="subtitulo-texto">2025: focos dispersos</h3>
                <p>
                    Em comparação com 2020, setembro de 2025 teve ocorrências menores e espalhadas pelo território, em núcleos
                    isolados, sobretudo a oeste e ao sul do município. <strong>82% dos focos</strong> estão em áreas de Cerrado.
                </p>
                <p>
                    <strong>Sustenta a Hipótese socioespacial:</strong> os focos surgem em pontos diferentes da zona rural ao
                    longo do mês, o que sugere ignições pontuais de origem humana, e não a propagação de um único incêndio.
                </p>
                <p>
                    <strong>Caso mais emblemático, 10 de setembro:</strong> focos detectados por satélite às 14h13 e às 14h34
                    coincidem em data, horário e local com o incêndio registrado pela Defesa Civil perto do Ribeirão dos Porcos
                    e da linha férrea. O fogo, provavelmente iniciado às margens da ferrovia, se espalhou pelo capim seco e
                    consumiu cerca de <strong>12,8 hectares</strong> de pastagem, bananal e mata nativa.
                </p>
                <p>
                    <strong>Sustenta também a Hipótese ambiental:</strong> a vegetação seca e a baixa umidade do ar explicam a
                    rapidez do avanço. O episódio mostra que os primeiros minutos são decisivos: um alerta imediato da
                    comunidade poderia antecipar o acionamento das equipes e reduzir a área atingida.
                </p>
            </div>
        </div>

        <div class="graficos">
            @foreach ($anosFocos as $g)
                @php($resumoGrafico = collect($g['dados'])->map(fn ($d) => $d['periodo'].': '.$d['valor'].' focos')->implode('; '))
                <figure class="grafico revelar">
                    <figcaption>
                        <span class="grafico__ano">Setembro {{ $g['ano'] }}</span>
                        <span class="grafico__total"><strong>{{ array_sum(array_column($g['dados'], 'valor')) }}</strong> focos</span>
                    </figcaption>
                    <div class="barras" role="img" aria-label="Focos de calor por período em setembro de {{ $g['ano'] }}. {{ $resumoGrafico }}.">
                        @foreach ($g['dados'] as $d)
                            <div class="barra">
                                <span class="barra__valor">{{ $d['valor'] }}</span>
                                <span class="barra__coluna" style="--h: {{ max(1.5, $d['valor'] / $maxFocos * 100) }}%"></span>
                                <span class="barra__rotulo">{{ $d['periodo'] }}</span>
                            </div>
                        @endforeach
                    </div>
                </figure>
            @endforeach
        </div>
        <p class="fonte">Comparativo de focos de calor por período de setembro, na mesma escala. Fontes: IBGE; BDQueimadas/INPE; Prefeitura Municipal de São João da Boa Vista (2025).</p>
    </div>
</section>

{{-- ===================== PREVENÇÃO ===================== --}}
<section class="secao secao--areia" id="prevencao">
    <div class="container">
        <div class="cabeca revelar">
            <span class="rotulo">Educação ambiental e prevenção</span>
            <h2>Menos fogo começa antes do fogo.</h2>
            <p>Medidas baseadas na Política Nacional de Manejo Integrado do Fogo (Lei Federal nº 14.944/2024), em três dimensões.</p>
        </div>
        <div class="dicas dicas--tres">
            <article class="dica revelar">
                {!! $ic('leaf') !!}
                <h3>Ambiental</h3>
                <p>O app não atua fisicamente no meio ambiente, mas a maioria dos focos começa por ação humana. Com aviso rápido, as chamas são combatidas nos minutos iniciais, o que reduz a área queimada, a perda de animais e a destruição da mata nativa e das nascentes da Serra da Paulista.</p>
            </article>
            <article class="dica revelar">
                {!! $ic('map') !!}
                <h3>Territorial</h3>
                <p>Com os relatos no mapa, fica claro quais áreas se repetem ao longo do tempo. Defesa Civil e órgãos de fiscalização planejam melhor a atuação, sobretudo nos meses mais secos.</p>
            </article>
            <article class="dica revelar">
                {!! $ic('users') !!}
                <h3>Social e institucional</h3>
                <p>Aproximar a população dos órgãos públicos, com integração direta entre moradores e Defesa Civil. No futuro, pode envolver o Corpo de Bombeiros, brigadas voluntárias e conselhos municipais de meio ambiente. Sem conta, qualquer morador, produtor ou turista envia a foto e a localização exata em poucos segundos.</p>
            </article>
        </div>
        <div class="emergencia revelar">
            <div>
                <h3>Canais de emergência</h3>
                <p>O Serra Alerta não substitui o acionamento oficial. Em caso de risco a pessoas, animais ou casas:</p>
            </div>
            <a href="tel:193" class="tel"><span>193</span>Corpo de Bombeiros</a>
            <a href="tel:199" class="tel"><span>199</span>Defesa Civil</a>
            <a href="tel:190" class="tel"><span>190</span>Polícia Militar</a>
        </div>
    </div>
</section>

{{-- ===================== REFERÊNCIAS ===================== --}}
<section class="secao secao--escura" id="referencias">
    <div class="container">
        <div class="cabeca revelar">
            <span class="rotulo rotulo--claro">Documentação acadêmica</span>
            <h2>Referências</h2>
        </div>
        <ul class="refs-lista revelar">
            <li><a href="https://vivasaojoao.org/wp-content/uploads/2018/08/Contribuic%CC%A7o%CC%83es-para-o-Plano-Diretor-SJBV-VIVA-S%C3%83O-JO%C3%83O.pdf" target="_blank" rel="noopener">ASSOCIAÇÃO VIVA SÃO JOÃO. Contribuições para o Plano Diretor de São João da Boa Vista. 2018.</a></li>
            <li><a href="https://www.planalto.gov.br/ccivil_03/_ato2023-2026/2024/lei/l14944.htm" target="_blank" rel="noopener">BRASIL. Lei nº 14.944, de 31 de julho de 2024 (Política Nacional de Manejo Integrado do Fogo).</a></li>
            <li><a href="https://g1.globo.com/sp/sao-carlos-regiao/videos-jornal-da-eptv-1-edicao/video/incendio-destroi-85-mil-metros-quadrados-de-area-de-preservacao-em-sao-joao-da-boa-vista-13793947.ghtml" target="_blank" rel="noopener">G1. Incêndio destrói 85 mil metros quadrados de área de preservação em São João da Boa Vista. 2025.</a></li>
            <li><a href="https://developers.google.com/maps/documentation/android-sdk/overview" target="_blank" rel="noopener">GOOGLE. Maps SDK for Android.</a></li>
            <li><a href="https://www.openstreetmap.org" target="_blank" rel="noopener">OPENSTREETMAP CONTRIBUTORS. OpenStreetMap Planet Dump. 2026.</a></li>
            <li><a href="https://www.saojoao.sp.gov.br/noticias/destaques/defesa-civil-combate-incendio-que-atingiu-128-hectares-na-zona-rural-de-sao-joao" target="_blank" rel="noopener">SÃO JOÃO DA BOA VISTA (Município). Defesa Civil combate incêndio que atingiu 12,8 hectares na zona rural de São João. 11 set. 2025.</a></li>
            <li><a href="https://terrabrasilis.dpi.inpe.br/queimadas/bdqueimadas/#mapa" target="_blank" rel="noopener">INPE. BDQueimadas: banco de dados de queimadas. 2026.</a></li>
        </ul>
    </div>
</section>

{{-- ===================== CTA FINAL ===================== --}}
<section class="final">
    <div class="container final__inner revelar">
        <h2>A Serra da Paulista precisa de mais olhos.</h2>
        <p>Cada relato encurta o caminho entre a primeira fumaça e a primeira equipe em campo.</p>
        <a href="{{ $apk }}" class="btn btn--brasa" target="_blank" rel="noopener">{!! $ic('download') !!} Baixar para Android</a>
        {{-- Versão para computador: pronta, escondida até o lançamento. Para exibir, remova o atributo hidden. --}}
        {{-- Continua disabled de propósito: ao exibir, remova também o disabled e o aria-disabled. --}}
        <button type="button" class="btn btn--desativado" disabled aria-disabled="true" aria-hidden="true" hidden>{!! $ic('monitor') !!} Versão para computador</button>
        <a href="{{ $repositorio }}" class="btn btn--vidro" target="_blank" rel="noopener">{!! $ic('code') !!} Código no GitHub</a>
        <p class="aviso-apk">Grátis · Android 7.0 ou mais novo · na instalação, permita instalar apps de fontes desconhecidas.</p>
    </div>
</section>

</main>

<footer class="rodape">
    <div class="container rodape__grid">
        <div>
            <a href="{{ route('landing') }}" class="marca">
                <span class="marca__logo">{!! $ic('flame') !!}</span>
                <span>Serra Alerta</span>
            </a>
            <p>Projetos de Extensão — Documento-síntese, edição 2026/2. Instituto Federal de Educação, Ciência e Tecnologia de São Paulo, Câmpus São João da Boa Vista.</p>
            <div class="rodape__links">
                <a href="{{ route('ferramenta') }}">Sobre a ferramenta (vídeo e documento-síntese)</a>
                <a href="{{ $apk }}" target="_blank" rel="noopener">Baixar o app (APK)</a>
                <a href="{{ $repositorio }}/tree/main/app-android" target="_blank" rel="noopener">Código do app Android</a>
                <a href="https://terrabrasilis.dpi.inpe.br/queimadas/portal/" target="_blank" rel="noopener">Portal BDQueimadas (INPE)</a>
            </div>
        </div>
        <div>
            <h4>Referências</h4>
            <ul class="refs">
                <li>BRASIL. Lei nº 14.944, de 31 de julho de 2024 — Política Nacional de Manejo Integrado do Fogo.</li>
                <li>INPE. BDQueimadas: banco de dados de queimadas, 2026.</li>
                <li>ASSOCIAÇÃO VIVA SÃO JOÃO. Contribuições para o Plano Diretor de São João da Boa Vista, 2018.</li>
                <li>SÃO JOÃO DA BOA VISTA. Defesa Civil combate incêndio que atingiu 12,8 hectares na zona rural, 2025.</li>
                <li>G1. Incêndio destrói 85 mil m² de área de preservação em São João da Boa Vista, 2025.</li>
                <li>OPENSTREETMAP CONTRIBUTORS. OpenStreetMap, 2026.</li>
            </ul>
        </div>
    </div>
    <div class="container rodape__base">
        <span>© {{ date('Y') }} Equipe Serra Alerta · IFSP</span>
        <span>Mapas © OpenStreetMap contributors · Focos de calor: INPE/BDQueimadas</span>
    </div>
</footer>

<script src="{{ asset('js/ferramenta.js') }}"></script>
</body>
</html>
