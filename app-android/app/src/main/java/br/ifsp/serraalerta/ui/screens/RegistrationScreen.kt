package br.ifsp.serraalerta.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ifsp.serraalerta.domain.model.CategoriaOcorrencia
import br.ifsp.serraalerta.location.CentroSerraPaulista
import br.ifsp.serraalerta.location.Coordenada
import br.ifsp.serraalerta.ui.components.*
import br.ifsp.serraalerta.ui.navigation.MAX_FOTOS
import br.ifsp.serraalerta.ui.navigation.Rascunho
import br.ifsp.serraalerta.ui.navigation.SerraAlertaViewModel
import br.ifsp.serraalerta.ui.theme.*

private const val MAX_DESCRICAO = 280

@Composable
fun RegistrationScreen(
    viewModel: SerraAlertaViewModel,
    onOpenCamera: () -> Unit,
    onAdjustLocation: () -> Unit,
    onSaved: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val rascunho by viewModel.rascunho.collectAsStateWithLifecycle()
    var categoria by rememberSaveable { mutableStateOf<CategoriaOcorrencia?>(null) }
    var descricao by rememberSaveable { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        hasLocationPermission = context.hasLocationPermission()
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && rascunho.gps == null) viewModel.localizar()
    }

    val faltando = buildList {
        if (rascunho.fotos.isEmpty()) add("foto")
        if (rascunho.posicao == null) add("localização")
        if (categoria == null) add("o que você está vendo")
    }

    Scaffold(
        containerColor = Papel,
        topBar = { ScreenHeader("Nova ocorrência", eyebrow = "Passo 2 de 2 · sem login", onBack = onBack) },
        bottomBar = {
            Surface(color = Papel, shadowElevation = 16.dp) {
                Column(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().imePadding().padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SerraButton(
                        text = if (saving) "Salvando…" else "Registrar ocorrência",
                        onClick = {
                            val selecionada = categoria ?: return@SerraButton
                            saving = true
                            viewModel.registrar(selecionada, descricao, onSaved)
                        },
                        enabled = faltando.isEmpty(),
                        loading = saving,
                        icon = Icons.Rounded.LocalFireDepartment,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            if (faltando.isEmpty()) Icons.Rounded.CloudOff else Icons.Rounded.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = TextoSecundario,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            if (faltando.isEmpty()) "Salvo no aparelho, mesmo sem internet" else "Falta: ${faltando.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            PhotosSection(
                fotos = rascunho.fotos,
                onRemove = viewModel::removerFoto,
                onRetake = { rascunho.fotos.lastOrNull()?.let(viewModel::removerFoto); onOpenCamera() },
                onAdd = onOpenCamera
            )
            Column(Modifier.padding(horizontal = 20.dp).selectableGroup()) {
                SectionTitle("O que você está vendo?", Modifier.padding(bottom = 10.dp))
                CategoriaOcorrencia.entries.chunked(2).forEach { linha ->
                    Row(Modifier.padding(bottom = 10.dp).height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        linha.forEach { opcao ->
                            CategoryOption(opcao, selected = categoria == opcao, Modifier.weight(1f).fillMaxHeight()) { categoria = opcao }
                        }
                    }
                }
            }
            LocationSection(
                rascunho = rascunho,
                hasPermission = hasLocationPermission,
                onRequestPermission = {
                    permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                },
                onRetry = viewModel::localizar,
                onAdjust = onAdjustLocation
            )
            Column(Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SectionTitle("Descrição · opcional", Modifier.weight(1f))
                    Text(
                        "${descricao.length}/$MAX_DESCRICAO",
                        style = Rotulo,
                        color = if (descricao.length >= MAX_DESCRICAO - 20) Brasa else TextoSecundario
                    )
                }
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it.take(MAX_DESCRICAO) },
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(18.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    placeholder = { Text("Ex.: fumaça escura perto da estrada, vento para a mata", color = TextoSecundario.copy(alpha = .8f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Superficie,
                        unfocusedContainerColor = Superficie,
                        focusedBorderColor = Carvao,
                        unfocusedBorderColor = Linha,
                        cursorColor = Brasa
                    )
                )
            }
        }
    }
}

@Composable
private fun PhotosSection(fotos: List<String>, onRemove: (String) -> Unit, onRetake: () -> Unit, onAdd: () -> Unit) {
    Column {
        Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            SectionTitle("Fotos · obrigatória", Modifier.weight(1f))
            if (fotos.isNotEmpty()) {
                TextButton(onClick = onRetake, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Carvao, modifier = Modifier.size(16.dp))
                    Text("Refazer", color = Carvao, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(start = 4.dp))
                }
            }
            Text("${fotos.size}/$MAX_FOTOS", style = Rotulo, color = TextoSecundario)
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 10.dp)
        ) {
            items(fotos, key = { it }) { foto ->
                Box(Modifier.animateItem()) {
                    PhotoPreview(foto, modifier = Modifier.size(width = 136.dp, height = 172.dp).clip(RoundedCornerShape(20.dp)))
                    IconCircleButton(
                        Icons.Rounded.Close, "Remover foto", { onRemove(foto) },
                        container = Carvao.copy(alpha = .7f), content = Color.White, size = 32.dp,
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                    )
                }
            }
            if (fotos.size < MAX_FOTOS) {
                item(key = "adicionar") {
                    Surface(
                        onClick = onAdd,
                        shape = RoundedCornerShape(20.dp),
                        color = Superficie,
                        border = BorderStroke(1.5.dp, LinhaForte),
                        modifier = Modifier.animateItem().size(width = if (fotos.isEmpty()) 200.dp else 136.dp, height = 172.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Box(Modifier.size(48.dp).background(Carvao, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.AddAPhoto, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                            Text(
                                if (fotos.isEmpty()) "Tirar foto" else "Adicionar foto",
                                style = MaterialTheme.typography.labelMedium,
                                color = Carvao,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationSection(
    rascunho: Rascunho,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onRetry: () -> Unit,
    onAdjust: () -> Unit
) {
    val posicao = rascunho.posicao
    val status = when {
        rascunho.posicaoAjustada != null -> "Ajustada manualmente" +
            (rascunho.gps?.let { " · ${it.distanciaAte(rascunho.posicaoAjustada).toInt()} m do GPS" } ?: "")
        rascunho.gps != null -> "Obtida automaticamente" + (rascunho.gps.precisaoMetros?.let { " · ±${it.toInt()} m" } ?: "")
        rascunho.localizando -> "Obtendo localização…"
        !hasPermission -> "Sem permissão de localização"
        else -> "Não foi possível obter a localização"
    }
    Column(Modifier.padding(horizontal = 20.dp)) {
        SectionTitle("Localização", Modifier.padding(bottom = 10.dp))
        SerraCard(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    Modifier.size(44.dp).background(if (posicao != null) Carvao else BrasaSuave, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (rascunho.localizando && posicao == null) {
                        CircularProgressIndicator(color = Brasa, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    } else {
                        Icon(
                            if (posicao != null) Icons.Rounded.LocationOn else Icons.Rounded.LocationOff,
                            contentDescription = null,
                            tint = if (posicao != null) BrasaViva else Brasa,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Column(Modifier.weight(1f)) {
                    Text(status, style = MaterialTheme.typography.titleSmall, color = Carvao)
                    posicao?.let { Text(formatarCoordenada(it.latitude, it.longitude), style = Dado, color = TextoSecundario) }
                }
            }
            Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when {
                    posicao == null && !hasPermission ->
                        SerraButton("Permitir", onRequestPermission, Modifier.weight(1f), tom = Tom.CARVAO, height = 44.dp)
                    posicao == null && !rascunho.localizando ->
                        SerraButton("Tentar de novo", onRetry, Modifier.weight(1f), tom = Tom.CARVAO, height = 44.dp)
                }
                SerraButton("Ajustar no mapa", onAdjust, Modifier.weight(1f), tom = Tom.CONTORNO, icon = Icons.Rounded.Map, height = 44.dp)
            }
        }
    }
}

@Composable
private fun CategoryOption(categoria: CategoriaOcorrencia, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val fundo by animateColorAsState(if (selected) Carvao else Superficie, label = "fundo_categoria")
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = fundo,
        border = if (selected) null else BorderStroke(1.dp, Linha),
        modifier = modifier.selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                CategoryGlyph(categoria, size = 36.dp)
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier.size(22.dp).background(if (selected) BrasaViva else Color.Transparent, CircleShape)
                        .then(if (selected) Modifier else Modifier.background(PapelFundo, CircleShape)),
                    contentAlignment = Alignment.Center
                ) {
                    if (selected) Icon(Icons.Rounded.Check, contentDescription = null, tint = Carvao, modifier = Modifier.size(14.dp))
                }
            }
            Text(
                categoria.rotulo,
                style = MaterialTheme.typography.titleSmall,
                color = if (selected) TextoSobreCarvao else Carvao,
                modifier = Modifier.padding(top = 14.dp)
            )
            Text(
                categoria.resumo,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) TextoSobreCarvaoSuave else TextoSecundario,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun AdjustLocationScreen(
    viewModel: SerraAlertaViewModel,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val rascunho by viewModel.rascunho.collectAsStateWithLifecycle()
    val inicial = remember { rascunho.posicao ?: CentroSerraPaulista }
    var centro by remember { mutableStateOf(inicial) }
    val distanciaGps = rascunho.gps?.distanciaAte(centro)

    Box(Modifier.fillMaxSize().background(Papel)) {
        LocationPickerMap(inicial = inicial, onCentroChange = { centro = it }, modifier = Modifier.fillMaxSize())
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconCircleButton(Icons.AutoMirrored.Rounded.ArrowBack, "Voltar", onBack, modifier = Modifier.size(48.dp))
            Surface(shape = RoundedCornerShape(18.dp), color = Superficie, border = BorderStroke(1.dp, Linha), shadowElevation = 4.dp, modifier = Modifier.weight(1f)) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text("Ajustar localização", style = MaterialTheme.typography.titleSmall, color = Carvao)
                    Text("Arraste o mapa para posicionar o pino", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                }
            }
        }
        Surface(
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Papel,
            shadowElevation = 20.dp,
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Eyebrow(
                    when {
                        distanciaGps == null -> "Sem GPS · posição escolhida no mapa"
                        distanciaGps < 5f -> "Na posição do GPS"
                        else -> "Ajustada · ${distanciaGps.toInt()} m do GPS"
                    },
                    color = if (distanciaGps != null && distanciaGps >= 5f) Brasa else TextoSecundario
                )
                Text(formatarCoordenada(centro.latitude, centro.longitude), style = Dado.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize), color = Carvao)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = Brasa, modifier = Modifier.size(18.dp))
                    Text(
                        "Ajuste quando o foco estiver longe de você: coloque o pino onde está a fumaça, não onde você está.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario
                    )
                }
                SerraButton(
                    "Confirmar posição",
                    onClick = {
                        viewModel.ajustarPosicao(Coordenada(centro.latitude, centro.longitude))
                        onConfirm()
                    },
                    icon = Icons.Rounded.Check,
                    tom = Tom.CARVAO,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
            }
        }
    }
}
