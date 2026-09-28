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
            // Como no app, a foto é obrigatória.
            'foto' => ['required', 'image', 'max:6144'],
        ], [
            'categoria.*' => 'Escolha o que você está vendo.',
            'latitude.*' => 'Local fora da área de cobertura do protótipo.',
            'longitude.*' => 'Local fora da área de cobertura do protótipo.',
            'descricao.max' => 'A descrição deve ter no máximo 280 caracteres.',
            'foto.required' => 'Tire ou escolha uma foto da ocorrência.',
            'foto.*' => 'A foto deve ser uma imagem de até 6 MB.',
        ]);

        $arquivo = $request->file('foto');
        $nome = now()->format('Ymd_His') . '_' . Str::random(6) . '.' . $arquivo->extension();
        $arquivo->move(public_path('uploads'), $nome);
        $dados['foto'] = $nome;

        return response()->json(['data' => Relato::create($dados)], 201);
    }
}
