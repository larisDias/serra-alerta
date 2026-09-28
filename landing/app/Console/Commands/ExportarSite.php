<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use Illuminate\Contracts\Http\Kernel;
use Illuminate\Filesystem\Filesystem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\URL;
use RuntimeException;

/**
 * Gera uma cópia estática do site para o GitHub Pages, que não executa PHP.
 * As páginas e os dois JSONs da API são renderizados pelas próprias rotas; o resto é copiado de public/.
 * No navegador, public/js/estatico.js simula a API em cima desses JSONs.
 */
class ExportarSite extends Command
{
    protected $signature = 'site:exportar {destino=dist : Pasta de saída (relativa a landing/)}';

    protected $description = 'Exporta o site como arquivos estáticos (GitHub Pages).';

    /** Rota => arquivo gerado. */
    private const PAGINAS = [
        '/' => 'index.html',
        '/app' => 'app/index.html',
        '/app?embed=1' => 'app/embed.html',
        '/api/relatos' => 'api/relatos.json',
        '/api/focos?dias=30' => 'api/focos.json',
    ];

    public function handle(Filesystem $fs): int
    {
        $base = rtrim(config('app.url'), '/');
        $destino = str_starts_with($this->argument('destino'), '/') ? $this->argument('destino') : base_path($this->argument('destino'));

        // route() e asset() passam a incluir o prefixo do Pages (ex.: /serra-alerta).
        URL::forceRootUrl($base);

        $fs->deleteDirectory($destino);
        foreach (['css', 'js'] as $pasta) {
            $fs->copyDirectory(public_path($pasta), "$destino/$pasta");
        }
        foreach (['favicon.ico', 'robots.txt'] as $arquivo) {
            $fs->copy(public_path($arquivo), "$destino/$arquivo");
        }
        $fs->put("$destino/.nojekyll", '');

        foreach (self::PAGINAS as $uri => $arquivo) {
            $conteudo = $this->renderizar($uri, $base);
            $fs->ensureDirectoryExists(dirname("$destino/$arquivo"));
            $fs->put("$destino/$arquivo", $conteudo);
            $this->line("  $uri → $arquivo");
        }

        $focos = json_decode($fs->get("$destino/api/focos.json"), true);
        $this->info(($focos['disponivel'] ?? false)
            ? "Site exportado em $destino ({$focos['total']} focos do INPE)."
            : "Site exportado em $destino, mas os focos do INPE estavam indisponíveis.");

        return self::SUCCESS;
    }

    private function renderizar(string $uri, string $base): string
    {
        $host = parse_url($base);
        $requisicao = Request::create($uri, 'GET', [], [], [], [
            'HTTP_HOST' => $host['host'],
            'HTTPS' => ($host['scheme'] ?? 'https') === 'https' ? 'on' : 'off',
        ]);
        $resposta = $this->laravel->make(Kernel::class)->handle($requisicao);

        if ($resposta->getStatusCode() !== 200) {
            throw new RuntimeException("$uri respondeu {$resposta->getStatusCode()}.");
        }

        $conteudo = $resposta->getContent();

        return str_ends_with($uri, '.json') || str_starts_with($uri, '/api/') ? $conteudo : $this->adaptar($conteudo, $base);
    }

    /** Ajustes do HTML que só fazem sentido sem servidor. */
    private function adaptar(string $html, string $base): string
    {
        // Sem PHP, a API vem do estatico.js (JSONs + localStorage); ele precisa rodar antes dos scripts da página.
        $html = preg_replace('#<script src="[^"]*/js/(landing|app-demo)\.js"#', '<script src="' . $base . '/js/estatico.js"></script>' . "\n" . '$0', $html, -1, $n);
        if ($n !== 1) {
            throw new RuntimeException('Não achei o <script> da página para injetar o estatico.js.');
        }

        // ?embed=1 seria ignorado pelo Pages: a versão embutida vira um arquivo próprio.
        $html = str_replace("$base/app?embed=1", "$base/app/embed.html", $html);

        // Textos que prometiam um backend.
        return strtr($html, [
            'Envie um relato pelo celular no topo da página e veja-o aparecer aqui.' => 'Nesta versão publicada no GitHub Pages os relatos são exemplos: o que você enviar pela demonstração do app fica salvo só no seu navegador e aparece aqui.',
            'e envia os relatos para o mapa colaborativo do site.' => 'e, nesta versão publicada, guarda os relatos enviados só no seu navegador.',
        ]);
    }
}
