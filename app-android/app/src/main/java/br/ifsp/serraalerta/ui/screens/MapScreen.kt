package br.ifsp.serraalerta.ui.screens

import android.Manifest
import android.text.format.DateUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ifsp.serraalerta.domain.model.CategoriaOcorrencia
import br.ifsp.serraalerta.domain.model.FiltroMapa
import br.ifsp.serraalerta.domain.model.FocoOficial
import br.ifsp.serraalerta.domain.model.Ocorrencia
import br.ifsp.serraalerta.domain.model.Periodo
import br.ifsp.serraalerta.location.Coordenada
import br.ifsp.serraalerta.ui.components.*
import br.ifsp.serraalerta.ui.navigation.EstadoFocos
import br.ifsp.serraalerta.ui.navigation.Routes
import br.ifsp.serraalerta.ui.navigation.SerraAlertaViewModel
import br.ifsp.serraalerta.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

@Composable
fun MapScreen(
    viewModel: SerraAlertaViewModel,
    onNavigate: (String) -> Unit,
    onSettings: () -> Unit,
    onOccurrenceClick: (String) -> Unit,
    onFocoClick: (FocoOficial) -> Unit,
    onRegister: () -> Unit
) {
    val occurrences by viewModel.ocorrencias.collectAsStateWithLifecycle()
    val officialFocuses by viewModel.focosOficiais.collectAsStateWithLifecycle()
    val filtro by viewModel.filtro.collectAsStateWithLifecycle()
    val estadoFocos by viewModel.estadoFocos.collectAsStateWithLifecycle()
    val ultimaAtualizacao by viewModel.ultimaAtualizacaoFocos.collectAsStateWithLifecycle()
    var showFilters by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var minhaPosicao by remember { mutableStateOf<Coordenada?>(null) }
    var centralizar by remember { mutableIntStateOf(0) }
    var localizando by remember { mutableStateOf(false) }
    val localizar = {
        localizando = true
        scope.launch {
            viewModel.localizacaoAtual()
                .onSuccess { minhaPosicao = it; centralizar++ }
                .onFailure { Toast.makeText(context, "Não foi possível obter sua localização.", Toast.LENGTH_SHORT).show() }
            localizando = false
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (context.hasLocationPermission()) localizar()
    }
    // Relógio de minuto em minuto para "atualizados há N min" e para os períodos relativos.
    val agora by produceState(System.currentTimeMillis()) {
        while (true) {
            delay(60_000)
            value = System.currentTimeMillis()
        }
    }

    LaunchedEffect(Unit) { viewModel.aoAbrirMapa() }

    val relatosVisiveis = occurrences.filter { filtro.inclui(it, agora) }
    val focosVisiveis = officialFocuses.filter { filtro.inclui(it, agora) }

    Box(Modifier.fillMaxSize().background(Papel)) {
        SerraMapView(
            ocorrencias = relatosVisiveis,
            focosOficiais = focosVisiveis,
            modifier = Modifier.fillMaxSize(),
            minhaPosicao = minhaPosicao,
            centralizar = centralizar,
            localizando = localizando,
            controlsPadding = PaddingValues(end = 16.dp, top = 40.dp),
            onLocalizar = {
                if (context.hasLocationPermission()) localizar()
                else permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            },
            onOcorrenciaClick = onOccurrenceClick,
            onFocoClick = onFocoClick
        )

        Column(Modifier.fillMaxWidth().statusBarsPadding().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Superficie,
                border = BorderStroke(1.dp, Linha),
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Row(Modifier.padding(start = 10.dp, end = 8.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).background(Carvao, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = BrasaViva, modifier = Modifier.size(22.dp))
                    }
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text("Serra Alerta", style = MaterialTheme.typography.titleMedium, color = Carvao)
                        Eyebrow("São João da Boa Vista · SP")
                    }
                    IconCircleButton(Icons.Rounded.Settings, "Configurações", onSettings, border = false)
                }
            }
            Row(
                Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MapChip(rotuloPeriodo(filtro), Icons.Rounded.Schedule, ativo = true) { showFilters = true }
                MapChip(rotuloCategorias(filtro.categorias), Icons.Rounded.Tune) { showFilters = true }
                MapChip(
                    "Satélite INPE",
                    Icons.Rounded.SatelliteAlt,
                    ativo = filtro.focos,
                    corAtiva = Inpe
                ) { viewModel.filtro.value = filtro.copy(focos = !filtro.focos) }
            }
            if (filtro.focos) {
                FocosStatus(estadoFocos, ultimaAtualizacao, agora, Modifier.padding(horizontal = 16.dp), onRefresh = viewModel::atualizarFocosOficiais)
            }
        }

        Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            Legenda(relatosVisiveis.size, if (filtro.focos) focosVisiveis.size else null, Modifier.padding(start = 16.dp, bottom = 10.dp))
            SerraBottomBar(Routes.MAPA, onNavigate, onRegister)
        }
    }

    if (showFilters) {
        FiltersSheet(
            atual = filtro,
            ocorrencias = occurrences,
            agora = agora,
            onApply = { viewModel.filtro.value = it; showFilters = false },
            onDismiss = { showFilters = false }
        )
    }
}

@Composable
private fun MapChip(label: String, icon: ImageVector, ativo: Boolean = false, corAtiva: Color = Carvao, onClick: () -> Unit) {
    val fundo by animateColorAsState(if (ativo) corAtiva else Superficie, label = "fundo_chip")
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = fundo,
        contentColor = if (ativo) Color.White else Carvao,
        border = if (ativo) null else BorderStroke(1.dp, Linha),
        shadowElevation = 3.dp,
        modifier = Modifier.height(40.dp)
    ) {
        Row(Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(17.dp))
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun Legenda(relatos: Int, focos: Int?, modifier: Modifier = Modifier) {
    Surface(shape = CircleShape, color = Superficie.copy(alpha = .94f), border = BorderStroke(1.dp, Linha), modifier = modifier) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = Brasa, modifier = Modifier.size(16.dp))
            Text("$relatos ${if (relatos == 1) "relato" else "relatos"}", style = Dado, color = Carvao)
            if (focos != null) {
                Icon(Icons.Rounded.SatelliteAlt, contentDescription = null, tint = Inpe, modifier = Modifier.padding(start = 6.dp).size(15.dp))
                Text("$focos ${if (focos == 1) "foco" else "focos"} INPE", style = Dado, color = Carvao)
            }
        }
    }
}

@Composable
private fun FocosStatus(estado: EstadoFocos, ultima: Long, agora: Long, modifier: Modifier = Modifier, onRefresh: () -> Unit) {
    val texto = when {
        estado == EstadoFocos.ATUALIZANDO -> "Atualizando focos do INPE…"
        estado == EstadoFocos.FALHOU -> "Focos INPE indisponíveis · toque para tentar"
        ultima == 0L -> "Focos INPE não carregados · toque para atualizar"
        agora - ultima < 60_000 -> "Focos INPE atualizados agora"
        else -> "Focos INPE atualizados " + DateUtils.getRelativeTimeSpanString(ultima, agora, DateUtils.MINUTE_IN_MILLIS)
    }
    Surface(
        onClick = onRefresh,
        enabled = estado != EstadoFocos.ATUALIZANDO,
        shape = CircleShape,
        color = Carvao.copy(alpha = .86f),
        contentColor = TextoSobreCarvao,
        modifier = modifier
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 7.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            when (estado) {
                EstadoFocos.ATUALIZANDO -> CircularProgressIndicator(color = InpeVivo, strokeWidth = 1.5.dp, modifier = Modifier.size(12.dp))
                EstadoFocos.FALHOU -> Icon(Icons.Rounded.CloudOff, contentDescription = null, tint = BrasaViva, modifier = Modifier.size(14.dp))
                EstadoFocos.OCIOSO -> Box(Modifier.size(7.dp).background(InpeVivo, CircleShape))
            }
            Text(texto, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersSheet(
    atual: FiltroMapa,
    ocorrencias: List<Ocorrencia>,
    agora: Long,
    onApply: (FiltroMapa) -> Unit,
    onDismiss: () -> Unit
) {
    var rascunho by remember { mutableStateOf(atual) }
    var escolhendoDatas by remember { mutableStateOf(false) }
    val quantidade = ocorrencias.count { rascunho.inclui(it, agora) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Papel,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = { Box(Modifier.padding(top = 12.dp, bottom = 4.dp).size(40.dp, 4.dp).background(LinhaForte, CircleShape)) }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState()).navigationBarsPadding().padding(horizontal = 20.dp)) {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Filtros e camadas", style = MaterialTheme.typography.headlineSmall, color = Carvao, modifier = Modifier.weight(1f))
                TextButton(onClick = { rascunho = FiltroMapa() }) { Text("Limpar", color = Brasa, style = MaterialTheme.typography.labelLarge) }
            }

            SectionTitle("Camadas", Modifier.padding(top = 20.dp, bottom = 8.dp))
            SerraCard(Modifier.fillMaxWidth()) {
                CamadaRow("Relatos da comunidade", "Cor da chama = categoria", rascunho.relatos, {
                    Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = Brasa, modifier = Modifier.size(22.dp))
                }) { rascunho = rascunho.copy(relatos = it) }
                Box(Modifier.fillMaxWidth().padding(start = 60.dp).height(1.dp).background(Linha))
                CamadaRow("Focos de calor oficiais", "INPE · BDQueimadas · requer internet", rascunho.focos, {
                    Icon(Icons.Rounded.SatelliteAlt, contentDescription = null, tint = Inpe, modifier = Modifier.size(20.dp))
                }) { rascunho = rascunho.copy(focos = it) }
                Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp)) {
                    Eyebrow("Intensidade do foco · FRP em MW", Modifier.padding(start = 44.dp, bottom = 6.dp))
                    EscalaIntensidade()
                }
            }

            SectionTitle("Período", Modifier.padding(top = 22.dp, bottom = 8.dp))
            Row(Modifier.fillMaxWidth().background(PapelFundo, RoundedCornerShape(16.dp)).padding(4.dp)) {
                Periodo.entries.forEach { periodo ->
                    val selecionado = rascunho.periodo == periodo
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selecionado) Superficie else Color.Transparent,
                        shadowElevation = if (selecionado) 2.dp else 0.dp,
                        modifier = Modifier.weight(1f).height(40.dp).selectable(selecionado, role = Role.RadioButton) {
                            if (periodo == Periodo.DATAS) escolhendoDatas = true else rascunho = rascunho.copy(periodo = periodo)
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(periodo.rotulo, style = MaterialTheme.typography.labelMedium, color = if (selecionado) Carvao else TextoSecundario)
                        }
                    }
                }
            }
            if (rascunho.periodo == Periodo.DATAS) {
                Text(rotuloPeriodo(rascunho), style = Dado, color = TextoSecundario, modifier = Modifier.padding(top = 8.dp))
            }

            SectionTitle("Categorias", Modifier.padding(top = 22.dp, bottom = 8.dp))
            CategoriaOcorrencia.entries.chunked(2).forEach { linha ->
                Row(Modifier.padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    linha.forEach { categoria ->
                        val marcada = categoria in rascunho.categorias
                        val total = ocorrencias.count { it.categoria == categoria && rascunho.noPeriodo(it.dataHoraRegistro, agora) }
                        CategoriaTile(categoria, total, marcada, Modifier.weight(1f)) {
                            rascunho = rascunho.copy(categorias = if (marcada) rascunho.categorias - categoria else rascunho.categorias + categoria)
                        }
                    }
                }
            }

            SerraButton(
                text = if (rascunho.relatos) "Mostrar $quantidade ${if (quantidade == 1) "relato" else "relatos"}" else "Mostrar só focos do INPE",
                onClick = { onApply(rascunho) },
                tom = Tom.CARVAO,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 16.dp)
            )
        }
    }

    if (escolhendoDatas) {
        val state = rememberDateRangePickerState(initialSelectedStartDateMillis = null, initialSelectedEndDateMillis = null)
        DatePickerDialog(
            onDismissRequest = { escolhendoDatas = false },
            confirmButton = {
                TextButton(
                    enabled = state.selectedStartDateMillis != null,
                    onClick = {
                        val inicio = state.selectedStartDateMillis?.let(::utcParaDiaLocal)
                        val fim = (state.selectedEndDateMillis ?: state.selectedStartDateMillis)?.let(::utcParaDiaLocal)
                        rascunho = rascunho.copy(periodo = Periodo.DATAS, inicio = inicio, fim = fim)
                        escolhendoDatas = false
                    }
                ) { Text("Aplicar") }
            },
            dismissButton = { TextButton(onClick = { escolhendoDatas = false }) { Text("Cancelar") } }
        ) {
            DateRangePicker(state = state, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CamadaRow(titulo: String, descricao: String, marcado: Boolean, marca: @Composable () -> Unit, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().toggleable(marcado, role = Role.Switch, onValueChange = onChange).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) { marca() }
        Column(Modifier.weight(1f).padding(start = 16.dp, end = 12.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleSmall, color = Carvao)
            Text(descricao, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
        }
        Switch(checked = marcado, onCheckedChange = null, colors = SerraSwitchColors)
    }
}

@Composable
private fun CategoriaTile(categoria: CategoriaOcorrencia, total: Int, marcada: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val borda by animateColorAsState(if (marcada) Carvao else Linha, label = "borda_categoria")
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Superficie,
        border = BorderStroke(if (marcada) 1.5.dp else 1.dp, borda),
        modifier = modifier.toggleable(marcada, role = Role.Checkbox, onValueChange = { onClick() })
    ) {
        Column(Modifier.padding(14.dp).alpha(if (marcada) 1f else .5f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryGlyph(categoria, size = 30.dp)
                Spacer(Modifier.weight(1f))
                Text("$total", style = Dado, color = TextoSecundario)
            }
            Text(categoria.rotulo, style = MaterialTheme.typography.labelMedium, color = Carvao, modifier = Modifier.padding(top = 12.dp), minLines = 2)
        }
    }
}

private fun rotuloPeriodo(filtro: FiltroMapa): String = when (filtro.periodo) {
    Periodo.HORAS_24 -> "Últimas 24 h"
    Periodo.DATAS -> listOfNotNull(filtro.inicio, filtro.fim).distinct().joinToString(" – ") { formatarData(it, "dd/MM") }
    else -> "Últimos ${filtro.periodo.rotulo}"
}

private fun rotuloCategorias(categorias: Set<CategoriaOcorrencia>): String = when (categorias.size) {
    CategoriaOcorrencia.entries.size -> "Todas as categorias"
    0 -> "Nenhuma categoria"
    1 -> categorias.first().rotulo
    else -> "${categorias.size} categorias"
}

/** O seletor de datas devolve meia-noite UTC; o filtro trabalha com meia-noite local. */
private fun utcParaDiaLocal(utc: Long): Long {
    val dia = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utc }
    return Calendar.getInstance().apply {
        clear()
        set(dia.get(Calendar.YEAR), dia.get(Calendar.MONTH), dia.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}
