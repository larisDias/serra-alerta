<?php

namespace App\Support;

/**
 * Dados fixos do projeto usados por mais de uma página (landing e "A ferramenta").
 */
class DadosDoProjeto
{
    /** Focos de calor por período em 2020 (BDQueimadas/INPE), conforme o documento-síntese. */
    public static function focos2020(): array
    {
        return [
            ['periodo' => '01–07 set', 'valor' => 49],
            ['periodo' => '08–10 set', 'valor' => 264],
            ['periodo' => '11–20 set', 'valor' => 7],
            ['periodo' => '21–30 set', 'valor' => 12],
        ];
    }

    /** Focos de calor por período em 2025 (BDQueimadas/INPE), conforme o documento-síntese. */
    public static function focos2025(): array
    {
        return [
            ['periodo' => '01–07 set', 'valor' => 7],
            ['periodo' => '08–14 set', 'valor' => 15],
            ['periodo' => '15–21 set', 'valor' => 7],
            ['periodo' => '22–30 set', 'valor' => 9],
        ];
    }

    /** Fazendas da região monitorada. */
    public static function fazendas(): array
    {
        return ['Fazenda Cachoeira', 'Fazenda Aliança', 'Fazenda Desterro', 'Fazenda Santa Gabriela', 'Fazenda Alto Alegre'];
    }

    /** Córregos e ribeirões da região monitorada. */
    public static function corregos(): array
    {
        return ['Córrego da Cachoeira', 'Córrego da Aliança', 'Córrego Sertãozinho', 'Córrego da Estiva', 'Ribeirão do Paraíso', 'Córrego da Bomba', 'Córrego São Pedro', 'Ribeirão dos Porcos'];
    }
}
