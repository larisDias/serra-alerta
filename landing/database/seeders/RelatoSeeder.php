<?php

namespace Database\Seeders;

use App\Models\Relato;
use Illuminate\Database\Seeder;

/** Relatos fictícios, os mesmos do app Android, para a demonstração. */
class RelatoSeeder extends Seeder
{
    public function run(): void
    {
        $agora = now();
        $relatos = [
            ['incendio', 'fogo', -21.9255, -46.7330, 'Chamas subindo a encosta da Serra da Paulista, muita fumaça escura.', 12],
            ['incendio', 'fogo', -21.9915, -46.8665, 'Fogo no capim seco perto da linha férrea, próximo ao Ribeirão dos Porcos.', 180],
            ['irregular', 'fumaca', -21.9548, -46.7585, 'Queima de lixo na margem da SP-342.', 40],
            ['irregular', 'fogo', -21.9862, -46.7795, 'Terreno baldio pegando fogo, perto do Córrego da Aliança.', 300],
            ['irregular', 'fumaca', -22.0105, -46.8470, 'Fumaça branca em pastagem, sem ninguém por perto.', 1560],
            ['controlada', 'fumaca', -21.9462, -46.7672, 'Queima de manejo com aceiro, produtor acompanhando.', 120],
            ['controlada', 'fumaca', -21.9320, -46.8040, 'Pequena queima de restos de poda.', 1800],
            ['controlada', 'fogo', -21.9990, -46.8230, 'Fogo baixo em área cercada, com caminhão-pipa.', 3000],
        ];

        foreach ($relatos as [$tipo, $sinal, $lat, $lon, $descricao, $minutosAtras]) {
            $quando = $agora->copy()->subMinutes($minutosAtras);
            Relato::create(compact('tipo', 'sinal', 'descricao') + [
                'latitude' => $lat,
                'longitude' => $lon,
            ])->forceFill(['created_at' => $quando, 'updated_at' => $quando])->save();
        }
    }
}
