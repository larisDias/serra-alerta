package br.ifsp.serraalerta

import android.content.Context

class Preferences(context: Context) {
    private val preferences = context.getSharedPreferences("serra_alerta_prefs", Context.MODE_PRIVATE)

    fun onboardingConcluido(): Boolean = preferences.getBoolean("onboarding_concluido", false)

    fun marcarOnboardingConcluido() {
        preferences.edit().putBoolean("onboarding_concluido", true).apply()
    }

    var gpsAltaPrecisao: Boolean
        get() = preferences.getBoolean("gps_alta_precisao", true)
        set(value) = preferences.edit().putBoolean("gps_alta_precisao", value).apply()

    var atualizarFocosAoAbrir: Boolean
        get() = preferences.getBoolean("atualizar_focos_ao_abrir", true)
        set(value) = preferences.edit().putBoolean("atualizar_focos_ao_abrir", value).apply()

    var ultimaAtualizacaoFocos: Long
        get() = preferences.getLong("ultima_atualizacao_focos", 0L)
        set(value) = preferences.edit().putLong("ultima_atualizacao_focos", value).apply()
}
