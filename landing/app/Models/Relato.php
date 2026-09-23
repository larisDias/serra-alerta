<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Relato extends Model
{
    public const TIPOS = ['controlada', 'irregular', 'incendio'];
    public const SINAIS = ['fumaca', 'fogo'];

    protected $fillable = ['tipo', 'sinal', 'latitude', 'longitude', 'descricao', 'foto'];

    protected $casts = [
        'latitude' => 'float',
        'longitude' => 'float',
    ];

    protected $appends = ['foto_url'];

    public function getFotoUrlAttribute(): ?string
    {
        return $this->foto ? asset('uploads/' . $this->foto) : null;
    }
}
