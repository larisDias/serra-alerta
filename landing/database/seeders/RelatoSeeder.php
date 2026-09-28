<?php

namespace Database\Seeders;

use App\Models\Relato;
use Illuminate\Database\Seeder;

/** Relatos fictícios para a demonstração, nas categorias do app Android. */
class RelatoSeeder extends Seeder
{
    public function run(): void
    {
        $agora = now();
        $relatos = [
            ['incendio_florestal', -21.9255, -46.7330, 'Chamas subindo a encosta da Serra da Paulista, muita fumaça escura.', 12],
            ['incendio_florestal', -21.9915, -46.8665, 'Fogo no capim seco perto da linha férrea, próximo ao Ribeirão dos Porcos.', 180],
            ['queimada_irregular', -21.9548, -46.7585, 'Queima de lixo na margem da SP-342.', 40],
            ['queimada_irregular', -21.9862, -46.7795, 'Terreno baldio pegando fogo, perto do Córrego da Aliança.', 300],
            ['fumaca_nao_identificada', -22.0105, -46.8470, 'Fumaça branca atrás do morro, não dá para ver a origem.', 1560],
            ['queima_controlada', -21.9462, -46.7672, 'Queima de manejo com aceiro, produtor acompanhando.', 120],
            ['queima_controlada', -21.9320, -46.8040, 'Pequena queima de restos de poda, com gente vigiando.', 1800],
            ['fumaca_nao_identificada', -21.9990, -46.8230, 'Coluna de fumaça cinza para os lados da estrada rural.', 3000],
        ];

        foreach ($relatos as [$categoria, $lat, $lon, $descricao, $minutosAtras]) {
            $quando = $agora->copy()->subMinutes($minutosAtras);
            Relato::create(compact('categoria', 'descricao') + [
                'latitude' => $lat,
                'longitude' => $lon,
            ])->forceFill(['created_at' => $quando, 'updated_at' => $quando])->save();
        }
    }
}
