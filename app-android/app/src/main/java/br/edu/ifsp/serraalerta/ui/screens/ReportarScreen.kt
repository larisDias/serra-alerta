package br.edu.ifsp.serraalerta.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifsp.serraalerta.data.Regiao
import br.edu.ifsp.serraalerta.data.Relato
import br.edu.ifsp.serraalerta.data.Sinal
import br.edu.ifsp.serraalerta.data.TipoFoco
import br.edu.ifsp.serraalerta.ui.theme.Areia
import br.edu.ifsp.serraalerta.ui.theme.Borda
import br.edu.ifsp.serraalerta.ui.theme.Carvao
import br.edu.ifsp.serraalerta.ui.theme.Laranja
import br.edu.ifsp.serraalerta.ui.theme.Texto2
import br.edu.ifsp.serraalerta.util.Localizacao
import br.edu.ifsp.serraalerta.util.coordenadas
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import java.io.File

@Composable
fun ReportarScreen(
    localManual: GeoPoint?,
    centroDoMapa: GeoPoint,
    onFechar: () -> Unit,
    onEnviar: (Relato) -> Unit,
) {
    val ctx = LocalContext.current
    val escopo = rememberCoroutineScope()

    var sinal by remember { mutableStateOf(Sinal.FUMACA) }
    var tipo by remember { mutableStateOf<TipoFoco?>(null) }
    var descricao by remember { mutableStateOf("") }
    var foto by remember { mutableStateOf<File?>(null) }
    var local by remember { mutableStateOf(localManual) }
    var origemLocal by remember { mutableStateOf(if (localManual != null) "Marcado no mapa" else "") }
    var buscandoGps by remember { mutableStateOf(false) }

    fun buscarGps() {
        buscandoGps = true
        escopo.launch {
            val p = Localizacao.atual(ctx)
            when {
                p != null && Regiao.dentroDaCobertura(p) -> { local = p; origemLocal = "GPS do aparelho" }
                p != null -> { local = centroDoMapa; origemLocal = "Fora da área do protótipo — usando o centro do mapa" }
                else -> { local = centroDoMapa; origemLocal = "GPS indisponível — usando o centro do mapa" }
            }
            buscandoGps = false
        }
    }

    val pedirPermissao = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        buscarGps()
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        if (bmp != null) escopo.launch { foto = salvarBitmap(ctx, bmp) }
    }
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) escopo.launch { foto = copiarUri(ctx, uri) }
    }

    LaunchedEffect(Unit) {
        if (localManual == null) {
            if (Localizacao.temPermissao(ctx)) buscarGps() else pedirPermissao.launch(Localizacao.PERMISSOES)
        }
    }
    BackHandler(onBack = onFechar)

    Column(Modifier.fillMaxSize().background(Areia).statusBarsPadding().imePadding()) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFechar) { Icon(Icons.Outlined.Close, "Fechar") }
            Text("Reportar foco", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(
                "Sem cadastro",
                fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2F6B4F),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2F0E8))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            Secao("O que você está vendo?") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CartaoSinal(Icons.Outlined.Cloud, "Fumaça", sinal == Sinal.FUMACA, Modifier.weight(1f)) { sinal = Sinal.FUMACA }
                    CartaoSinal(Icons.Outlined.LocalFireDepartment, "Fogo", sinal == Sinal.FOGO, Modifier.weight(1f)) { sinal = Sinal.FOGO }
                }
            }

            Secao("Parece ser…") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TipoFoco.entries.forEach { t ->
                        OpcaoTipo(t, tipo == t) { tipo = t }
                    }
                }
            }

            Secao("Foto (opcional)") {
                val f = foto
                if (f != null) {
                    Box(Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(18.dp))) {
                        AsyncImage(model = f, contentDescription = "Foto do foco", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        IconButton(
                            onClick = { foto = null },
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).clip(CircleShape).background(Color.Black.copy(alpha = .5f)),
                        ) { Icon(Icons.Outlined.Close, "Remover foto", tint = Color.White) }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BotaoFoto(Icons.Outlined.CameraAlt, "Câmera", Modifier.weight(1f)) { camera.launch(null) }
                        BotaoFoto(Icons.Outlined.PhotoLibrary, "Galeria", Modifier.weight(1f)) {
                            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    }
                }
            }

            Secao("Descrição") {
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { if (it.length <= 280) descricao = it },
                    placeholder = { Text("Ex.: fumaça escura atrás da fazenda, vento forte para o norte…") },
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    supportingText = { Text("${descricao.length}/280") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Secao("Localização") {
                Surface(shape = RoundedCornerShape(18.dp), color = Color.White, border = BorderStroke(1.dp, Borda)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(42.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.Outlined.Place, null, tint = Laranja) }
                        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            val p = local
                            if (buscandoGps || p == null) {
                                Text("Obtendo sua localização…", fontWeight = FontWeight.SemiBold)
                            } else {
                                Text("Próximo a ${Regiao.referenciaMaisProxima(p)}", fontWeight = FontWeight.SemiBold)
                                Text(coordenadas(p.latitude, p.longitude), fontSize = 13.sp, color = Texto2)
                                Text(origemLocal, fontSize = 12.sp, color = Texto2)
                            }
                        }
                        if (buscandoGps) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                        else IconButton(onClick = {
                            if (Localizacao.temPermissao(ctx)) buscarGps() else pedirPermissao.launch(Localizacao.PERMISSOES)
                        }) { Icon(Icons.Outlined.Refresh, "Atualizar GPS") }
                    }
                }
            }

            Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFFFEEE8)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Risco a pessoas ou casas? Ligue já para os Bombeiros.",
                        fontSize = 14.sp, color = Carvao, modifier = Modifier.weight(1f),
                    )
                    OutlinedButton(
                        onClick = { ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:193"))) },
                        border = BorderStroke(1.dp, Laranja),
                    ) {
                        Icon(Icons.Outlined.Phone, null, tint = Laranja, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("193", color = Laranja, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }

        Surface(color = Color.White, shadowElevation = 12.dp) {
            Button(
                onClick = {
                    val p = local ?: return@Button
                    val t = tipo ?: return@Button
                    onEnviar(
                        Relato(
                            tipo = t, sinal = sinal, latitude = p.latitude, longitude = p.longitude,
                            descricao = descricao.trim().ifEmpty { "${sinal.rotulo} avistada (${t.rotulo.lowercase()})." },
                            fotoPath = foto?.absolutePath,
                        )
                    )
                },
                enabled = tipo != null && local != null && !buscandoGps,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp).height(58.dp),
            ) {
                Text(if (tipo == null) "Escolha o tipo para enviar" else "Enviar relato", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun Secao(titulo: String, conteudo: @Composable () -> Unit) {
    Column {
        Text(titulo, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Texto2, modifier = Modifier.padding(bottom = 10.dp))
        conteudo()
    }
}

@Composable
private fun CartaoSinal(icone: ImageVector, texto: String, ativo: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (ativo) Carvao else Color.White)
            .border(1.dp, if (ativo) Carvao else Borda, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icone, null, tint = if (ativo) Color.White else Carvao, modifier = Modifier.size(30.dp))
        Spacer(Modifier.height(6.dp))
        Text(texto, color = if (ativo) Color.White else Carvao, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun OpcaoTipo(tipo: TipoFoco, ativo: Boolean, onClick: () -> Unit) {
    val cor = Color(tipo.cor)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (ativo) cor.copy(alpha = .1f) else Color.White)
            .border(if (ativo) 2.dp else 1.dp, if (ativo) cor else Borda, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(12.dp).clip(CircleShape).background(cor))
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(if (tipo == TipoFoco.INCENDIO) "Incêndio florestal" else "Queimada ${tipo.rotulo.lowercase()}", fontWeight = FontWeight.SemiBold)
            Text(tipo.descricao, fontSize = 13.sp, color = Texto2)
        }
        RadioButton(selected = ativo, onClick = onClick)
    }
}

@Composable
private fun BotaoFoto(icone: ImageVector, texto: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .height(110.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, Borda, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icone, null, tint = Laranja, modifier = Modifier.size(30.dp))
        Spacer(Modifier.height(6.dp))
        Text(texto, fontWeight = FontWeight.Medium)
    }
}

private suspend fun salvarBitmap(ctx: Context, bmp: Bitmap): File = withContext(Dispatchers.IO) {
    val arq = novoArquivoDeFoto(ctx)
    arq.outputStream().use { bmp.compress(Bitmap.CompressFormat.JPEG, 90, it) }
    arq
}

private suspend fun copiarUri(ctx: Context, uri: Uri): File? = withContext(Dispatchers.IO) {
    val arq = novoArquivoDeFoto(ctx)
    ctx.contentResolver.openInputStream(uri)?.use { entrada ->
        arq.outputStream().use { entrada.copyTo(it) }
        arq
    }
}

private fun novoArquivoDeFoto(ctx: Context): File =
    File(ctx.filesDir, "fotos").apply { mkdirs() }.let { File(it, "relato_${System.currentTimeMillis()}.jpg") }
