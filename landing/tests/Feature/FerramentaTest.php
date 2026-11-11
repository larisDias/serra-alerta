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
            ->assertSee('A ferramenta');
    }

    public function test_landing_continua_respondendo(): void
    {
        $this->get('/')->assertOk();
    }
}
