package br.ifsp.serraalerta.domain.model

import java.util.UUID

enum class CategoriaOcorrencia(val valor: String, val rotulo: String) {
    QUEIMA_CONTROLADA("queima_controlada", "Queima controlada"),
    QUEIMADA_IRREGULAR("queimada_irregular", "Queimada irregular"),
    INCENDIO_FLORESTAL("incendio_florestal", "Incêndio florestal"),
    FUMACA_NAO_IDENTIFICADA("fumaca_nao_identificada", "Fumaça não identificada");

    companion object {
        fun fromValor(valor: String): CategoriaOcorrencia =
            entries.firstOrNull { it.valor == valor } ?: FUMACA_NAO_IDENTIFICADA
    }
}

enum class StatusEnvio(val valor: String, val rotulo: String) {
    REGISTRADO_LOCALMENTE("registrado_localmente", "Salvo no aparelho"),
    COMPARTILHADO("compartilhado", "Compartilhado");

    companion object {
        fun fromValor(valor: String): StatusEnvio =
            entries.firstOrNull { it.valor == valor } ?: REGISTRADO_LOCALMENTE
    }
}

data class Ocorrencia(
    val id: String = UUID.randomUUID().toString(),
    val latitude: Double,
    val longitude: Double,
    val categoria: CategoriaOcorrencia,
    val descricao: String?,
    val fotos: List<String>,
    val dataHoraRegistro: Long,
    val statusEnvio: StatusEnvio = StatusEnvio.REGISTRADO_LOCALMENTE,
    val precisaoMetros: Float? = null,
    val ajustadaManualmente: Boolean = false
)

/** Intensidade do foco pela potência radiativa do fogo (FRP, em MW). */
enum class NivelFoco(val rotulo: String) {
    BAIXO("Baixa"),
    MODERADO("Moderada"),
    ALTO("Alta"),
    EXTREMO("Extrema"),
    DESCONHECIDO("Não informada");

    companion object {
        fun deFrp(frp: Double?): NivelFoco = when {
            frp == null || frp < 0 -> DESCONHECIDO
            frp < 10 -> BAIXO
            frp < 50 -> MODERADO
            frp < 100 -> ALTO
            else -> EXTREMO
        }
    }
}

data class FocoOficial(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val dataDeteccao: Long,
    val frp: Double?,
    val fonte: String
) {
    val nivel: NivelFoco get() = NivelFoco.deFrp(frp)
}
