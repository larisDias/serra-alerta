@php
    // Ícones em linha (traço 2px, estilo Lucide) para não depender de biblioteca externa.
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
        'database' => '<ellipse cx="12" cy="5" rx="9" ry="3"/><path d="M3 5v14a9 3 0 0 0 18 0V5"/><path d="M3 12a9 3 0 0 0 18 0"/>',
        'satellite' => '<path d="m13 7-4-4-4 4 4 4"/><path d="m17 11 4 4-4 4-4-4"/><path d="m8 12 4 4 6-6-4-4Z"/><path d="m16 8 3-3"/><path d="M9 21a6 6 0 0 0-6-6"/>',
        'clock' => '<circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/>',
        'info' => '<circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/>',
        'wind' => '<path d="M17.7 7.7a2.5 2.5 0 1 1 1.8 4.3H2"/><path d="M9.6 4.6A2 2 0 1 1 11 8H2"/><path d="M12.6 19.4A2 2 0 1 0 14 16H2"/>',
        'menu' => '<path d="M4 6h16M4 12h16M4 18h16"/>',
        'book' => '<path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>',
    ];
    $ic = fn (string $nome, string $classe = 'ic') => '<svg class="'.$classe.'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">'.$icones[$nome].'</svg>';
    $maxFocos = max(array_column(array_merge($focos2020, $focos2025), 'valor'));
@endphp
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Serra Alerta — monitoramento colaborativo de queimadas</title>
    <meta name="description" content="Aplicativo colaborativo para reportar focos de fumaça e fogo em São João da Boa Vista e na Serra da Paulista. Projeto de extensão do IFSP.">
    <meta name="theme-color" content="#171412">
    <link rel="icon" href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 32 32'%3E%3Crect width='32' height='32' rx='8' fill='%23D9482B'/%3E%3Cpath fill='white' d='M16 5s8 6 8 13a8 8 0 0 1-16 0c0-4 2-6 4-8 0 3 2 5 4 5-1-4 0-7 0-10z'/%3E%3C/svg%3E">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Bricolage+Grotesque:opsz,wght@12..96,500;12..96,700;12..96,800&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css">
    <link rel="stylesheet" href="{{ asset('css/landing.css') }}">
</head>
<body>

<header class="nav" id="nav">
    <div class="container nav__inner">
        <a href="#inicio" class="marca">
            <span class="marca__logo">{!! $ic('flame') !!}</span>
            <span>Serra Alerta</span>
        </a>
        <nav class="nav__links" id="navLinks">
            <a href="#problema">O problema</a>
            <a href="#como">Como funciona</a>
            <a href="#mapa">Mapa</a>
            <a href="#dados">Dados</a>
            <a href="#prevencao">Prevenção</a>
            <a href="#equipe">Equipe</a>
        </nav>
        <a href="{{ route('app.demo') }}" class="btn btn--brasa btn--sm nav__cta" target="_blank">Abrir o app</a>
        <button class="nav__menu" id="navMenu" aria-label="Abrir menu">{!! $ic('menu') !!}</button>
    </div>
</header>

{{-- ===================== HERO ===================== --}}
<section class="hero" id="inicio">
    <div class="hero__brilho" aria-hidden="true"></div>
    <div class="hero__brasas" aria-hidden="true">
        @for ($i = 0; $i < 18; $i++)<span style="--x: {{ rand(0, 100) }}%; --d: {{ rand(6, 14) }}s; --a: {{ rand(0, 80) / 10 }}s; --s: {{ rand(3, 7) }}px"></span>@endfor
    </div>
    <div class="container hero__grid">
        <div class="hero__texto">
            <span class="selo">{!! $ic('leaf') !!} Projeto de extensão · IFSP São João da Boa Vista</span>
            <h1>Viu fumaça na Serra?<br><em>Avise em segundos.</em></h1>
            <p class="hero__lead">
                O <strong>Serra Alerta</strong> transforma moradores, produtores rurais e ecoturistas numa rede de
                vigilância contra queimadas: foto, GPS automático e mapa em tempo real — <strong>sem cadastro</strong>.
            </p>
            <div class="hero__acoes">
                <a href="#como" class="btn btn--brasa">Ver como funciona {!! $ic('arrow') !!}</a>
                <a href="#problema" class="btn btn--vidro">Entenda o problema</a>
            </div>
            <ul class="hero__pontos">
                <li>{!! $ic('noaccount') !!} Sem conta</li>
                <li>{!! $ic('pin') !!} GPS automático</li>
                <li>{!! $ic('camera') !!} Foto do foco</li>
            </ul>
        </div>

        <div class="hero__celular">
            <div class="celular">
                <div class="celular__notch"></div>
                <iframe src="{{ route('app.demo', ['embed' => 1]) }}" title="Demonstração do app Serra Alerta" loading="lazy"></iframe>
            </div>
            <div class="flutuante flutuante--a">
                <span class="ponto ponto--incendio"></span>
                <div><strong>Novo relato</strong><small>Serra da Paulista · agora</small></div>
            </div>
            <div class="flutuante flutuante--b">
                {!! $ic('clock') !!}
                <div><strong>&lt; 30 s</strong><small>para reportar um foco</small></div>
            </div>
            <p class="hero__dica">Protótipo interativo — toque em “Reportar foco”</p>
        </div>
    </div>
</section>

{{-- ===================== NÚMEROS ===================== --}}
<section class="faixa-numeros">
    <div class="container numeros">
        <div class="numero revelar">
            <strong data-contar="332">0</strong>
            <span>focos de calor em setembro de 2020 no município</span>
        </div>
        <div class="numero revelar">
            <strong data-contar="264">0</strong>
            <span>deles concentrados em apenas 3 dias (8 a 10/09)</span>
        </div>
        <div class="numero revelar">
            <strong><span data-contar="85">0</span> mil m²</strong>
            <span>de APP destruídos por uma queima de lixo (Defesa Civil, 2025)</span>
        </div>
        <div class="numero revelar">
            <strong><span data-contar="12.8" data-decimais="1">0</span> ha</strong>
            <span>consumidos em um único incêndio em 10/09/2025</span>
        </div>
    </div>
</section>

{{-- ===================== PROBLEMA ===================== --}}
<section class="secao" id="problema">
    <div class="container duas-colunas">
        <div class="revelar">
            <span class="rotulo">O problema</span>
            <h2>Um patrimônio de importância <em>“Extremamente Alta”</em> ameaçado pelo fogo.</h2>
            <p>
                A Serra da Paulista e suas áreas de amortecimento integram as <strong>Áreas Prioritárias para a
                Preservação da Biodiversidade</strong> de importância “Extremamente Alta” do Ministério do Meio Ambiente
                (Decreto nº 5.092/2004 e Portaria MMA nº 09/2007), a Reserva da Biosfera da Mata Atlântica e a bacia do
                Rio Jaguari-Mirim.
            </p>
            <p>
                Mesmo assim, as queimadas são recorrentes. No episódio mais recente registrado pela Defesa Civil, a
                queima inadequada de lixo alastrou-se pela vegetação de pasto e destruiu mais de <strong>85 mil m² de área
                de preservação permanente</strong>.
            </p>
        </div>
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
            <p class="fonte">Fonte: Associação Viva São João / MMA (2018).</p>
        </div>
    </div>
</section>

{{-- ===================== PRIMEIROS MINUTOS ===================== --}}
<section class="secao secao--escura" id="minutos">
    <div class="container">
        <div class="cabeca revelar">
            <span class="rotulo rotulo--claro">10 de setembro de 2025</span>
            <h2>Os primeiros minutos decidem o tamanho do estrago.</h2>
            <p>
                O incêndio perto do Ribeirão dos Porcos começou, provavelmente, às margens da linha férrea. Quando o
                satélite o detectou, o fogo já corria pelo capim seco.
            </p>
        </div>

        <ol class="linha-tempo revelar">
            <li>
                <span class="linha-tempo__marca">{!! $ic('flame') !!}</span>
                <small>Início</small>
                <strong>Ignição à margem da ferrovia</strong>
                <p>Uma pequena interferência humana em vegetação seca.</p>
            </li>
            <li>
                <span class="linha-tempo__marca">{!! $ic('wind') !!}</span>
                <small>Minutos seguintes</small>
                <strong>Propagação pelo capim seco</strong>
                <p>Baixa umidade do ar e ninguém para avisar.</p>
            </li>
            <li>
                <span class="linha-tempo__marca">{!! $ic('satellite') !!}</span>
                <small>14h13 e 14h34</small>
                <strong>Satélite detecta os focos</strong>
                <p>O alerta oficial só chega com o incêndio já estabelecido.</p>
            </li>
            <li class="linha-tempo__fim">
                <span class="linha-tempo__marca">{!! $ic('target') !!}</span>
                <small>Resultado</small>
                <strong>12,8 hectares queimados</strong>
                <p>Pastagem, bananal e mata nativa.</p>
            </li>
        </ol>

        <div class="virada revelar">
            <div class="virada__icone">{!! $ic('phone') !!}</div>
            <div>
                <h3>Com o Serra Alerta, quem vê a fumaça avisa primeiro.</h3>
                <p>
                    Um relato georreferenciado, com foto, feito por quem está perto, pode chegar à Defesa Civil e ao Corpo
                    de Bombeiros <strong>antes</strong> do satélite, quando o fogo ainda é pequeno.
                </p>
            </div>
        </div>
    </div>
</section>

{{-- ===================== COMO FUNCIONA ===================== --}}
<section class="secao" id="como">
    <div class="container">
        <div class="cabeca cabeca--centro revelar">
            <span class="rotulo">Como funciona</span>
            <h2>Três toques entre ver a fumaça e colocar o foco no mapa.</h2>
        </div>
        <div class="passos">
            <article class="passo revelar">
                <span class="passo__num">1</span>
                <div class="passo__icone">{!! $ic('flame') !!}</div>
                <h3>Toque em “Reportar foco”</h3>
                <p>Sem login, sem cadastro. Escolha se é fumaça ou fogo e o tipo provável: queimada controlada, irregular ou incêndio florestal.</p>
            </article>
            <article class="passo revelar">
                <span class="passo__num">2</span>
                <div class="passo__icone">{!! $ic('camera') !!}</div>
                <h3>Foto e localização automáticas</h3>
                <p>O GPS do celular captura as coordenadas. Se o foco estiver longe, arraste o mapa e marque o ponto exato.</p>
            </article>
            <article class="passo revelar">
                <span class="passo__num">3</span>
                <div class="passo__icone">{!! $ic('map') !!}</div>
                <h3>O alerta aparece no mapa</h3>
                <p>O relato entra no mapa público e ajuda Defesa Civil e Bombeiros a validar a ocorrência e agir com rapidez.</p>
            </article>
        </div>
        <div class="aviso revelar">
            {!! $ic('info') !!}
            <p><strong>Relato é um alerta preliminar.</strong> A indicação de fumaça ou foco de calor não equivale automaticamente a um incêndio florestal: ela serve para que os órgãos competentes validem e ajam com celeridade.</p>
        </div>
    </div>
</section>

{{-- ===================== FUNCIONALIDADES ===================== --}}
<section class="secao secao--areia" id="recursos">
    <div class="container">
        <div class="cabeca revelar">
            <span class="rotulo">O que o app faz</span>
            <h2>Simples para quem reporta. Útil para quem combate.</h2>
        </div>
        <div class="recursos">
            <article class="recurso recurso--destaque revelar">
                <div class="recurso__icone">{!! $ic('map') !!}</div>
                <h3>Mapa de focos em tempo real</h3>
                <p>Tela principal com mapa OpenStreetMap e marcadores coloridos por tipo. Toque em um foco para ver descrição, foto, horário e coordenadas.</p>
                <div class="legenda-tipos">
                    <span><i class="ponto ponto--controlada"></i>Controlada</span>
                    <span><i class="ponto ponto--irregular"></i>Irregular</span>
                    <span><i class="ponto ponto--incendio"></i>Incêndio</span>
                </div>
            </article>
            <article class="recurso revelar">
                <div class="recurso__icone">{!! $ic('noaccount') !!}</div>
                <h3>Denúncia expressa sem conta</h3>
                <p>Nenhuma barreira de cadastro ou autenticação. Qualquer pessoa pode reportar em poucos segundos.</p>
            </article>
            <article class="recurso revelar">
                <div class="recurso__icone">{!! $ic('pin') !!}</div>
                <h3>Geolocalização automática</h3>
                <p>Coordenadas obtidas pelo GPS do aparelho no momento do registro, com opção de marcar manualmente no mapa.</p>
            </article>
            <article class="recurso revelar">
                <div class="recurso__icone">{!! $ic('camera') !!}</div>
                <h3>Foto e descrição sumária</h3>
                <p>Integração com a câmera ou galeria e um campo curto de texto para detalhes como direção do vento.</p>
            </article>
            <article class="recurso revelar">
                <div class="recurso__icone">{!! $ic('target') !!}</div>
                <h3>Mapeamento das áreas de ocorrência</h3>
                <p>Os relatos são agrupados em núcleos, revelando as regiões com maior repetição de queimadas ao longo do tempo.</p>
            </article>
            <article class="recurso recurso--faixa revelar">
                <div class="recurso__icone">{!! $ic('call') !!}</div>
                <div>
                    <h3>Atalhos de emergência</h3>
                    <p>Ligação direta para Bombeiros (193) e Defesa Civil (199) a partir de qualquer relato. O app complementa, e nunca substitui, o acionamento oficial.</p>
                </div>
            </article>
        </div>
    </div>
</section>

{{-- ===================== MAPA AO VIVO ===================== --}}
<section class="secao" id="mapa">
    <div class="container">
        <div class="cabeca cabeca--linha revelar">
            <div>
                <span class="rotulo">Mapa colaborativo</span>
                <h2>Os relatos da comunidade, ao vivo.</h2>
                <p>Este mapa lê a mesma base usada pelo app. Envie um relato pelo celular ao lado, no topo da página, e veja-o aparecer aqui.</p>
            </div>
            <div class="filtros" id="filtrosMapa" role="group" aria-label="Filtrar por tipo">
                <button class="filtro ativo" data-tipo="">Todos <b data-cont="">{{ $totalRelatos }}</b></button>
                <button class="filtro" data-tipo="controlada"><i class="ponto ponto--controlada"></i>Controlada <b data-cont="controlada">–</b></button>
                <button class="filtro" data-tipo="irregular"><i class="ponto ponto--irregular"></i>Irregular <b data-cont="irregular">–</b></button>
                <button class="filtro" data-tipo="incendio"><i class="ponto ponto--incendio"></i>Incêndio <b data-cont="incendio">–</b></button>
            </div>
        </div>
        <div class="mapa-moldura revelar">
            <div id="mapaPublico" class="mapa" data-api="{{ route('api.relatos.index') }}"></div>
            <aside class="mapa-lista">
                <h3>Relatos recentes</h3>
                <ul id="listaRelatos"><li class="vazio">Carregando…</li></ul>
            </aside>
        </div>
    </div>
</section>

{{-- ===================== DADOS ===================== --}}
<section class="secao secao--areia" id="dados">
    <div class="container">
        <div class="cabeca revelar">
            <span class="rotulo">Síntese analítica</span>
            <h2>O que os satélites mostram sobre setembro.</h2>
            <p>Focos de calor detectados em São João da Boa Vista, por período do mês (BDQueimadas/INPE). Mesma escala nos dois gráficos.</p>
        </div>

        <div class="graficos">
            @foreach ([['ano' => 2020, 'dados' => $focos2020, 'total' => 332, 'nota' => 'Um único grande evento: 264 focos entre 8 e 10/09, durante alerta de emergência da Defesa Civil estadual por ar seco e quente.'], ['ano' => 2025, 'dados' => $focos2025, 'total' => 38, 'nota' => 'Ocorrências menores e dispersas, em núcleos isolados a oeste e ao sul — 82% em áreas de Cerrado.']] as $g)
                <figure class="grafico revelar">
                    <figcaption>
                        <span class="grafico__ano">Setembro {{ $g['ano'] }}</span>
                        <span class="grafico__total"><strong>{{ $g['total'] }}</strong> focos</span>
                    </figcaption>
                    <div class="barras" role="img" aria-label="Focos de calor por período em setembro de {{ $g['ano'] }}">
                        @foreach ($g['dados'] as $d)
                            <div class="barra" tabindex="0" data-tip="{{ $d['periodo'] }}/{{ $g['ano'] }}: {{ $d['valor'] }} focos">
                                <span class="barra__valor">{{ $d['valor'] }}</span>
                                <span class="barra__coluna" style="--h: {{ max(1.5, $d['valor'] / $maxFocos * 100) }}%"></span>
                                <span class="barra__rotulo">{{ $d['periodo'] }}</span>
                            </div>
                        @endforeach
                    </div>
                    <p class="grafico__nota">{{ $g['nota'] }}</p>
                </figure>
            @endforeach
        </div>

        <div class="hipoteses">
            <article class="hipotese revelar">
                <div class="hipotese__icone">{!! $ic('sun') !!}</div>
                <div>
                    <span class="rotulo">Hipótese ambiental</span>
                    <h3>Pasto e vegetação secos na estiagem aceleram o fogo.</h3>
                    <p>O pico de 2020 coincide com o alerta de emergência por umidade relativa crítica. Em 2025, o incêndio de 10/09 avançou rápido pelo capim seco.</p>
                </div>
            </article>
            <article class="hipotese revelar">
                <div class="hipotese__icone">{!! $ic('users') !!}</div>
                <div>
                    <span class="rotulo">Hipótese socioespacial</span>
                    <h3>Pequenas ignições humanas crescem sem aviso nos minutos iniciais.</h3>
                    <p>Os focos surgem em pontos distintos da zona rural, em pastagens e margens de vias: ignições pontuais, não a propagação de um único incêndio.</p>
                </div>
            </article>
        </div>
        <p class="fonte">Fontes: IBGE; BDQueimadas/INPE; Prefeitura Municipal de São João da Boa Vista (2025). Mapas temáticos elaborados pelos autores.</p>
    </div>
</section>

{{-- ===================== PREVENÇÃO ===================== --}}
<section class="secao" id="prevencao">
    <div class="container">
        <div class="cabeca revelar">
            <span class="rotulo">Educação ambiental e prevenção</span>
            <h2>Menos fogo começa antes do fogo.</h2>
            <p>Orientações baseadas na Política Nacional de Manejo Integrado do Fogo (Lei Federal nº 14.944/2024).</p>
        </div>
        <div class="dicas">
            <article class="dica revelar">
                {!! $ic('trash') !!}
                <h3>Não queime lixo</h3>
                <p>A queima de resíduos domésticos é irregular e foi a origem do incêndio que destruiu 85 mil m² de APP.</p>
            </article>
            <article class="dica revelar">
                {!! $ic('leaf') !!}
                <h3>Prefira alternativas ao fogo</h3>
                <p>Compostagem, roçada e incorporação da palhada substituem a queima no manejo agrícola.</p>
            </article>
            <article class="dica revelar">
                {!! $ic('sun') !!}
                <h3>Cuidado redobrado na estiagem</h3>
                <p>De julho a outubro a umidade despenca. Mantenha aceiros e nunca deixe fogo sem vigilância.</p>
            </article>
            <article class="dica revelar">
                {!! $ic('shield') !!}
                <h3>Viu fumaça? Registre e avise</h3>
                <p>Um relato rápido no Serra Alerta ajuda a validar a ocorrência. Havendo risco, ligue na hora.</p>
            </article>
        </div>
        <div class="emergencia revelar">
            <div>
                <h3>Canais de emergência</h3>
                <p>O Serra Alerta não substitui o acionamento oficial. Em caso de risco a pessoas, animais ou casas:</p>
            </div>
            <a href="tel:193" class="tel"><span>193</span>Corpo de Bombeiros</a>
            <a href="tel:199" class="tel"><span>199</span>Defesa Civil</a>
        </div>
    </div>
</section>

{{-- ===================== TECNOLOGIA ===================== --}}
<section class="secao secao--escura" id="tecnologia">
    <div class="container duas-colunas duas-colunas--centro">
        <div class="revelar">
            <span class="rotulo rotulo--claro">Tecnologia</span>
            <h2>Protótipo nativo, de custo zero, pronto para crescer.</h2>
            <p>
                O app é desenvolvido em <strong>Kotlin</strong> com interface em <strong>Jetpack Compose</strong>. Nesta etapa
                os dados ficam no próprio aparelho (SQLite via Room), validando o fluxo completo sem custos de
                infraestrutura. A arquitetura já prevê a migração para uma API REST e um banco geográfico centralizado,
                como a demonstração em Laravel deste site.
            </p>
            <div class="stack">
                @foreach (['Kotlin', 'Jetpack Compose', 'OSMdroid · OpenStreetMap', 'FusedLocationProvider', 'Room · SQLite', 'Laravel · API REST', 'Leaflet'] as $t)
                    <span>{{ $t }}</span>
                @endforeach
            </div>
        </div>
        <div class="arquitetura revelar" aria-label="Arquitetura do sistema">
            <div class="arq arq--app">
                {!! $ic('phone') !!}
                <div><strong>App Android</strong><small>Kotlin + Compose · mapa, câmera, GPS</small></div>
            </div>
            <div class="arq__seta"><span>hoje</span></div>
            <div class="arq">
                {!! $ic('database') !!}
                <div><strong>Banco local</strong><small>Room / SQLite no aparelho</small></div>
            </div>
            <div class="arq__seta arq__seta--futuro"><span>próxima etapa</span></div>
            <div class="arq arq--futuro">
                {!! $ic('satellite') !!}
                <div><strong>API REST + banco geográfico</strong><small>visão colaborativa entre usuários, painel para a Defesa Civil</small></div>
            </div>
        </div>
    </div>
</section>

{{-- ===================== EQUIPE ===================== --}}
<section class="secao" id="equipe">
    <div class="container">
        <div class="cabeca cabeca--centro revelar">
            <span class="rotulo">Equipe</span>
            <h2>Quem está por trás do Serra Alerta</h2>
            <p>Bacharelado em Ciência da Computação · Ciências do Ambiente (SBVCIAM) · IFSP Câmpus São João da Boa Vista</p>
        </div>
        <div class="equipe">
            @foreach ($equipe as $m)
                @php($partes = explode(' ', $m['nome']))
                <article class="membro revelar">
                    <span class="membro__avatar">{{ mb_substr($partes[0], 0, 1) }}{{ mb_substr(end($partes), 0, 1) }}</span>
                    <h3>{{ $m['nome'] }}</h3>
                    <small>{{ $m['prontuario'] }}</small>
                </article>
            @endforeach
        </div>
        <div class="orientador revelar">
            {!! $ic('book') !!}
            <span>Orientação: <strong>Prof. Elias Mendes Oliveira</strong></span>
        </div>
    </div>
</section>

{{-- ===================== CTA FINAL ===================== --}}
<section class="final">
    <div class="container final__inner revelar">
        <h2>A Serra da Paulista precisa de mais olhos.</h2>
        <p>Cada relato encurta o caminho entre a primeira fumaça e a primeira equipe em campo.</p>
        <a href="{{ route('app.demo') }}" class="btn btn--brasa" target="_blank">Experimentar o protótipo {!! $ic('arrow') !!}</a>
    </div>
</section>

<footer class="rodape">
    <div class="container rodape__grid">
        <div>
            <a href="#inicio" class="marca">
                <span class="marca__logo">{!! $ic('flame') !!}</span>
                <span>Serra Alerta</span>
            </a>
            <p>Projetos de Extensão — Documento-síntese, edição 2026/2. Instituto Federal de Educação, Ciência e Tecnologia de São Paulo, Câmpus São João da Boa Vista.</p>
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
        <span>Mapas © OpenStreetMap contributors</span>
    </div>
</footer>

<div class="tooltip" id="tooltip" role="status" hidden></div>

<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script src="{{ asset('js/landing.js') }}"></script>
</body>
</html>
