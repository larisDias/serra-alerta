<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Relato extends Model
{
    /** Mesmas categorias do app Android (CategoriaOcorrencia.valor). */
    public const CATEGORIAS = ['queima_controlada', 'queimada_irregular', 'incendio_florestal', 'fumaca_nao_identificada'];

    protected $fillable = ['categoria', 'latitude', 'longitude', 'descricao', 'foto', 'ajustada_manualmente'];

    protected $casts = [
        'latitude' => 'float',
        'longitude' => 'float',
        'ajustada_manualmente' => 'boolean',
    ];

    protected $appends = ['foto_url'];

    public function getFotoUrlAttribute(): ?string
    {
        return $this->foto ? asset('uploads/' . $this->foto) : null;
    }
}
