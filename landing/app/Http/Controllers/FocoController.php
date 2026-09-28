<?php

namespace App\Http\Controllers;

use Illuminate\Http\Client\Pool;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Carbon;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Facades\Http;

/**
 * Focos de calor oficiais (INPE/BDQueimadas), somente leitura, no mesmo recorte do app Android.
 * O site busca os CSVs diários do INPE (Brasil inteiro, ~3,5 MB cada), filtra e guarda em cache, em vez de cada visitante baixá-los.
 */
class FocoController extends Controller
{
    private const URL = 'https://dataserver-coids.inpe.br/queimadas/queimadas/focos/csv/diario/Brasil/focos_diario_br_%s.csv';

    private const MIN_LAT = -22.2;
    private const MAX_LAT = -21.5;
    private const MIN_LON = -47.4;
    private const MAX_LON = -46.7;

    public function __invoke(Request $request): JsonResponse
    {
        $dias = min(max((int) $request->query('dias', 7), 1), 30);
        $datas = collect(range(0, $dias - 1))->map(fn ($i) => now('UTC')->subDays($i)->format('Ymd'));

        $focos = [];
        $faltando = [];
        foreach ($datas as $data) {
            $emCache = Cache::get("focos:$data");
            $emCache === null ? $faltando[] = $data : array_push($focos, ...$emCache);
        }

        $falhas = 0;
        if ($faltando) {
            $respostas = Http::pool(fn (Pool $pool) => array_map(
                fn ($data) => $pool->as($data)->timeout(25)->get(sprintf(self::URL, $data)),
                $faltando,
            ));
            foreach ($faltando as $data) {
                $resposta = $respostas[$data];
                if (! $resposta instanceof \Illuminate\Http\Client\Response || ! $resposta->successful()) {
                    $falhas++;
                    continue;
                }
                $doDia = $this->filtrar($resposta->body());
                // O arquivo do dia corrente ainda recebe focos; dias anteriores não mudam mais.
                Cache::put("focos:$data", $doDia, $data === now('UTC')->format('Ymd') ? now()->addMinutes(20) : now()->addDays(2));
                array_push($focos, ...$doDia);
            }
        }

        usort($focos, fn ($a, $b) => strcmp($b['data_deteccao'], $a['data_deteccao']));

        return response()->json([
            'data' => $focos,
            'total' => count($focos),
            'fonte' => 'INPE/BDQueimadas',
            'disponivel' => $falhas < count($datas),
            'atualizado_em' => now()->toIso8601String(),
        ]);
    }

    /** @return list<array{id: string, latitude: float, longitude: float, data_deteccao: string, frp: ?float, satelite: string, municipio: string}> */
    private function filtrar(string $csv): array
    {
        $linhas = preg_split('/\r?\n/', trim($csv));
        $cabecalho = array_flip(str_getcsv(array_shift($linhas)));
        $col = fn (array $l, string $nome) => isset($cabecalho[$nome]) ? trim($l[$cabecalho[$nome]] ?? '') : '';

        $focos = [];
        foreach ($linhas as $linha) {
            $l = str_getcsv($linha);
            $lat = (float) $col($l, 'lat');
            $lon = (float) $col($l, 'lon');
            if ($lat < self::MIN_LAT || $lat > self::MAX_LAT || $lon < self::MIN_LON || $lon > self::MAX_LON) {
                continue;
            }
            $frp = $col($l, 'frp');
            $focos[] = [
                'id' => $col($l, 'id'),
                'latitude' => $lat,
                'longitude' => $lon,
                'data_deteccao' => Carbon::parse($col($l, 'data_hora_gmt'), 'UTC')->toIso8601String(),
                'frp' => $frp === '' ? null : (float) $frp,
                'satelite' => $col($l, 'satelite'),
                'municipio' => $this->nomeProprio($col($l, 'municipio')),
            ];
        }

        return $focos;
    }

    /** "ESPÍRITO SANTO DO PINHAL" → "Espírito Santo do Pinhal". */
    private function nomeProprio(string $nome): string
    {
        $titulo = mb_convert_case(mb_strtolower($nome, 'UTF-8'), MB_CASE_TITLE, 'UTF-8');

        return preg_replace_callback('/(?<=\s)(Da|Das|De|Do|Dos|E)(?=\s)/u', fn ($m) => mb_strtolower($m[1]), $titulo);
    }
}
