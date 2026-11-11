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

    public function test_landing_continua_respondendo(): void
    {
        $this->get('/')->assertOk();
    }
}
