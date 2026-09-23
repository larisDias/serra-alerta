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
            ->when($request->query('tipo'), fn ($q, $tipo) => $q->where('tipo', $tipo))
            ->latest()
            ->limit(500)
            ->get();

        return response()->json(['data' => $relatos, 'total' => $relatos->count()]);
    }

    public function store(Request $request): JsonResponse
    {
        $dados = $request->validate([
            'tipo' => ['required', Rule::in(Relato::TIPOS)],
            'sinal' => ['required', Rule::in(Relato::SINAIS)],
            // Recorte aproximado da região de São João da Boa Vista.
            'latitude' => ['required', 'numeric', 'between:-22.6,-21.4'],
            'longitude' => ['required', 'numeric', 'between:-47.3,-46.3'],
            'descricao' => ['nullable', 'string', 'max:280'],
            'foto' => ['nullable', 'image', 'max:6144'],
        ], [
            'tipo.*' => 'Escolha o tipo de ocorrência.',
            'sinal.*' => 'Informe se é fumaça ou fogo.',
            'latitude.*' => 'Local fora da área de cobertura do protótipo.',
            'longitude.*' => 'Local fora da área de cobertura do protótipo.',
            'descricao.max' => 'A descrição deve ter no máximo 280 caracteres.',
            'foto.*' => 'A foto deve ser uma imagem de até 6 MB.',
        ]);

        if ($request->hasFile('foto')) {
            $arquivo = $request->file('foto');
            $nome = now()->format('Ymd_His') . '_' . Str::random(6) . '.' . $arquivo->extension();
            $arquivo->move(public_path('uploads'), $nome);
            $dados['foto'] = $nome;
        }

        return response()->json(['data' => Relato::create($dados)], 201);
    }
}
