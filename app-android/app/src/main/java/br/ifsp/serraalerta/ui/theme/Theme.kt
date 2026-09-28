package br.ifsp.serraalerta.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Tema claro fixo: o app é usado ao ar livre, sob sol forte; telas críticas (splash, câmera,
// emergência) já são escuras por desenho.
private val Cores = lightColorScheme(
    primary = Brasa,
    onPrimary = Color.White,
    primaryContainer = BrasaSuave,
    onPrimaryContainer = BrasaEscura,
    secondary = Inpe,
    onSecondary = Color.White,
    secondaryContainer = InpeSuave,
    onSecondaryContainer = Color(0xFF103A46),
    tertiary = Seguro,
    background = Papel,
    onBackground = Carvao,
    surface = Papel,
    onSurface = Carvao,
    surfaceVariant = Superficie,
    onSurfaceVariant = TextoSecundario,
    surfaceContainerLowest = Superficie,
    surfaceContainerLow = Superficie,
    surfaceContainer = Superficie,
    surfaceContainerHigh = Superficie,
    surfaceContainerHighest = PapelFundo,
    inverseSurface = Carvao,
    inverseOnSurface = TextoSobreCarvao,
    outline = LinhaForte,
    outlineVariant = Linha,
    error = Perigo,
    scrim = Carvao
)

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun SerraAlertaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Cores, typography = Typography, shapes = Formas, content = content)
}
