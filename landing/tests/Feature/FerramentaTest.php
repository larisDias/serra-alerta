<?php

namespace Tests\Feature;

use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class FerramentaTest extends TestCase
{
    use RefreshDatabase;

    public function test_pagina_da_ferramenta_responde(): void
    {
        $this->get('/ferramenta')
            ->assertOk()
            ->assertSee('Sobre a ferramenta')
            ->assertSee('Hipótese ambiental')
            ->assertSee('Hipótese socioespacial')
            ->assertSee('img/mapas/mapa1-areas-prioritarias.png')
            ->assertSee('img/mapas/mapa2-focos-setembro-2020.png')
            ->assertSee('img/mapas/mapa3-focos-setembro-2025.png');
    }

    public function test_player_do_video_tem_botao_de_som_e_comeca_mudo(): void
    {
        if (! file_exists(public_path('video/como-usar.mp4'))) {
            $this->markTestSkipped('O vídeo de demonstração não está em public/video.');
        }

        $resposta = $this->get('/ferramenta')->assertOk();

        $resposta
            ->assertSee('<video', false)
            ->assertSee('muted playsinline preload="metadata"', false)
            ->assertSee('id="botaoSom"', false)
            ->assertSee('aria-label="Ativar o som"', false)
            ->assertSee('aria-pressed="false"', false)
            ->assertSee('id="barra"', false)
            ->assertSee('Toque para ativar o som', false)
            ->assertSee('Vídeo de menos de um minuto', false);

        if (file_exists(public_path('video/poster.jpg'))) {
            $resposta->assertSee('poster="', false);
        }
    }

    public function test_video_nao_tem_loop(): void
    {
        if (! file_exists(public_path('video/como-usar.mp4'))) {
            $this->markTestSkipped('O vídeo de demonstração não está em public/video.');
        }

        $html = $this->get('/ferramenta')->assertOk()->getContent();

        // Sem "loop": ao terminar, aparece o overlay "Assistir de novo" em vez de reiniciar sozinho.
        $this->assertSame(1, preg_match('/<video\b[^>]*>/', $html, $tag));
        $this->assertDoesNotMatchRegularExpression('/\sloop[\s>=]/', $tag[0]);
    }

    public function test_fim_do_video_tem_botao_de_repetir_e_link_para_o_app(): void
    {
        if (! file_exists(public_path('video/como-usar.mp4'))) {
            $this->markTestSkipped('O vídeo de demonstração não está em public/video.');
        }

        $html = $this->get('/ferramenta')->assertOk()->getContent();

        $this->assertStringContainsString('id="botaoRepetir"', $html);
        $this->assertStringContainsString('aria-label="Assistir de novo"', $html);
        $this->assertStringContainsString('<span class="player__repetir-rotulo">Assistir de novo</span>', $html);
        $this->assertStringContainsString(
            '<a href="'.route('app.demo').'" class="player__app" target="_blank" rel="noopener">Experimentar o app',
            $html
        );
    }

    public function test_passos_do_video_tem_os_instantes_em_data_t(): void
    {
        $html = $this->get('/ferramenta')->assertOk()->getContent();

        foreach (['4.2', '14.4', '18.6', '21.2', '24.7', '27.9', '34.4'] as $instante) {
            $this->assertStringContainsString('data-t="'.$instante.'"', $html);
        }

        $this->assertSame(7, substr_count($html, 'data-t="'));
        $this->assertStringContainsString('data-fim="47.7"', $html);
    }

    public function test_landing_continua_respondendo(): void
    {
        $this->get('/')->assertOk();
    }
}
