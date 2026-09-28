<?php

namespace Tests\Feature;

use App\Models\Relato;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Http\UploadedFile;
use Tests\TestCase;

class RelatoApiTest extends TestCase
{
    use RefreshDatabase;

    protected function tearDown(): void
    {
        foreach (Relato::all() as $relato) {
            foreach ($relato->arquivos() as $nome) {
                @unlink(public_path('uploads/' . $nome));
            }
        }
        parent::tearDown();
    }

    private function dados(int $fotos): array
    {
        return [
            'categoria' => 'queimada_irregular',
            'latitude' => -21.95,
            'longitude' => -46.80,
            'fotos' => array_map(fn ($i) => UploadedFile::fake()->image("foto$i.jpg"), $fotos ? range(1, $fotos) : []),
        ];
    }

    public function test_cria_relato_com_varias_fotos_e_devolve_token(): void
    {
        $resposta = $this->postJson('/api/relatos', $this->dados(3))->assertCreated();

        $resposta->assertJsonCount(3, 'data.fotos_urls');
        $resposta->assertJsonPath('data.foto_url', $resposta->json('data.fotos_urls.0'));
        $this->assertNotEmpty($resposta->json('token'));
        $resposta->assertJsonMissingPath('data.token_exclusao');
    }

    public function test_exige_de_uma_a_tres_fotos(): void
    {
        $this->postJson('/api/relatos', $this->dados(0))->assertUnprocessable();
        $this->postJson('/api/relatos', $this->dados(4))->assertUnprocessable();
    }

    public function test_exclui_relato_somente_com_o_token_correto(): void
    {
        $criado = $this->postJson('/api/relatos', $this->dados(2))->assertCreated();
        $id = $criado->json('data.id');

        $this->deleteJson("/api/relatos/$id", ['token' => 'errado'])->assertForbidden();
        $this->deleteJson("/api/relatos/$id")->assertForbidden();
        $this->assertDatabaseCount('relatos', 1);

        $this->deleteJson("/api/relatos/$id", ['token' => $criado->json('token')])->assertNoContent();
        $this->assertDatabaseCount('relatos', 0);
    }

    public function test_relato_de_exemplo_sem_token_nao_pode_ser_excluido(): void
    {
        $relato = Relato::create(['categoria' => 'queima_controlada', 'latitude' => -21.95, 'longitude' => -46.8]);

        $this->deleteJson("/api/relatos/{$relato->id}", ['token' => ''])->assertForbidden();
    }
}
