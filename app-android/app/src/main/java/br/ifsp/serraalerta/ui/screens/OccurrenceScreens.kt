package br.ifsp.serraalerta.ui.screens

import android.content.Context
import android.text.format.DateUtils
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ifsp.serraalerta.domain.model.Ocorrencia
import br.ifsp.serraalerta.domain.model.StatusEnvio
import br.ifsp.serraalerta.ui.components.*
import br.ifsp.serraalerta.ui.navigation.Routes
import br.ifsp.serraalerta.ui.navigation.SerraAlertaViewModel
import br.ifsp.serraalerta.ui.theme.*
import java.util.Calendar

@Composable
fun ConfirmationScreen(
    occurrence: Ocorrencia?,
    context: Context,
    onShare: () -> Unit,
    onMap: () -> Unit
) {
    // Momento de conclusão: as curvas se abrem e o check cresce com mola, sem bloquear os botões.
    val escala = remember { Animatable(0f) }
    val curvas = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        curvas.animateTo(1f, tween(900))
    }
    LaunchedEffect(Unit) { escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
    BarrasSobreEscuro()

    Column(Modifier.fillMaxSize().background(Papel)) {
        Box(Modifier.fillMaxWidth().background(Carvao, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))) {
            TopoArt(BrasaViva, Modifier.matchParentSize(), centro = Offset(.2f, .45f), niveis = 12, progresso = curvas.value)
            Column(Modifier.statusBarsPadding().padding(horizontal = 24.dp).padding(top = 40.dp, bottom = 28.dp)) {
                Box(
                    Modifier.size(64.dp).scale(escala.value).background(BrasaViva, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, tint = Carvao, modifier = Modifier.size(34.dp))
                }
                Eyebrow("Salvo no aparelho", color = TextoSobreCarvaoSuave, modifier = Modifier.padding(top = 28.dp))
                Text("Relato registrado", style = MaterialTheme.typography.displaySmall, color = TextoSobreCarvao, modifier = Modifier.padding(top = 6.dp))
                Text(
                    "Sua ocorrência já aparece no mapa. Compartilhe agora com a Defesa Civil ou sua brigada para acelerar a resposta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSobreCarvaoSuave,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            occurrence?.let { item ->
                SerraCard(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        PhotoPreview(item.fotos.firstOrNull(), Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(item.categoria.rotulo, style = MaterialTheme.typography.titleMedium, color = Carvao)
                            Text(dataCurta(item.dataHoraRegistro), style = Dado, color = TextoSecundario)
                        }
                        CategoryGlyph(item.categoria, size = 32.dp)
                    }
                }
            }
            SerraCard(Modifier.fillMaxWidth(), container = BrasaSuave) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Rounded.Warning, contentDescription = null, tint = BrasaEscura, modifier = Modifier.size(20.dp))
                        Text("Há risco a pessoas, animais ou casas?", style = MaterialTheme.typography.titleSmall, color = BrasaEscura)
                    }
                    SerraButton("Ligar para os Bombeiros · 193", { abrirDiscador(context, "193") }, Modifier.fillMaxWidth(), tom = Tom.PERIGO, icon = Icons.Rounded.Call, height = 48.dp)
                }
            }
        }
        Column(Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 8.dp)) {
            SerraButton(
                "Compartilhar relato",
                onClick = { occurrence?.let { compartilharOcorrencia(context, it) }; onShare() },
                icon = Icons.Rounded.IosShare,
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(onClick = onMap, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Text("Voltar ao mapa", color = Carvao, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun DetailScreen(
    viewModel: SerraAlertaViewModel,
    id: String,
    context: Context,
    onShare: () -> Unit,
    onBack: () -> Unit
) {
    val occurrence by viewModel.observarPorId(id).collectAsStateWithLifecycle(initialValue = null)
    var confirmarExclusao by remember { mutableStateOf(false) }
    val item = occurrence
    if (item == null) {
        Column(Modifier.fillMaxSize().background(Papel)) {
            ScreenHeader("Ocorrência não encontrada.", onBack = onBack)
        }
        return
    }
    Box(Modifier.fillMaxSize().background(Papel)) {
        BarrasSobreEscuro()
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 120.dp)) {
            item {
                val pager = rememberPagerState { item.fotos.size.coerceAtLeast(1) }
                Box(Modifier.fillMaxWidth().height(380.dp)) {
                    HorizontalPager(pager) { pagina -> PhotoPreview(item.fotos.getOrNull(pagina), Modifier.fillMaxSize()) }
                    if (item.fotos.size > 1) {
                        Row(
                            Modifier.align(Alignment.BottomCenter).padding(bottom = 44.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(item.fotos.size) { i ->
                                Box(Modifier.size(if (i == pager.currentPage) 18.dp else 6.dp, 6.dp).background(Color.White.copy(alpha = if (i == pager.currentPage) 1f else .6f), CircleShape))
                            }
                        }
                    }
                }
            }
            item {
                Surface(
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = Papel,
                    modifier = Modifier.offset(y = (-28).dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CategoryGlyph(item.categoria, size = 28.dp)
                            Eyebrow("${formatarData(item.dataHoraRegistro, "dd/MM/yyyy · HH:mm")} · ${tempoRelativo(item.dataHoraRegistro)}", Modifier.weight(1f))
                        }
                        Text(item.categoria.rotulo, style = MaterialTheme.typography.displaySmall, color = Carvao)
                        StatusBadge(item.statusEnvio)
                        item.descricao?.takeIf { it.isNotBlank() }?.let {
                            Row(Modifier.height(IntrinsicSize.Min)) {
                                Box(Modifier.width(3.dp).fillMaxHeight().background(Brasa, CircleShape))
                                Text(it, style = MaterialTheme.typography.bodyLarge, color = Carvao, modifier = Modifier.padding(start = 14.dp))
                            }
                        }
                        SerraCard(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Box(Modifier.size(44.dp).background(Carvao, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = BrasaViva, modifier = Modifier.size(22.dp))
                                }
                                Column {
                                    Eyebrow("Localização")
                                    Text(formatarCoordenada(item.latitude, item.longitude), style = Dado, color = Carvao, modifier = Modifier.padding(top = 2.dp))
                                    Text(
                                        when {
                                            item.ajustadaManualmente -> "Posição ajustada manualmente"
                                            item.precisaoMetros != null -> "Precisão ±${item.precisaoMetros.toInt()} m · GPS"
                                            else -> "GPS"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextoSecundario
                                    )
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Rounded.Info, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(16.dp))
                            Text(
                                "Alerta preliminar enviado por um cidadão. Não substitui a verificação dos órgãos competentes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario
                            )
                        }
                    }
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconCircleButton(Icons.AutoMirrored.Rounded.ArrowBack, "Voltar", onBack, container = Carvao.copy(alpha = .55f), content = Color.White)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (item.fotos.size > 1) Pill("${item.fotos.size} fotos", icon = Icons.Rounded.PhotoLibrary, container = Carvao.copy(alpha = .55f), content = Color.White)
                IconCircleButton(Icons.Rounded.DeleteOutline, "Excluir relato", { confirmarExclusao = true }, container = Carvao.copy(alpha = .55f), content = Color.White)
            }
        }
        if (confirmarExclusao) {
            AlertDialog(
                onDismissRequest = { confirmarExclusao = false },
                containerColor = Superficie,
                icon = { Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = Perigo) },
                title = { Text("Excluir este relato?", style = MaterialTheme.typography.headlineSmall) },
                text = {
                    Text(
                        "O relato e ${if (item.fotos.size == 1) "a foto" else "as ${item.fotos.size} fotos"} serão apagados deste aparelho. Quem já recebeu o compartilhamento continua com a cópia.",
                        color = TextoSecundario
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        confirmarExclusao = false
                        viewModel.excluir(item)
                        onBack()
                    }) { Text("Excluir", color = Perigo) }
                },
                dismissButton = { TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar", color = Carvao) } }
            )
        }
        Surface(color = Papel, shadowElevation = 16.dp, modifier = Modifier.align(Alignment.BottomCenter)) {
            Row(
                Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SerraButton("193", { abrirDiscador(context, "193") }, tom = Tom.PERIGO, icon = Icons.Rounded.Call)
                SerraButton(
                    "Compartilhar",
                    onClick = { compartilharOcorrencia(context, item); onShare() },
                    icon = Icons.Rounded.IosShare,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MyReportsScreen(
    viewModel: SerraAlertaViewModel,
    onOccurrenceClick: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onRegister: () -> Unit
) {
    val occurrences by viewModel.ocorrencias.collectAsStateWithLifecycle()
    var soPendentes by rememberSaveable { mutableStateOf(false) }
    val compartilhados = occurrences.count { it.statusEnvio == StatusEnvio.COMPARTILHADO }
    val visiveis = if (soPendentes) occurrences.filter { it.statusEnvio != StatusEnvio.COMPARTILHADO } else occurrences

    Scaffold(
        containerColor = Papel,
        topBar = { ScreenHeader("Meus relatos", eyebrow = "Histórico salvo neste aparelho") },
        bottomBar = { SerraBottomBar(Routes.MEUS_RELATOS, onNavigate, onRegister) }
    ) { padding ->
        if (occurrences.isEmpty()) {
            Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
                Box(Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(28.dp)).background(Carvao)) {
                    TopoArt(BrasaViva.copy(alpha = .8f), Modifier.fillMaxSize(), centro = Offset(.5f, .55f), niveis = 10)
                    Icon(Icons.Rounded.PhotoCamera, contentDescription = null, tint = TextoSobreCarvao, modifier = Modifier.align(Alignment.Center).size(40.dp))
                }
                Text("Você ainda não registrou ocorrências.", style = MaterialTheme.typography.headlineSmall, color = Carvao, modifier = Modifier.padding(top = 24.dp))
                Text("Quando você registrar um alerta, ele aparecerá aqui e no mapa.", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario, modifier = Modifier.padding(top = 8.dp))
                SerraButton("Registrar ocorrência", onRegister, Modifier.fillMaxWidth().padding(top = 24.dp), icon = Icons.Rounded.PhotoCamera)
            }
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SerraCard(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(vertical = 16.dp).height(IntrinsicSize.Min)) {
                        Stat(occurrences.size, "relatos", Modifier.weight(1f))
                        Box(Modifier.width(1.dp).fillMaxHeight().background(Linha))
                        Stat(compartilhados, "compartilhados", Modifier.weight(1f))
                        Box(Modifier.width(1.dp).fillMaxHeight().background(Linha))
                        Stat(occurrences.size - compartilhados, "a compartilhar", Modifier.weight(1f), destaque = occurrences.size > compartilhados)
                    }
                }
            }
            item {
                Segmented(listOf("Todos", "A compartilhar"), if (soPendentes) 1 else 0, { soPendentes = it == 1 }, Modifier.padding(top = 6.dp))
            }
            if (visiveis.isEmpty()) {
                item {
                    Row(Modifier.padding(vertical = 28.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Seguro)
                        Text("Tudo compartilhado. Obrigado!", style = MaterialTheme.typography.titleSmall, color = Carvao)
                    }
                }
            }
            visiveis.groupBy { grupoData(it.dataHoraRegistro) }.forEach { (grupo, itens) ->
                item(key = grupo) { Eyebrow(grupo, Modifier.padding(top = 14.dp, bottom = 2.dp)) }
                items(itens, key = { it.id }) { occurrence ->
                    ReportListItem(occurrence, onClick = { onOccurrenceClick(occurrence.id) }, modifier = Modifier.animateItem())
                }
            }
        }
    }
}

@Composable
private fun Stat(valor: Int, rotulo: String, modifier: Modifier = Modifier, destaque: Boolean = false) {
    Column(modifier.padding(horizontal = 14.dp)) {
        Text("$valor", style = MaterialTheme.typography.headlineLarge, color = if (destaque) Brasa else Carvao)
        Text(rotulo, style = MaterialTheme.typography.labelSmall, color = TextoSecundario)
    }
}

@Composable
private fun ReportListItem(occurrence: Ocorrencia, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val hoje = DateUtils.isToday(occurrence.dataHoraRegistro)
    SerraCard(modifier.fillMaxWidth(), onClick = onClick) {
        Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box {
                PhotoPreview(occurrence.fotos.firstOrNull(), modifier = Modifier.size(72.dp).clip(RoundedCornerShape(16.dp)))
                CategoryGlyph(
                    occurrence.categoria,
                    size = 24.dp,
                    modifier = Modifier.align(Alignment.BottomEnd).offset(4.dp, 4.dp).border(2.dp, Superficie, RoundedCornerShape(8.dp))
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(occurrence.categoria.rotulo, style = MaterialTheme.typography.titleSmall, color = Carvao)
                Text(
                    "${formatarData(occurrence.dataHoraRegistro, if (hoje) "HH:mm" else "dd/MM · HH:mm")} · ${formatarCoordenada(occurrence.latitude, occurrence.longitude)}",
                    style = Rotulo,
                    color = TextoSecundario,
                    maxLines = 1
                )
                Box(Modifier.padding(top = 2.dp)) { StatusBadge(occurrence.statusEnvio) }
            }
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = LinhaForte, modifier = Modifier.size(18.dp))
        }
    }
}

private fun tempoRelativo(timestamp: Long): String =
    DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString()

private fun dataCurta(timestamp: Long): String =
    (if (DateUtils.isToday(timestamp)) "Hoje · " + formatarData(timestamp, "HH:mm") else formatarData(timestamp, "dd/MM · HH:mm"))

private fun grupoData(timestamp: Long): String {
    val ano = Calendar.getInstance().get(Calendar.YEAR)
    val anoDoRelato = Calendar.getInstance().apply { timeInMillis = timestamp }.get(Calendar.YEAR)
    return when {
        DateUtils.isToday(timestamp) -> "Hoje"
        DateUtils.isToday(timestamp + DateUtils.DAY_IN_MILLIS) -> "Ontem"
        else -> formatarData(timestamp, if (anoDoRelato == ano) "LLLL" else "LLLL 'de' yyyy").replaceFirstChar { it.uppercase() }
    }
}
