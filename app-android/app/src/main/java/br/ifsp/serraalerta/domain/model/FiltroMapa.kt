package br.ifsp.serraalerta.domain.model

private const val DIA = 24L * 60L * 60L * 1_000L

enum class Periodo(val rotulo: String, val dias: Int) {
    HORAS_24("24 h", 1),
    DIAS_7("7 dias", 7),
    DIAS_30("30 dias", 30),
    DATAS("Datas", 0)
}

/** [inicio] e [fim] são meia-noite local do primeiro e do último dia escolhidos. */
data class FiltroMapa(
    val relatos: Boolean = true,
    val focos: Boolean = true,
    val categorias: Set<CategoriaOcorrencia> = CategoriaOcorrencia.entries.toSet(),
    val periodo: Periodo = Periodo.DIAS_7,
    val inicio: Long? = null,
    val fim: Long? = null
) {
    fun noPeriodo(dataHora: Long, agora: Long): Boolean = when (periodo) {
        Periodo.DATAS -> dataHora >= (inicio ?: 0L) && dataHora < (fim?.plus(DIA) ?: Long.MAX_VALUE)
        else -> dataHora >= agora - periodo.dias * DIA
    }

    fun inclui(ocorrencia: Ocorrencia, agora: Long): Boolean =
        relatos && ocorrencia.categoria in categorias && noPeriodo(ocorrencia.dataHoraRegistro, agora)

    fun inclui(foco: FocoOficial, agora: Long): Boolean = focos && noPeriodo(foco.dataDeteccao, agora)
}
