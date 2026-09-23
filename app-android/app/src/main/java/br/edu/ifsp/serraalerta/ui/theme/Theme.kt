package br.edu.ifsp.serraalerta.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Laranja = Color(0xFFD9482B)
val LaranjaEscuro = Color(0xFFB23A21)
val Carvao = Color(0xFF1E1B18)
val Areia = Color(0xFFFAF7F2)
val Superficie = Color(0xFFFFFFFF)
val Texto2 = Color(0xFF6B635C)
val Borda = Color(0xFFE9E2D9)
val Verde = Color(0xFF2F6B4F)

private val Esquema = lightColorScheme(
    primary = Laranja,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE3D9),
    onPrimaryContainer = LaranjaEscuro,
    secondary = Verde,
    background = Areia,
    onBackground = Carvao,
    surface = Superficie,
    onSurface = Carvao,
    surfaceVariant = Color(0xFFF3EEE7),
    onSurfaceVariant = Texto2,
    outline = Borda,
)

@Composable
fun SerraAlertaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Esquema, content = content)
}
