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
