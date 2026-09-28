<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use Illuminate\Contracts\Http\Kernel;
use Illuminate\Filesystem\Filesystem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\URL;
use RuntimeException;

/**
 * Gera uma cópia estática do site para o GitHub Pages, que não executa PHP.
 * As páginas e o JSON dos relatos de exemplo são renderizados pelas próprias rotas; o resto é copiado de public/.
 * Os focos do INPE viram um snapshot (api/focos.json) por uma única consulta ao WFS; o estatico.js o usa como
 * primeira resposta e depois consulta o INPE ao vivo. No navegador, ele simula a API em cima desses JSONs.
 */
class ExportarSite extends Command
{
    protected $signature = 'site:exportar {destino=dist : Pasta de saída (relativa a landing/)}';

    protected $description = 'Exporta o site como arquivos estáticos (GitHub Pages).';

    private const WFS = 'https://terrabrasilis.dpi.inpe.br/queimadas/geoserver/wfs';

    // Mesmo recorte do app Android e do FocoController.
    private const MIN_LAT = -22.2;
    private const MAX_LAT = -21.5;
    private const MIN_LON = -47.4;
    private const MAX_LON = -46.7;

    /** Rota => arquivo gerado. */
    private const PAGINAS = [
        '/' => 'index.html',
        '/app' => 'app/index.html',
        '/app?embed=1' => 'app/embed.html',
        '/api/relatos' => 'api/relatos.json',
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

        $this->coletarFocos($fs, "$destino/api/focos.json");

        $this->info("Site exportado em $destino.");

        return self::SUCCESS;
    }

    /** Snapshot dos focos dos últimos 30 dias na região, no mesmo formato de /api/focos. Sem ele, o site só usa o INPE ao vivo. */
    private function coletarFocos(Filesystem $fs, string $arquivo): void
    {
        try {
            $resposta = Http::timeout(30)->retry(2, 1000)->get(self::WFS, [
                'service' => 'WFS',
                'version' => '1.0.0',
                'request' => 'GetFeature',
                'typeName' => 'bdqueimadas2:focos',
                'outputFormat' => 'application/json',
                'propertyName' => 'id_foco_bdq,latitude,longitude,data_hora_gmt,satelite,municipio,frp',
                'CQL_FILTER' => sprintf(
                    "BBOX(geometria,%s,%s,%s,%s) AND data_hora_gmt >= '%s'",
                    self::MIN_LON, self::MIN_LAT, self::MAX_LON, self::MAX_LAT,
                    now('UTC')->subDays(30)->format('Y-m-d\T00:00:00\Z'),
                ),
            ])->throw();
        } catch (\Throwable $e) {
            $this->warn('Focos do INPE indisponíveis; o site usará só a consulta ao vivo. (' . $e->getMessage() . ')');

            return;
        }

        $focos = collect($resposta->json('features', []))->map(fn ($f) => [
            'id' => (string) $f['properties']['id_foco_bdq'],
            'latitude' => (float) $f['properties']['latitude'],
            'longitude' => (float) $f['properties']['longitude'],
            'data_deteccao' => $f['properties']['data_hora_gmt'],
            'frp' => $f['properties']['frp'] ?? null,
            'satelite' => $f['properties']['satelite'] ?? '',
            'municipio' => $this->nomeProprio($f['properties']['municipio'] ?? ''),
        ])->sortByDesc('data_deteccao')->values();

        $fs->ensureDirectoryExists(dirname($arquivo));
        $fs->put($arquivo, json_encode([
            'data' => $focos,
            'total' => $focos->count(),
            'fonte' => 'INPE/BDQueimadas',
            'disponivel' => true,
            'atualizado_em' => now()->toIso8601String(),
        ], JSON_UNESCAPED_UNICODE));
        $this->line("  focos do INPE → api/focos.json ({$focos->count()} focos)");
    }

    /** "ESPÍRITO SANTO DO PINHAL" → "Espírito Santo do Pinhal". */
    private function nomeProprio(string $nome): string
    {
        $titulo = mb_convert_case(mb_strtolower($nome, 'UTF-8'), MB_CASE_TITLE, 'UTF-8');

        return preg_replace_callback('/(?<=\s)(Da|Das|De|Do|Dos|E)(?=\s)/u', fn ($m) => mb_strtolower($m[1]), $titulo);
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
