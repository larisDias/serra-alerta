package br.ifsp.serraalerta.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// ponytail: fontes do sistema; trocar Display/Corpo/Mono por fontes em res/font sem mexer nas telas.
val FonteDisplay = FontFamily.SansSerif
val FonteCorpo = FontFamily.SansSerif
val FonteMono = FontFamily.Monospace

private fun display(size: Int, line: Int, weight: FontWeight = FontWeight.Bold) = TextStyle(
    fontFamily = FonteDisplay, fontWeight = weight, fontSize = size.sp, lineHeight = line.sp, letterSpacing = (-0.03).em
)

private fun corpo(size: Int, line: Int, weight: FontWeight = FontWeight.Normal, spacing: Double = 0.0) = TextStyle(
    fontFamily = FonteCorpo, fontWeight = weight, fontSize = size.sp, lineHeight = line.sp, letterSpacing = spacing.em
)

val Typography = Typography(
    displayLarge = display(56, 56, FontWeight.ExtraBold),
    displayMedium = display(44, 46, FontWeight.ExtraBold),
    displaySmall = display(36, 38),
    headlineLarge = display(32, 36),
    headlineMedium = display(28, 32),
    headlineSmall = display(24, 28),
    titleLarge = corpo(20, 26, FontWeight.SemiBold, -0.01),
    titleMedium = corpo(16, 22, FontWeight.SemiBold),
    titleSmall = corpo(14, 20, FontWeight.SemiBold),
    bodyLarge = corpo(16, 24),
    bodyMedium = corpo(14, 21),
    bodySmall = corpo(13, 18),
    labelLarge = corpo(15, 20, FontWeight.SemiBold),
    labelMedium = corpo(13, 16, FontWeight.Medium),
    labelSmall = corpo(11, 14, FontWeight.Medium)
)

/** Rótulos técnicos: coordenadas, horários, passos e fontes de dado. */
val Rotulo = TextStyle(fontFamily = FonteMono, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.08.em)
val Dado = TextStyle(fontFamily = FonteMono, fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 18.sp)
