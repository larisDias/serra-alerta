package br.ifsp.serraalerta.ui.theme

import androidx.compose.ui.graphics.Color
import br.ifsp.serraalerta.domain.model.CategoriaOcorrencia
import br.ifsp.serraalerta.domain.model.NivelFoco

// Sistema "carvão e brasa": papel quente para ler ao sol, carvão para momentos críticos,
// brasa para a ação principal e azul-petróleo exclusivo para o dado oficial do INPE.
val Papel = Color(0xFFF3EEE6)
val PapelFundo = Color(0xFFEAE3D7)
val Superficie = Color(0xFFFFFDF9)
val Linha = Color(0xFFE2D9CB)
val LinhaForte = Color(0xFFCFC3AE)

val Carvao = Color(0xFF161311)
val Carvao2 = Color(0xFF221E1A)
val Carvao3 = Color(0xFF342E28)
val TextoSecundario = Color(0xFF675E55)
val TextoSobreCarvao = Color(0xFFF3EEE6)
val TextoSobreCarvaoSuave = Color(0xFFA39A90)

val Brasa = Color(0xFFC2410C)
val BrasaEscura = Color(0xFF9A330A)
val BrasaViva = Color(0xFFFF7A45)
val BrasaSuave = Color(0xFFFBE4D7)

val Inpe = Color(0xFF1F5F73)
val InpeVivo = Color(0xFF7CC4D6)
val InpeSuave = Color(0xFFDCEEF2)

val Seguro = Color(0xFF2F6B3F)
val SeguroSuave = Color(0xFFE0EEDF)
val Perigo = Color(0xFFB91C1C)

val CategoriaOcorrencia.cor: Color
    get() = when (this) {
        CategoriaOcorrencia.QUEIMA_CONTROLADA -> Color(0xFFA16207)
        CategoriaOcorrencia.QUEIMADA_IRREGULAR -> Brasa
        CategoriaOcorrencia.INCENDIO_FLORESTAL -> Color(0xFF991B1B)
        CategoriaOcorrencia.FUMACA_NAO_IDENTIFICADA -> Color(0xFF57534E)
    }

/** Escala de intensidade dos focos do INPE: amarelo → laranja → vermelho → vinho. */
val NivelFoco.cor: Color
    get() = when (this) {
        NivelFoco.BAIXO -> Color(0xFFEAB308)
        NivelFoco.MODERADO -> Color(0xFFF97316)
        NivelFoco.ALTO -> Color(0xFFDC2626)
        NivelFoco.EXTREMO -> Color(0xFF7F1D1D)
        NivelFoco.DESCONHECIDO -> Color(0xFF78716C)
    }
