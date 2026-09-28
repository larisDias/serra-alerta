package br.ifsp.serraalerta.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val MAPA = "mapa"
    const val REGISTRO = "registro"
    const val CAMERA = "camera"
    const val AJUSTAR_LOCALIZACAO = "ajustar-localizacao"
    const val CONFIRMACAO = "confirmacao/{id}"
    const val DETALHES = "detalhes/{id}"
    const val FOCO_OFICIAL = "foco-oficial"
    const val MEUS_RELATOS = "meus-relatos"
    const val EDUCACAO = "educacao"
    const val EMERGENCIA = "emergencia"
    const val CONFIGURACOES = "configuracoes"
    const val SOBRE = "sobre"

    fun confirmacao(id: String) = "confirmacao/$id"
    fun detalhes(id: String) = "detalhes/$id"
}
