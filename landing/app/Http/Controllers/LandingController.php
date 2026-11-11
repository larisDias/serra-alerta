<?php

namespace App\Http\Controllers;

use App\Models\Relato;
use App\Support\DadosDoProjeto;
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
            'focos2020' => DadosDoProjeto::focos2020(),
            'focos2025' => DadosDoProjeto::focos2025(),

            'fazendas' => DadosDoProjeto::fazendas(),
            'corregos' => DadosDoProjeto::corregos(),

            'equipe' => [
                ['nome' => 'André Lyra Fernandes', 'prontuario' => 'BV303139X', 'foto' => 'img/equipe/andre.jpg'],
                ['nome' => 'Gabriel Maia Miguel', 'prontuario' => 'BV3035522', 'foto' => 'img/equipe/gabriel.jpg'],
                ['nome' => 'Larissa Gabriela Sant’Angelo Dias', 'prontuario' => 'BV3032078', 'foto' => 'img/equipe/larissa.jpg'],
                ['nome' => 'Mariana Peixoto Chahud', 'prontuario' => 'BV3031586', 'foto' => 'img/equipe/mariana.jpg'],
                ['nome' => 'Victoria Carolina Ferreira da Silva', 'prontuario' => 'BV3033848', 'foto' => 'img/equipe/victoria.jpg'],
            ],
        ]);
    }
}
