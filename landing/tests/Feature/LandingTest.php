<?php

namespace Tests\Feature;

use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class LandingTest extends TestCase
{
    use RefreshDatabase;

    public function test_landing_nao_linka_a_demo_web_nem_tem_iframe(): void
    {
        $html = $this->get('/')->assertOk()->getContent();

        $this->assertNaoLinkaDemoWeb($html);
        $this->assertStringNotContainsString('Experimentar o protótipo', $html);
        $this->assertStringNotContainsString('Abrir o app', $html);
        $this->assertStringNotContainsString('Demonstração do app', $html);
        $this->assertStringNotContainsString('Experimentar o app', $html);
    }

    public function test_landing_oferece_download_pelo_github_releases(): void
    {
        $html = $this->get('/')->assertOk()->getContent();

        $this->assertStringContainsString('href="https://github.com/larisDias/serra-alerta/releases/latest"', $html);
        $this->assertStringContainsString('Baixar o app', $html);
        $this->assertStringContainsString('Baixar para Android', $html);
    }

    public function test_landing_tem_botao_de_computador_escondido(): void
    {
        $html = $this->get('/')->assertOk()->getContent();

        $this->assertSame(1, substr_count($html, 'class="btn btn--desativado" disabled aria-disabled="true" aria-hidden="true" hidden>'));
        $this->assertStringContainsString('Versão para computador', $html);
        $this->assertStringNotContainsString('Em breve', $html);
        $this->assertStringNotContainsString('Versão para computador em breve', $html);
        $this->assertStringNotContainsString('selo-breve', $html);
        $this->assertStringNotContainsString('desktop-aviso', $html);
    }

    private function assertNaoLinkaDemoWeb(string $html): void
    {
        $this->assertDoesNotMatchRegularExpression('#href="[^"]*/app(/|\?|")#', $html);
        $this->assertStringNotContainsString('app?embed=1', $html);
        $this->assertStringNotContainsString('app/embed.html', $html);
        $this->assertStringNotContainsString('<iframe', $html);
    }
}
