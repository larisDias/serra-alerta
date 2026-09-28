package br.ifsp.serraalerta.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.format.DateUtils
import android.text.format.Formatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ifsp.serraalerta.Preferences
import br.ifsp.serraalerta.domain.model.CategoriaOcorrencia
import br.ifsp.serraalerta.ui.components.*
import br.ifsp.serraalerta.ui.navigation.Routes
import br.ifsp.serraalerta.ui.navigation.SerraAlertaViewModel
import br.ifsp.serraalerta.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File

private val Dicas = listOf(
    "Não queime lixo nem restos de poda" to "Leve para a coleta ou para o ecoponto do município.",
    "Não descarte bitucas em estradas" to "Margens de rodovias e pastagens concentram os focos.",
    "Mantenha aceiros limpos" to "Faixas sem vegetação ao redor de casas, cercas e plantações freiam o avanço do fogo.",
    "Não solte balões" to "Um balão pode cair aceso a quilômetros de distância."
)

private val ResumoLei = listOf(
    "Diferencia o uso autorizado do fogo, como a queima controlada, do fogo sem controle.",
    "Prevê planos de manejo com ações de prevenção, preparo, combate e recuperação das áreas atingidas.",
    "Reconhece o papel das brigadas, inclusive comunitárias e voluntárias, e a cooperação entre União, estados e municípios.",
    "Mantém a exigência de autorização do órgão ambiental para queimas controladas."
)

@Composable
fun EducationScreen(onNavigate: (String) -> Unit, onRegister: () -> Unit) {
    var resumoAberto by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        containerColor = Papel,
        topBar = { ScreenHeader("Prevenção", eyebrow = "Manejo do fogo e boas práticas") },
        bottomBar = { SerraBottomBar(Routes.EDUCACAO, onNavigate, onRegister) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Carvao)) {
                    TopoArt(BrasaViva.copy(alpha = .85f), Modifier.matchParentSize(), centro = Offset(.9f, .15f), niveis = 12)
                    Column(Modifier.padding(22.dp)) {
                        Eyebrow("Na estiagem", color = BrasaViva)
                        Text(
                            "Pasto e vegetação secos espalham o fogo em minutos.",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextoSobreCarvao,
                            modifier = Modifier.padding(top = 10.dp, end = 24.dp)
                        )
                    }
                }
            }
            item { SectionTitle("Boas práticas", Modifier.padding(top = 10.dp)) }
            item {
                SerraCard(Modifier.fillMaxWidth()) {
                    Dicas.forEachIndexed { i, (titulo, texto) ->
                        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text("0${i + 1}", style = Dado, color = Brasa, modifier = Modifier.padding(top = 1.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(titulo, style = MaterialTheme.typography.titleSmall, color = Carvao)
                                Text(texto, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                            }
                        }
                        if (i < Dicas.lastIndex) Box(Modifier.fillMaxWidth().padding(start = 48.dp).height(1.dp).background(Linha))
                    }
                }
            }
            item { SectionTitle("Controlada × irregular", Modifier.padding(top = 10.dp)) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        CategoriaOcorrencia.QUEIMA_CONTROLADA to "Uso planejado do fogo, com autorização do órgão ambiental, área delimitada, aceiros e acompanhamento.",
                        CategoriaOcorrencia.QUEIMADA_IRREGULAR to "Fogo sem autorização, como queimar lixo ou limpar pasto por conta própria. É infração ambiental.",
                        CategoriaOcorrencia.INCENDIO_FLORESTAL to "Fogo sem controle que atinge mata e vegetação nativa. Qualquer uma das anteriores pode virar um incêndio."
                    ).forEach { (categoria, texto) ->
                        SerraCard(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                CategoryGlyph(categoria, size = 36.dp)
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(categoria.rotulo, style = MaterialTheme.typography.titleSmall, color = Carvao)
                                    Text(texto, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                                }
                            }
                        }
                    }
                }
            }
            item {
                SerraCard(Modifier.fillMaxWidth().padding(top = 10.dp).animateContentSize(), container = InpeSuave) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Eyebrow("Lei Federal nº 14.944/2024", color = Inpe)
                        Text("Política Nacional de Manejo Integrado do Fogo", style = MaterialTheme.typography.titleLarge, color = Carvao)
                        Text(
                            "Organiza a prevenção, o uso autorizado do fogo e o combate aos incêndios, envolvendo poder público, produtores e comunidade.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario
                        )
                        AnimatedVisibility(resumoAberto) {
                            Column(Modifier.padding(top = 6.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                ResumoLei.forEach {
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Box(Modifier.padding(top = 7.dp).size(6.dp).background(Inpe, CircleShape))
                                        Text(it, style = MaterialTheme.typography.bodyMedium, color = Carvao)
                                    }
                                }
                            }
                        }
                        TextButton(onClick = { resumoAberto = !resumoAberto }, contentPadding = PaddingValues(0.dp)) {
                            Text(if (resumoAberto) "Ocultar resumo" else "Ler o resumo", color = Inpe, style = MaterialTheme.typography.labelLarge)
                            Icon(
                                if (resumoAberto) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                contentDescription = null,
                                tint = Inpe,
                                modifier = Modifier.padding(start = 4.dp).size(18.dp)
                            )
                        }
                    }
                }
            }
            item {
                SerraCard(Modifier.fillMaxWidth(), container = Brasa) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Viu fumaça? Registre e avise", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                        Text("Os primeiros minutos decidem o tamanho do incêndio.", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = .85f))
                        SerraButton("Registrar ocorrência", onRegister, Modifier.fillMaxWidth().padding(top = 10.dp), tom = Tom.CLARO, icon = Icons.Rounded.PhotoCamera)
                        Eyebrow("Sem cadastro, funciona offline", color = Color.White.copy(alpha = .8f), modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyScreen(onNavigate: (String) -> Unit, onRegister: () -> Unit) {
    BarrasSobreEscuro()
    val context = LocalContext.current
    val contacts = listOf(
        Triple("199", "Defesa Civil", "Riscos e desastres ambientais"),
        Triple("190", "Polícia Militar", "Emergências policiais")
    )
    Scaffold(
        containerColor = Carvao,
        bottomBar = { SerraBottomBar(Routes.EMERGENCIA, onNavigate, onRegister) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(Modifier.statusBarsPadding().padding(top = 20.dp, bottom = 8.dp)) {
                    Eyebrow("Ligação direta, com um toque", color = TextoSobreCarvaoSuave)
                    Text("Emergência", style = MaterialTheme.typography.headlineLarge, color = TextoSobreCarvao, modifier = Modifier.padding(top = 6.dp))
                }
            }
            item {
                Surface(
                    onClick = { abrirDiscador(context, "193") },
                    shape = RoundedCornerShape(28.dp),
                    color = Perigo,
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box {
                        TopoArt(Color.White.copy(alpha = .35f), Modifier.matchParentSize(), centro = Offset(.95f, .5f), niveis = 10)
                        Column(Modifier.padding(22.dp)) {
                            Eyebrow("Incêndios e resgates", color = Color.White.copy(alpha = .8f))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("193", style = MaterialTheme.typography.displayLarge, modifier = Modifier.weight(1f).padding(top = 6.dp))
                                Box(Modifier.size(56.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Rounded.Call, contentDescription = "Ligar para Corpo de Bombeiros", tint = Perigo, modifier = Modifier.size(26.dp))
                                }
                            }
                            Text("Corpo de Bombeiros", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
            items(contacts.size) { i ->
                val (number, name, description) = contacts[i]
                Surface(
                    onClick = { abrirDiscador(context, number) },
                    shape = RoundedCornerShape(24.dp),
                    color = Carvao2,
                    contentColor = TextoSobreCarvao,
                    border = BorderStroke(1.dp, Carvao3),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(horizontal = 20.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(number, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.width(84.dp))
                        Column(Modifier.weight(1f)) {
                            Text(name, style = MaterialTheme.typography.titleMedium)
                            Text(description, style = MaterialTheme.typography.bodySmall, color = TextoSobreCarvaoSuave)
                        }
                        Box(Modifier.size(44.dp).background(Carvao3, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Call, contentDescription = "Ligar para $name", tint = BrasaViva, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
            item {
                Column(Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Eyebrow("O que informar na ligação", color = TextoSobreCarvaoSuave)
                    listOf(
                        "Onde você está e um ponto de referência",
                        "O que está queimando: pasto, mata, lixo",
                        "Para onde a fumaça e o vento seguem",
                        "Se há pessoas, animais ou casas em risco"
                    ).forEachIndexed { i, texto ->
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text("0${i + 1}", style = Dado, color = BrasaViva)
                            Text(texto, style = MaterialTheme.typography.bodyMedium, color = TextoSobreCarvao)
                        }
                    }
                }
            }
            item {
                Text(
                    "Ligações gratuitas, inclusive sem crédito. Depois, registre a ocorrência no app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSobreCarvaoSuave,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    viewModel: SerraAlertaViewModel,
    preferences: Preferences,
    onAbout: () -> Unit,
    onReplayIntro: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val occurrences by viewModel.ocorrencias.collectAsStateWithLifecycle()
    val ultimaAtualizacao by viewModel.ultimaAtualizacaoFocos.collectAsStateWithLifecycle()
    var gpsAltaPrecisao by remember { mutableStateOf(preferences.gpsAltaPrecisao) }
    var atualizarFocos by remember { mutableStateOf(preferences.atualizarFocosAoAbrir) }
    var showPrivacy by remember { mutableStateOf(false) }
    var showClearConfirmation by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val diretorioFotos = remember { File(context.filesDir, "ocorrencias") }
    // Relê permissões e armazenamento ao voltar das configurações do sistema.
    var retorno by remember { mutableIntStateOf(0) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { retorno++ }
    val cameraGranted = remember(retorno) { context.hasPermission(Manifest.permission.CAMERA) }
    val locationGranted = remember(retorno) { context.hasLocationPermission() }
    val bytes = remember(retorno, occurrences) { diretorioFotos.walk().filter { it.isFile }.sumOf { it.length() } }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { retorno++ }
    val abrirSistema = {
        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
    }
    val versao = remember { runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull() ?: "" }

    if (showPrivacy) {
        AlertDialog(
            onDismissRequest = { showPrivacy = false },
            containerColor = Superficie,
            title = { Text("Política de privacidade", style = MaterialTheme.typography.headlineSmall) },
            text = { Text("O Serra Alerta não exige conta e não coleta dados pessoais identificáveis. Fotos, localização e descrições ficam armazenadas localmente no dispositivo para compor seus relatos. Focos oficiais são consultados de fonte pública do INPE/BDQueimadas quando há conexão. Ao compartilhar, o conteúdo passa a seguir as políticas do aplicativo escolhido pelo usuário.", color = TextoSecundario) },
            confirmButton = { TextButton(onClick = { showPrivacy = false }) { Text("Entendi", color = Carvao) } }
        )
    }
    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            containerColor = Superficie,
            title = { Text("Limpar histórico local?", style = MaterialTheme.typography.headlineSmall) },
            text = { Text("${occurrences.size} relatos e suas fotos serão apagados deste aparelho. Esta ação não pode ser desfeita.", color = TextoSecundario) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.limparHistorico(diretorioFotos)
                    showClearConfirmation = false
                    scope.launch { snackbarHostState.showSnackbar("Histórico apagado deste aparelho") }
                }) { Text("Apagar", color = Perigo) }
            },
            dismissButton = { TextButton(onClick = { showClearConfirmation = false }) { Text("Cancelar", color = Carvao) } }
        )
    }

    Scaffold(
        containerColor = Papel,
        topBar = { ScreenHeader("Configurações", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { SectionTitle("Permissões") }
            item {
                SerraCard(Modifier.fillMaxWidth()) {
                    PermissionItem(Icons.Rounded.PhotoCamera, "Câmera", "Para fotografar a ocorrência", cameraGranted) {
                        if (cameraGranted) abrirSistema() else permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                    }
                    Divisoria()
                    PermissionItem(Icons.Rounded.LocationOn, "Localização", "Precisa, apenas durante o registro", locationGranted) {
                        if (locationGranted) abrirSistema()
                        else permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                    }
                    Divisoria()
                    SwitchItem(Icons.Rounded.GpsFixed, "GPS de alta precisão só ao registrar", "Economiza bateria", gpsAltaPrecisao) {
                        gpsAltaPrecisao = it
                        preferences.gpsAltaPrecisao = it
                    }
                }
            }
            item { SectionTitle("Dados", Modifier.padding(top = 14.dp)) }
            item {
                SerraCard(Modifier.fillMaxWidth()) {
                    SwitchItem(
                        Icons.Rounded.SatelliteAlt,
                        "Atualizar focos do INPE ao abrir o mapa",
                        "Última atualização: " + if (ultimaAtualizacao == 0L) "nunca" else dataHoje(ultimaAtualizacao),
                        atualizarFocos
                    ) {
                        atualizarFocos = it
                        preferences.atualizarFocosAoAbrir = it
                    }
                    Divisoria()
                    SettingsRow(
                        Icons.Rounded.Storage,
                        "Histórico local",
                        "${occurrences.size} ${if (occurrences.size == 1) "relato" else "relatos"} · ${Formatter.formatShortFileSize(context, bytes)} neste aparelho"
                    )
                    Divisoria()
                    SettingsRow(
                        Icons.Rounded.DeleteOutline,
                        "Limpar histórico local",
                        "Apaga relatos e fotos deste aparelho",
                        destrutivo = true,
                        onClick = if (occurrences.isNotEmpty()) ({ showClearConfirmation = true }) else null
                    )
                }
            }
            item { SectionTitle("Privacidade e projeto", Modifier.padding(top = 14.dp)) }
            item {
                SerraCard(Modifier.fillMaxWidth()) {
                    SettingsRow(Icons.Rounded.Lock, "Política de privacidade", "Nenhum dado pessoal é coletado", onClick = { showPrivacy = true })
                    Divisoria()
                    SettingsRow(Icons.Rounded.School, "Sobre o projeto", "IFSP · São João da Boa Vista", onClick = onAbout)
                    Divisoria()
                    SettingsRow(Icons.Rounded.Replay, "Rever introdução", null, onClick = onReplayIntro)
                }
            }
            item {
                Eyebrow(
                    "Serra Alerta · versão $versao",
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp).wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun Divisoria() = Box(Modifier.fillMaxWidth().padding(start = 64.dp).height(1.dp).background(Linha))

@Composable
private fun IconeLinha(icon: ImageVector, tint: Color = Carvao) {
    Box(Modifier.size(34.dp).background(PapelFundo, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    description: String?,
    destrutivo: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = { if (onClick != null && !destrutivo) Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = LinhaForte, modifier = Modifier.size(18.dp)) }
) {
    Row(
        Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconeLinha(icon, if (destrutivo) Perigo else Carvao)
        Column(Modifier.weight(1f).padding(start = 14.dp, end = 10.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = if (destrutivo) Perigo else Carvao)
            description?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = TextoSecundario) }
        }
        trailing()
    }
}

@Composable
private fun PermissionItem(icon: ImageVector, title: String, description: String, granted: Boolean, onClick: () -> Unit) {
    SettingsRow(icon, title, description, onClick = onClick) {
        if (granted) Pill("Permitido", icon = Icons.Rounded.Check, container = SeguroSuave, content = Seguro)
        else SerraButton("Permitir", onClick, tom = Tom.CARVAO, height = 36.dp)
    }
}

@Composable
private fun SwitchItem(icon: ImageVector, title: String, description: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().toggleable(checked, role = Role.Switch, onValueChange = onChange).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconeLinha(icon)
        Column(Modifier.weight(1f).padding(start = 14.dp, end = 10.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = Carvao)
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
        }
        Switch(checked = checked, onCheckedChange = null, colors = SerraSwitchColors)
    }
}

private fun dataHoje(timestamp: Long): String =
    if (DateUtils.isToday(timestamp)) "hoje, " + formatarData(timestamp, "HH:mm") else formatarData(timestamp, "dd/MM, HH:mm")

private val Equipe = listOf(
    "André Lyra Fernandes",
    "Gabriel Maia Miguel",
    "Larissa Gabriela Sant’Angelo Dias",
    "Mariana Peixoto Chahud",
    "Victoria Carolina Ferreira da Silva"
)

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Papel,
        topBar = { ScreenHeader("Sobre o projeto", eyebrow = "Projeto de extensão · Ciência da Computação", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Carvao)) {
                    TopoArt(BrasaViva.copy(alpha = .85f), Modifier.matchParentSize(), centro = Offset(.8f, .3f), niveis = 12)
                    Column(Modifier.padding(22.dp)) {
                        Text("Serra", style = MaterialTheme.typography.displayMedium, color = TextoSobreCarvao)
                        Text("Alerta", style = MaterialTheme.typography.displayMedium, color = BrasaViva)
                        Text(
                            "Uma rede de vigilância colaborativa que alerta os focos de fogo antes que atinjam proporções incontroláveis, com a comunidade da Serra da Paulista como aliada.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSobreCarvaoSuave,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
            item {
                AboutSection("Instituição") {
                    Text("Instituto Federal de São Paulo", style = MaterialTheme.typography.titleMedium, color = Carvao)
                    Text("Campus São João da Boa Vista", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                }
            }
            item {
                AboutSection("Equipe") {
                    Equipe.forEach { nome ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.size(36.dp).background(Carvao, CircleShape), contentAlignment = Alignment.Center) {
                                Text(iniciais(nome), style = Rotulo, color = BrasaViva)
                            }
                            Text(nome, style = MaterialTheme.typography.bodyMedium, color = Carvao)
                        }
                    }
                }
            }
            item {
                AboutSection("Orientação") { Text("Prof. Elias Mendes Oliveira", style = MaterialTheme.typography.titleSmall, color = Carvao) }
            }
            item {
                AboutSection("Dados e créditos") {
                    Text("Focos de calor: INPE · BDQueimadas", style = MaterialTheme.typography.bodyMedium, color = Carvao)
                    Text("Mapa: © colaboradores do OpenStreetMap", style = MaterialTheme.typography.bodyMedium, color = Carvao)
                }
            }
        }
    }
}

@Composable
private fun AboutSection(titulo: String, content: @Composable () -> Unit) {
    SerraCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Eyebrow(titulo)
            content()
        }
    }
}

private fun iniciais(nome: String): String = nome.split(" ").let { "${it.first().first()}${it.last().first()}" }
