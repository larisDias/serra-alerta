<?php

namespace App\Http\Controllers;

use App\Models\Relato;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Str;
use Illuminate\Validation\Rule;

/**
 * API REST mínima que representa a "base centralizada" prevista no projeto:
 * o app envia relatos e o mapa público os consulta.
 */
class RelatoController extends Controller
{
    public function index(Request $request): JsonResponse
    {
        $relatos = Relato::query()
            ->when($request->query('categoria'), fn ($q, $categoria) => $q->where('categoria', $categoria))
            ->latest()
            ->limit(500)
            ->get();

        return response()->json(['data' => $relatos, 'total' => $relatos->count()]);
    }

    public function store(Request $request): JsonResponse
    {
        $dados = $request->validate([
            'categoria' => ['required', Rule::in(Relato::CATEGORIAS)],
            // Recorte aproximado da região de São João da Boa Vista.
            'latitude' => ['required', 'numeric', 'between:-22.6,-21.4'],
            'longitude' => ['required', 'numeric', 'between:-47.3,-46.3'],
            'descricao' => ['nullable', 'string', 'max:280'],
            'ajustada_manualmente' => ['sometimes', 'boolean'],
            // Como no app, ao menos uma foto é obrigatória, até o limite de 3.
            'fotos' => ['required', 'array', 'min:1', 'max:' . Relato::MAX_FOTOS],
            'fotos.*' => ['image', 'max:6144'],
        ], [
            'categoria.*' => 'Escolha o que você está vendo.',
            'latitude.*' => 'Local fora da área de cobertura do protótipo.',
            'longitude.*' => 'Local fora da área de cobertura do protótipo.',
            'descricao.max' => 'A descrição deve ter no máximo 280 caracteres.',
            'fotos.required' => 'Tire ou escolha uma foto da ocorrência.',
            'fotos.min' => 'Tire ou escolha uma foto da ocorrência.',
            'fotos.max' => 'Envie no máximo ' . Relato::MAX_FOTOS . ' fotos.',
            'fotos.*' => 'As fotos devem ser imagens de até 6 MB.',
        ]);

        $nomes = [];
        foreach ($request->file('fotos') as $arquivo) {
            $nome = now()->format('Ymd_His') . '_' . Str::random(6) . '.' . $arquivo->extension();
            $arquivo->move(public_path('uploads'), $nome);
            $nomes[] = $nome;
        }
        $dados['fotos'] = $nomes;
        $dados['foto'] = $nomes[0];
        $token = Str::random(40);
        $dados['token_exclusao'] = hash('sha256', $token);

        // O token em claro só é devolvido aqui; o navegador o guarda para permitir a exclusão.
        return response()->json(['data' => Relato::create($dados), 'token' => $token], 201);
    }

    public function destroy(Request $request, Relato $relato): JsonResponse
    {
        $token = (string) $request->input('token', $request->bearerToken());
        abort_unless(
            $relato->token_exclusao && $token !== '' && hash_equals($relato->token_exclusao, hash('sha256', $token)),
            403,
            'Só quem enviou o relato pode excluí-lo.'
        );

        foreach ($relato->arquivos() as $nome) {
            @unlink(public_path('uploads/' . basename($nome)));
        }
        $relato->delete();

        return response()->json(null, 204);
    }
}
