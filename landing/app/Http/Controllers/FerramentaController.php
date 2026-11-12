<?php

namespace App\Http\Controllers;

use App\Support\DadosDoProjeto;
use Illuminate\View\View;

class FerramentaController extends Controller
{
    public function __invoke(): View
    {
        return view('ferramenta', [
            'repositorio' => 'https://github.com/larisDias/serra-alerta',
            // Página da versão mais recente do APK (publicada pelo workflow release-apk.yml ao criar uma tag v*).
            'apk' => 'https://github.com/larisDias/serra-alerta/releases/latest',

            // Vídeo de demonstração, quando o arquivo existe em public/ (o build estático não quebra sem ele).
            'video' => file_exists(public_path('video/como-usar.mp4')) ? 'video/como-usar.mp4' : null,

            // Quadro inicial do vídeo (gerado com ffmpeg), usado como poster enquanto o vídeo não toca.
            'poster' => file_exists(public_path('video/poster.jpg')) ? 'video/poster.jpg' : null,

            'fazendas' => DadosDoProjeto::fazendas(),
            'corregos' => DadosDoProjeto::corregos(),

            'focos2020' => DadosDoProjeto::focos2020(),
            'focos2025' => DadosDoProjeto::focos2025(),
        ]);
    }
}
