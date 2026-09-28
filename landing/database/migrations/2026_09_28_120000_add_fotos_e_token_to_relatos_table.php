<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('relatos', function (Blueprint $table) {
            // Até 3 fotos por relato, como no app; `foto` continua sendo a primeira.
            $table->json('fotos')->nullable()->after('foto');
            // Só quem enviou o relato recebe o token e pode excluí-lo.
            $table->string('token_exclusao', 64)->nullable()->after('ajustada_manualmente');
        });
    }

    public function down(): void
    {
        Schema::table('relatos', function (Blueprint $table) {
            $table->dropColumn(['fotos', 'token_exclusao']);
        });
    }
};
