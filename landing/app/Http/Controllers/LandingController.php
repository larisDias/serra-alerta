<?php

namespace App\Http\Controllers;

use App\Models\Relato;
use Illuminate\View\View;

class LandingController extends Controller
{
    public function __invoke(): View
    {
        return view('landing', [
            'totalRelatos' => Relato::count(),
            'repositorio' => 'https://github.com/larisDias/serra-alerta',
            // Página da versão mais recente do APK (publicada pelo workflow release-apk.yml ao criar uma tag v*).
            'apk' => 'https://github.com/larisDias/serra-alerta/releases/latest',

            // Focos de calor por período (BDQueimadas/INPE), conforme o documento-síntese.
            'focos2020' => [
                ['periodo' => '01–07 set', 'valor' => 49],
                ['periodo' => '08–10 set', 'valor' => 264],
                ['periodo' => '11–20 set', 'valor' => 7],
                ['periodo' => '21–30 set', 'valor' => 12],
            ],
            'focos2025' => [
                ['periodo' => '01–07 set', 'valor' => 7],
                ['periodo' => '08–14 set', 'valor' => 15],
                ['periodo' => '15–21 set', 'valor' => 7],
                ['periodo' => '22–30 set', 'valor' => 9],
            ],

            'fazendas' => ['Fazenda Cachoeira', 'Fazenda Aliança', 'Fazenda Desterro', 'Fazenda Santa Gabriela', 'Fazenda Alto Alegre'],
            'corregos' => ['Córrego da Cachoeira', 'Córrego da Aliança', 'Córrego Sertãozinho', 'Córrego da Estiva', 'Ribeirão do Paraíso', 'Córrego da Bomba', 'Córrego São Pedro', 'Ribeirão dos Porcos'],

            'equipe' => [
                ['nome' => 'André Lyra Fernandes', 'prontuario' => 'BV303139X', 'foto' => 'img/equipe/andre.jpg'],
                ['nome' => 'Gabriel Maia Miguel', 'prontuario' => 'BV3035522', 'foto' => 'img/equipe/gabriel.jpg'],
                ['nome' => 'Larissa Gabriela Sant’Angelo Dias', 'prontuario' => 'BV3032078'],
                ['nome' => 'Mariana Peixoto Chahud', 'prontuario' => 'BV3031586'],
                ['nome' => 'Victoria Carolina Ferreira da Silva', 'prontuario' => 'BV3033848'],
            ],
        ]);
    }
}
