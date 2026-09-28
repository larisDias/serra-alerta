<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('relatos', function (Blueprint $table) {
            $table->id();
            // queima_controlada | queimada_irregular | incendio_florestal | fumaca_nao_identificada
            $table->string('categoria', 30);
            $table->decimal('latitude', 9, 6);
            $table->decimal('longitude', 9, 6);
            $table->string('descricao', 280)->nullable();
            $table->string('foto')->nullable();
            $table->boolean('ajustada_manualmente')->default(false);
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('relatos');
    }
};
