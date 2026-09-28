<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Relato extends Model
{
    /** Mesmas categorias do app Android (CategoriaOcorrencia.valor). */
    public const CATEGORIAS = ['queima_controlada', 'queimada_irregular', 'incendio_florestal', 'fumaca_nao_identificada'];

    /** Mesmo limite de fotos por relato do app Android. */
    public const MAX_FOTOS = 3;

    protected $fillable = ['categoria', 'latitude', 'longitude', 'descricao', 'foto', 'fotos', 'ajustada_manualmente', 'token_exclusao'];

    protected $hidden = ['token_exclusao', 'fotos'];

    protected $casts = [
        'latitude' => 'float',
        'longitude' => 'float',
        'fotos' => 'array',
        'ajustada_manualmente' => 'boolean',
    ];

    protected $appends = ['foto_url', 'fotos_urls'];

    /** Nomes dos arquivos do relato; relatos antigos só têm `foto`. */
    public function arquivos(): array
    {
        return $this->fotos ?: array_filter([$this->foto]);
    }

    public function getFotoUrlAttribute(): ?string
    {
        return $this->foto ? asset('uploads/' . $this->foto) : null;
    }

    /** @return list<string> */
    public function getFotosUrlsAttribute(): array
    {
        return array_values(array_map(fn ($nome) => asset('uploads/' . $nome), $this->arquivos()));
    }
}
