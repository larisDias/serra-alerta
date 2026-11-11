(() => {
    'use strict';

    /* ---------- Navegação ---------- */
    // Menu do celular: abre e fecha a lista de links.
    const nav = document.getElementById('nav');
    const links = document.getElementById('navLinks');
    const menu = document.getElementById('navMenu');
    if (menu && links) {
        menu.addEventListener('click', () => links.classList.toggle('aberto'));
        links.querySelectorAll('a').forEach(a => a.addEventListener('click', () => links.classList.remove('aberto')));
    }

    // Barra de navegação ganha fundo ao rolar a página (mesmo critério da landing).
    if (nav) {
        const aoRolar = () => nav.classList.toggle('rolado', window.scrollY > 40);
        window.addEventListener('scroll', aoRolar, { passive: true });
        aoRolar();
    }

    /* ---------- Player do vídeo de demonstração ----------
       Fica antes de "Revelar ao rolar" porque aquela seção usa return quando não há
       IntersectionObserver, e o return encerraria esta IIFE antes do player. */
    const player = document.getElementById('player');
    const video = document.getElementById('videoUso');
    if (player && video) {
        iniciarPlayer(player, video);
    }

    function iniciarPlayer(player, video) {
        const reduzirMovimento = !!(window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches);
        const botaoSom = document.getElementById('botaoSom');
        const dica = document.getElementById('dicaSom');
        const botaoPlay = document.getElementById('botaoPlay');
        const botaoRepetir = document.getElementById('botaoRepetir');
        const barra = document.getElementById('barra');
        const flashPlay = player.querySelector('.player__flash--play');
        const flashPause = player.querySelector('.player__flash--pause');

        // Passos sincronizados: data-t de cada item (segundo em que o passo começa) e data-fim da lista.
        const lista = document.querySelector('.passos-uso');
        const passos = lista ? Array.from(lista.querySelectorAll('li[data-t]')) : [];
        const fimDosPassos = (lista && parseFloat(lista.dataset.fim)) || Infinity;

        let pausadoPeloUsuario = false; // se a pessoa pausou, o vídeo não volta a tocar sozinho ao entrar na tela
        let dicaIniciada = false;
        let timerDica = null;
        let quadro = 0;
        let passoAtual = -1;
        let arrastando = false;
        // Estado final: o vídeo terminou e o overlay "Assistir de novo" está na tela. Nele, nada toca sozinho.
        let terminado = false;

        // Com JavaScript, os controles próprios substituem os nativos (sem JS, "controls" continua valendo).
        video.removeAttribute('controls');
        player.classList.add('player--ativo');

        const duracaoValida = () => isFinite(video.duration) && video.duration > 0;

        const formatarTempo = (segundos) => {
            const total = isFinite(segundos) && segundos > 0 ? Math.floor(segundos) : 0;
            return `${Math.floor(total / 60)}:${String(total % 60).padStart(2, '0')}`;
        };

        // No estado final o botão de play não aparece (o overlay toma o lugar dele).
        const atualizarEstado = () => player.classList.toggle('player--pausado', video.paused && !terminado);

        // Fração do player que está na tela neste momento (medida agora, não pelo último aviso do observador).
        const proporcaoVisivel = () => {
            const area = player.getBoundingClientRect();
            if (area.height === 0) return 0;
            const alturaVisivel = Math.min(area.bottom, window.innerHeight) - Math.max(area.top, 0);
            return Math.max(alturaVisivel, 0) / area.height;
        };

        /* ----- Reprodução ----- */
        function reproduzir() {
            const promessa = video.play();
            // Se o navegador recusar a reprodução, o vídeo fica pausado e aparece o botão de play.
            if (promessa && typeof promessa.catch === 'function') {
                promessa.catch(atualizarEstado);
            }
        }

        // Ícone de play/pause que aparece e some (entrada em 250 ms, depois fade out).
        function piscarIcone(icone) {
            if (reduzirMovimento || !icone || typeof icone.animate !== 'function') return;
            icone.animate(
                [
                    { opacity: 0, transform: 'scale(.6)' },
                    { opacity: 1, transform: 'scale(1)', offset: 0.38 },
                    { opacity: 0, transform: 'scale(1.15)' }
                ],
                { duration: 650, easing: 'ease-out' }
            );
        }

        function alternarReproducao() {
            if (terminado) {
                reiniciar();
                return;
            }
            if (video.paused) {
                pausadoPeloUsuario = false;
                piscarIcone(flashPlay);
                reproduzir();
            } else {
                pausadoPeloUsuario = true;
                piscarIcone(flashPause);
                video.pause();
            }
        }

        /* ----- Fim do vídeo ----- */
        // Ao terminar, o vídeo fica no último quadro e aparece o overlay (o vídeo não tem "loop").
        function entrarNoFim() {
            terminado = true;
            cancelAnimationFrame(quadro);
            player.classList.add('player--fim');
            atualizarEstado();
            atualizar();
        }

        // Sai do estado final (ao reiniciar ou ao voltar a um ponto anterior). O CSS faz o fade de 250 ms.
        function sairDoFim() {
            if (!terminado) return;
            terminado = false;
            player.classList.remove('player--fim');
            atualizarEstado();
            atualizar();
        }

        // Volta a 0 s e toca. Não mexe em video.muted: o som escolhido pela pessoa continua o mesmo.
        function reiniciar() {
            pausadoPeloUsuario = false;
            sairDoFim();
            video.currentTime = 0;
            atualizar();
            reproduzir();
        }

        /* ----- Som ----- */
        function esconderDica() {
            clearTimeout(timerDica);
            if (dica) dica.classList.add('oculta');
            botaoSom.classList.remove('pulsa');
        }

        // A dica começa quando o vídeo aparece pela primeira vez e some após 6 s (ou no primeiro clique).
        function iniciarDica() {
            dicaIniciada = true;
            if (!video.muted) return;
            botaoSom.classList.add('pulsa');
            timerDica = setTimeout(esconderDica, 6000);
        }

        function atualizarSom() {
            const comSom = !video.muted;
            botaoSom.setAttribute('aria-pressed', String(comSom));
            botaoSom.setAttribute('aria-label', comSom ? 'Desativar o som' : 'Ativar o som');
            if (comSom) esconderDica();
        }

        // Liga e desliga o som sem reiniciar o vídeo.
        botaoSom.addEventListener('click', () => {
            video.muted = !video.muted;
            esconderDica();
        });

        /* ----- Progresso, busca e passos ----- */
        function destacarPasso(tempo) {
            let indice = -1;
            // No estado final nenhum passo fica destacado.
            if (!terminado && tempo < fimDosPassos) {
                passos.forEach((li, i) => {
                    if (tempo >= parseFloat(li.dataset.t)) indice = i;
                });
            }
            if (indice === passoAtual) return;
            passoAtual = indice;
            passos.forEach((li, i) => {
                const ativo = i === indice;
                li.classList.toggle('ativo', ativo);
                if (ativo) li.setAttribute('aria-current', 'step');
                else li.removeAttribute('aria-current');
            });
        }

        function atualizar() {
            const tempo = video.currentTime || 0;
            const duracao = duracaoValida() ? video.duration : 0;
            // No estado final a barra fica cheia (100%).
            const tempoExibido = terminado ? duracao : tempo;
            const proporcao = terminado ? 1 : (duracao ? Math.min(tempo / duracao, 1) : 0);
            barra.style.setProperty('--p', proporcao.toFixed(4));
            barra.setAttribute('aria-valuemax', String(Math.floor(duracao)));
            barra.setAttribute('aria-valuenow', String(Math.floor(tempoExibido)));
            barra.setAttribute('aria-valuetext', `${formatarTempo(tempoExibido)} de ${formatarTempo(duracao)}`);
            destacarPasso(tempo);
        }

        // Enquanto toca, atualiza a barra e os passos a cada quadro (mais suave que timeupdate).
        function cicloDeQuadros() {
            atualizar();
            quadro = video.paused ? 0 : requestAnimationFrame(cicloDeQuadros);
        }

        function buscar(tempo) {
            const fim = duracaoValida() ? video.duration : Infinity;
            const destino = Math.min(Math.max(tempo, 0), fim);
            // Voltar para antes do fim (barra, teclado ou passo) tira o vídeo do estado final.
            if (terminado && destino < fim - 0.5) sairDoFim();
            video.currentTime = destino;
            atualizar();
        }

        // Clique e arraste na barra de progresso.
        const buscarPeloPonteiro = (evento) => {
            const area = barra.getBoundingClientRect();
            if (!duracaoValida() || area.width === 0) return;
            const proporcao = Math.min(Math.max((evento.clientX - area.left) / area.width, 0), 1);
            buscar(proporcao * video.duration);
        };

        barra.addEventListener('pointerdown', (evento) => {
            arrastando = true;
            barra.setPointerCapture(evento.pointerId);
            buscarPeloPonteiro(evento);
        });
        barra.addEventListener('pointermove', (evento) => {
            if (arrastando) buscarPeloPonteiro(evento);
        });
        const soltar = () => { arrastando = false; };
        barra.addEventListener('pointerup', soltar);
        barra.addEventListener('pointercancel', soltar);
        barra.addEventListener('lostpointercapture', soltar);

        // Teclado na barra: setas ±5 s, Home e End.
        barra.addEventListener('keydown', (evento) => {
            let destino = null;
            if (evento.key === 'ArrowLeft') destino = video.currentTime - 5;
            else if (evento.key === 'ArrowRight') destino = video.currentTime + 5;
            else if (evento.key === 'Home') destino = 0;
            else if (evento.key === 'End' && duracaoValida()) destino = video.duration;
            if (destino === null) return;
            evento.preventDefault();
            buscar(destino);
        });

        // Clicar num passo leva o vídeo àquele instante e toca.
        passos.forEach((li) => {
            const botao = li.querySelector('button');
            if (!botao) return;
            botao.addEventListener('click', () => {
                pausadoPeloUsuario = false;
                buscar(parseFloat(li.dataset.t));
                // Se o player está na tela, toca já. Se não está, leva a pessoa até ele: o observador
                // abaixo toca quando ele entrar na tela (tocar agora seria pausado de novo ao rolar).
                if (proporcaoVisivel() >= 0.5) reproduzir();
                else player.scrollIntoView({ behavior: 'smooth', block: 'center' });
            });
        });

        /* ----- Controles do player ----- */
        // Clique no vídeo alterna play e pausa (no estado final, reinicia).
        video.addEventListener('click', alternarReproducao);
        botaoPlay.addEventListener('click', () => {
            if (terminado) {
                reiniciar();
                return;
            }
            pausadoPeloUsuario = false;
            reproduzir();
        });
        botaoRepetir.addEventListener('click', reiniciar);

        // No estado final, clicar no fundo do overlay também reinicia. Botões, links e a barra têm ação própria.
        player.addEventListener('click', (evento) => {
            if (!terminado || evento.target.closest('a, button, .player__barra')) return;
            reiniciar();
        });

        // Teclado no próprio player: Espaço ou Enter alternam (os botões têm o seu próprio teclado).
        player.addEventListener('keydown', (evento) => {
            if (evento.target !== player) return;
            if (evento.key === ' ' || evento.key === 'Enter') {
                evento.preventDefault();
                alternarReproducao();
            }
        });

        video.addEventListener('play', () => {
            sairDoFim(); // tocando de novo, o vídeo não está mais no estado final
            atualizarEstado();
            cancelAnimationFrame(quadro);
            quadro = requestAnimationFrame(cicloDeQuadros);
        });
        video.addEventListener('pause', () => {
            atualizarEstado();
            cancelAnimationFrame(quadro);
            atualizar();
        });
        video.addEventListener('ended', entrarNoFim);
        ['timeupdate', 'seeked', 'loadedmetadata', 'durationchange'].forEach((evento) => {
            video.addEventListener(evento, atualizar);
        });
        video.addEventListener('volumechange', atualizarSom);

        /* ----- Autoplay: toca ao entrar na tela (50% visível) e pausa ao sair -----
           Com "reduzir movimento", não toca sozinho: fica o botão de play grande.
           No estado final não toca sozinho: só o botão "Assistir de novo" ou um clique no player reinicia. */
        if ('IntersectionObserver' in window) {
            const observador = new IntersectionObserver((entradas) => {
                entradas.forEach((entrada) => {
                    if (entrada.intersectionRatio >= 0.5) {
                        if (!dicaIniciada) iniciarDica();
                        if (!reduzirMovimento && !pausadoPeloUsuario && !terminado && video.paused) reproduzir();
                    } else if (!video.paused) {
                        video.pause();
                    }
                });
            }, { threshold: [0, 0.5, 1] });
            observador.observe(player);
        } else {
            iniciarDica();
        }

        atualizarEstado();
        atualizarSom();
        atualizar();
    }

    /* ---------- Revelar ao rolar ---------- */
    const elementos = document.querySelectorAll('.revelar');

    // Sem IntersectionObserver (navegadores antigos), mostra tudo de uma vez.
    if (!('IntersectionObserver' in window)) {
        elementos.forEach(el => el.classList.add('revelado'));
        return;
    }

    const obs = new IntersectionObserver((entradas) => {
        entradas.forEach(e => {
            if (!e.isIntersecting) return;
            e.target.classList.add('revelado');
            obs.unobserve(e.target);
        });
    }, { threshold: 0.15 });

    elementos.forEach((el, i) => {
        el.style.transitionDelay = `${(i % 4) * 70}ms`;
        obs.observe(el);
    });
})();
