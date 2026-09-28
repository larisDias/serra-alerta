package br.ifsp.serraalerta.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ListAlt
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.ifsp.serraalerta.domain.model.CategoriaOcorrencia
import br.ifsp.serraalerta.domain.model.NivelFoco
import br.ifsp.serraalerta.domain.model.StatusEnvio
import br.ifsp.serraalerta.ui.navigation.Routes
import br.ifsp.serraalerta.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PtBr = Locale.forLanguageTag("pt-BR")

fun formatarData(timestamp: Long, padrao: String): String = SimpleDateFormat(padrao, PtBr).format(Date(timestamp))

fun formatarCoordenada(latitude: Double, longitude: Double): String =
    "%.4f, %.4f".format(Locale.US, latitude, longitude)

/** Rótulo técnico em caixa alta e fonte mono: passos, fontes de dado, seções. */
@Composable
fun Eyebrow(text: String, modifier: Modifier = Modifier, color: Color = TextoSecundario) {
    Text(text.uppercase(PtBr), style = Rotulo, color = color, modifier = modifier)
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Eyebrow(text, modifier.padding(bottom = 2.dp))
}

enum class Tom { BRASA, CARVAO, CONTORNO, PERIGO, INPE, CLARO }

/** Botão da casa: 56 dp, cantos de 18 dp e leve compressão ao toque. */
@Composable
fun SerraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tom: Tom = Tom.BRASA,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    height: Dp = 56.dp
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val escala by animateFloatAsState(if (pressed) .97f else 1f, spring(stiffness = 900f), label = "escala_botao")
    val (fundo, conteudo) = when (tom) {
        Tom.BRASA -> Brasa to Color.White
        Tom.CARVAO -> Carvao to TextoSobreCarvao
        Tom.CONTORNO -> Color.Transparent to Carvao
        Tom.PERIGO -> Perigo to Color.White
        Tom.INPE -> Inpe to Color.White
        Tom.CLARO -> Superficie to Carvao
    }
    val cor by animateColorAsState(if (enabled) fundo else PapelFundo, label = "fundo_botao")
    Surface(
        onClick = onClick,
        enabled = enabled && !loading,
        interactionSource = interaction,
        shape = RoundedCornerShape(18.dp),
        color = cor,
        contentColor = if (enabled) conteudo else TextoSecundario,
        border = if (tom == Tom.CONTORNO) BorderStroke(1.dp, LinhaForte) else null,
        modifier = modifier.height(height).graphicsLayer { scaleX = escala; scaleY = escala }
    ) {
        Row(
            Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                loading -> CircularProgressIndicator(color = conteudo, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                icon != null -> Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun IconCircleButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = Superficie,
    content: Color = Carvao,
    size: Dp = 44.dp,
    border: Boolean = container == Superficie
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = container,
        contentColor = content,
        border = if (border) BorderStroke(1.dp, Linha) else null,
        modifier = modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(size * .45f))
        }
    }
}

/** Cartão de superfície clara com filete: base de quase todos os blocos de conteúdo. */
@Composable
fun SerraCard(
    modifier: Modifier = Modifier,
    container: Color = Superficie,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(22.dp)
    val borda = if (container == Superficie) BorderStroke(1.dp, Linha) else null
    if (onClick != null) {
        Surface(onClick = onClick, shape = forma, color = container, border = borda, modifier = modifier) { Column(content = content) }
    } else {
        Surface(shape = forma, color = container, border = borda, modifier = modifier) { Column(content = content) }
    }
}

@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    container: Color = Superficie,
    content: Color = Carvao,
    dot: Color? = null
) {
    Surface(shape = CircleShape, color = container, contentColor = content, modifier = modifier) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            dot?.let { Box(Modifier.size(7.dp).background(it, CircleShape)) }
            icon?.let { Icon(it, contentDescription = null, modifier = Modifier.size(15.dp)) }
            Text(text, style = MaterialTheme.typography.labelMedium, maxLines = 1)
        }
    }
}

val CategoriaOcorrencia.icone: ImageVector
    get() = when (this) {
        CategoriaOcorrencia.QUEIMA_CONTROLADA -> Icons.Rounded.Agriculture
        CategoriaOcorrencia.QUEIMADA_IRREGULAR -> Icons.Rounded.LocalFireDepartment
        CategoriaOcorrencia.INCENDIO_FLORESTAL -> Icons.Rounded.Forest
        CategoriaOcorrencia.FUMACA_NAO_IDENTIFICADA -> Icons.Rounded.Cloud
    }

val CategoriaOcorrencia.resumo: String
    get() = when (this) {
        CategoriaOcorrencia.QUEIMA_CONTROLADA -> "Fogo planejado, autorizado e acompanhado"
        CategoriaOcorrencia.QUEIMADA_IRREGULAR -> "Lixo ou pasto queimado sem autorização"
        CategoriaOcorrencia.INCENDIO_FLORESTAL -> "Fogo sem controle em mata ou vegetação"
        CategoriaOcorrencia.FUMACA_NAO_IDENTIFICADA -> "Vejo fumaça, mas não sei a origem"
    }

@Composable
fun CategoryGlyph(categoria: CategoriaOcorrencia, modifier: Modifier = Modifier, size: Dp = 40.dp) {
    Box(
        modifier.size(size).clip(RoundedCornerShape(size * .3f)).background(categoria.cor),
        contentAlignment = Alignment.Center
    ) {
        Icon(categoria.icone, contentDescription = null, tint = Color.White, modifier = Modifier.size(size * .5f))
    }
}

@Composable
fun StatusBadge(status: StatusEnvio) {
    val compartilhado = status == StatusEnvio.COMPARTILHADO
    Pill(
        text = if (compartilhado) "Compartilhado" else "A compartilhar",
        icon = if (compartilhado) Icons.Rounded.Check else null,
        dot = if (compartilhado) null else Brasa,
        container = if (compartilhado) SeguroSuave else BrasaSuave,
        content = if (compartilhado) Seguro else BrasaEscura
    )
}

@Composable
fun PermissionExplanation(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    SerraCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
            SerraButton(actionLabel, onAction, Modifier.fillMaxWidth().padding(top = 4.dp))
        }
    }
}

/** Cabeçalho editorial: ação de voltar, rótulo técnico e título grande. */
@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp).padding(top = 8.dp, bottom = 12.dp)) {
        if (onBack != null) {
            Row(Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
                IconCircleButton(Icons.AutoMirrored.Rounded.ArrowBack, "Voltar", onBack)
                Spacer(Modifier.weight(1f))
                actions()
            }
        }
        eyebrow?.let { Eyebrow(it, Modifier.padding(top = if (onBack != null) 16.dp else 12.dp)) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.headlineLarge, color = Carvao, modifier = Modifier.weight(1f).padding(top = 6.dp))
            if (onBack == null) actions()
        }
    }
}

/** Controle segmentado em trilho de papel; o item ativo sobe como cartão. */
@Composable
fun Segmented(options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().background(PapelFundo, RoundedCornerShape(16.dp)).padding(4.dp)) {
        options.forEachIndexed { i, rotulo ->
            val ativo = i == selected
            Surface(
                onClick = { onSelect(i) },
                shape = RoundedCornerShape(12.dp),
                color = if (ativo) Superficie else Color.Transparent,
                shadowElevation = if (ativo) 2.dp else 0.dp,
                modifier = Modifier.weight(1f).height(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(rotulo, style = MaterialTheme.typography.labelMedium, color = if (ativo) Carvao else TextoSecundario)
                }
            }
        }
    }
}

val SerraSwitchColors: SwitchColors
    @Composable get() = SwitchDefaults.colors(
        checkedThumbColor = Superficie,
        checkedTrackColor = Carvao,
        checkedBorderColor = Carvao,
        uncheckedThumbColor = TextoSecundario,
        uncheckedTrackColor = PapelFundo,
        uncheckedBorderColor = LinhaForte
    )

private data class Aba(val rota: String, val rotulo: String, val icone: ImageVector)

private val Abas = listOf(
    Aba(Routes.MAPA, "Mapa", Icons.Rounded.Map),
    Aba(Routes.MEUS_RELATOS, "Relatos", Icons.AutoMirrored.Rounded.ListAlt),
    Aba(Routes.EDUCACAO, "Prevenção", Icons.Rounded.Spa),
    Aba(Routes.EMERGENCIA, "193", Icons.Rounded.Call)
)

/** Altura reservada para o dock flutuante, sem contar a barra de navegação do sistema. */
val DockAltura = 100.dp

/** Dock flutuante em carvão com o registro em destaque no centro. */
@Composable
fun SerraBottomBar(atual: String, onNavigate: (String) -> Unit, onRegister: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().navigationBarsPadding().padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
        Surface(shape = RoundedCornerShape(28.dp), color = Carvao, shadowElevation = 12.dp, border = BorderStroke(1.dp, Carvao3), modifier = Modifier.fillMaxWidth().height(72.dp)) {
            Row(Modifier.fillMaxSize().padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Abas.take(2).forEach { DockItem(it, it.rota == atual, onNavigate, Modifier.weight(1f)) }
                Box(Modifier.weight(1.2f), contentAlignment = Alignment.Center) {
                    Surface(
                        onClick = onRegister,
                        shape = RoundedCornerShape(20.dp),
                        color = Brasa,
                        contentColor = Color.White,
                        modifier = Modifier.size(width = 64.dp, height = 52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.PhotoCamera, contentDescription = "Registrar ocorrência", modifier = Modifier.size(26.dp))
                        }
                    }
                }
                Abas.drop(2).forEach { DockItem(it, it.rota == atual, onNavigate, Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun DockItem(aba: Aba, selecionada: Boolean, onNavigate: (String) -> Unit, modifier: Modifier) {
    val cor by animateColorAsState(if (selecionada) TextoSobreCarvao else TextoSobreCarvaoSuave, label = "cor_aba")
    val emergencia = aba.rota == Routes.EMERGENCIA
    Surface(
        onClick = { if (!selecionada) onNavigate(aba.rota) },
        color = Color.Transparent,
        contentColor = if (emergencia && !selecionada) BrasaViva else cor,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.height(60.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(aba.icone, contentDescription = null, modifier = Modifier.size(22.dp))
            Text(aba.rotulo, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 3.dp))
            Box(Modifier.padding(top = 3.dp).size(4.dp).background(if (selecionada) BrasaViva else Color.Transparent, CircleShape))
        }
    }
}

/** Ícones claros nas barras do sistema enquanto a tela escura estiver visível. */
@Composable
fun BarrasSobreEscuro() {
    val view = LocalView.current
    if (view.isInEditMode) return
    DisposableEffect(view) {
        val window = (view.context as? android.app.Activity)?.window ?: return@DisposableEffect onDispose {}
        val controle = WindowCompat.getInsetsController(window, view)
        controle.isAppearanceLightStatusBars = false
        controle.isAppearanceLightNavigationBars = false
        onDispose {
            controle.isAppearanceLightStatusBars = true
            controle.isAppearanceLightNavigationBars = true
        }
    }
}

private val FaixasFrp = listOf(
    NivelFoco.BAIXO to "< 10",
    NivelFoco.MODERADO to "10–50",
    NivelFoco.ALTO to "50–100",
    NivelFoco.EXTREMO to "≥ 100"
)

/** Legenda de intensidade dos focos (FRP em MW); [atual] destaca o nível de um foco. */
@Composable
fun EscalaIntensidade(modifier: Modifier = Modifier, atual: NivelFoco? = null) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        FaixasFrp.forEach { (nivel, faixa) ->
            val ativo = atual == null || atual == nivel
            Column(
                Modifier.weight(1f)
                    .background(if (atual == nivel) nivel.cor.copy(alpha = .14f) else Color.Transparent, RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Rounded.LocalFireDepartment,
                    contentDescription = null,
                    tint = nivel.cor.copy(alpha = if (ativo) 1f else .3f),
                    modifier = Modifier.size(24.dp)
                )
                Text(nivel.rotulo, style = MaterialTheme.typography.labelSmall, color = if (ativo) Carvao else TextoSecundario)
                Text(faixa, style = Rotulo, color = TextoSecundario)
            }
        }
    }
}
