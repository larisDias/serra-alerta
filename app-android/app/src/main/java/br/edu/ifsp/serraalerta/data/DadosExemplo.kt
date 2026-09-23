package br.edu.ifsp.serraalerta.data

/** Relatos fictícios para demonstrar o protótipo na primeira execução. */
object DadosExemplo {
    private const val MIN = 60_000L
    private const val HORA = 60 * MIN

    fun relatos(agora: Long = System.currentTimeMillis()) = listOf(
        Relato(tipo = TipoFoco.INCENDIO, sinal = Sinal.FOGO, latitude = -21.9255, longitude = -46.7330,
            descricao = "Chamas subindo a encosta da Serra da Paulista, muita fumaça escura.", criadoEm = agora - 12 * MIN),
        Relato(tipo = TipoFoco.INCENDIO, sinal = Sinal.FOGO, latitude = -21.9915, longitude = -46.8665,
            descricao = "Fogo no capim seco perto da linha férrea, próximo ao Ribeirão dos Porcos.", criadoEm = agora - 3 * HORA),
        Relato(tipo = TipoFoco.IRREGULAR, sinal = Sinal.FUMACA, latitude = -21.9548, longitude = -46.7585,
            descricao = "Queima de lixo na margem da SP-342.", criadoEm = agora - 40 * MIN),
        Relato(tipo = TipoFoco.IRREGULAR, sinal = Sinal.FOGO, latitude = -21.9862, longitude = -46.7795,
            descricao = "Terreno baldio pegando fogo, perto do Córrego da Aliança.", criadoEm = agora - 5 * HORA),
        Relato(tipo = TipoFoco.IRREGULAR, sinal = Sinal.FUMACA, latitude = -22.0105, longitude = -46.8470,
            descricao = "Fumaça branca em pastagem, sem ninguém por perto.", criadoEm = agora - 26 * HORA),
        Relato(tipo = TipoFoco.CONTROLADA, sinal = Sinal.FUMACA, latitude = -21.9462, longitude = -46.7672,
            descricao = "Queima de manejo com aceiro, produtor acompanhando.", criadoEm = agora - 2 * HORA),
        Relato(tipo = TipoFoco.CONTROLADA, sinal = Sinal.FUMACA, latitude = -21.9320, longitude = -46.8040,
            descricao = "Pequena queima de restos de poda.", criadoEm = agora - 30 * HORA),
        Relato(tipo = TipoFoco.CONTROLADA, sinal = Sinal.FOGO, latitude = -21.9990, longitude = -46.8230,
            descricao = "Fogo baixo em área cercada, com caminhão-pipa.", criadoEm = agora - 50 * HORA),
    )
}
