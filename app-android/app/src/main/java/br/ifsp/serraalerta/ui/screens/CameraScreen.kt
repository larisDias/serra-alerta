package br.ifsp.serraalerta.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GpsFixed
import androidx.compose.material.icons.rounded.GpsOff
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ifsp.serraalerta.location.Coordenada
import br.ifsp.serraalerta.ui.components.BarrasSobreEscuro
import br.ifsp.serraalerta.ui.components.Eyebrow
import br.ifsp.serraalerta.ui.components.IconCircleButton
import br.ifsp.serraalerta.ui.components.PermissionExplanation
import br.ifsp.serraalerta.ui.components.PhotoPreview
import br.ifsp.serraalerta.ui.theme.Brasa
import br.ifsp.serraalerta.ui.theme.BrasaViva
import br.ifsp.serraalerta.ui.theme.Carvao
import br.ifsp.serraalerta.ui.theme.Dado
import br.ifsp.serraalerta.ui.theme.Papel
import br.ifsp.serraalerta.ui.navigation.MAX_FOTOS
import br.ifsp.serraalerta.ui.navigation.SerraAlertaViewModel
import kotlinx.coroutines.launch
import java.io.File

private val PermissoesRegistro = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

@Composable
fun CameraScreen(
    viewModel: SerraAlertaViewModel,
    onPhotoCaptured: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val rascunho by viewModel.rascunho.collectAsStateWithLifecycle()
    var hasCamera by remember { mutableStateOf(context.hasPermission(Manifest.permission.CAMERA)) }
    var hasLocation by remember { mutableStateOf(context.hasLocationPermission()) }
    var capture by remember { mutableStateOf<ImageCapture?>(null) }
    var capturando by remember { mutableStateOf(false) }
    val flash = remember { Animatable(0f) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        hasCamera = context.hasPermission(Manifest.permission.CAMERA)
        hasLocation = context.hasLocationPermission()
    }

    // Pede câmera e localização juntas: o GPS trabalha enquanto o usuário enquadra a foto.
    LaunchedEffect(Unit) {
        if (!hasCamera || !hasLocation) permissionLauncher.launch(PermissoesRegistro)
    }
    LaunchedEffect(hasLocation) {
        if (hasLocation && rascunho.gps == null) viewModel.localizar()
    }

    if (!hasCamera) {
        Column(
            modifier = Modifier.fillMaxSize().background(Papel).statusBarsPadding().padding(20.dp)
        ) {
            IconCircleButton(Icons.AutoMirrored.Rounded.ArrowBack, "Voltar", onBack)
            Spacer(Modifier.weight(1f))
            Box(Modifier.size(64.dp).background(Carvao, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.PhotoCamera, contentDescription = null, tint = BrasaViva, modifier = Modifier.size(30.dp))
            }
            PermissionExplanation(
                title = "Permissão para usar a câmera",
                message = "A câmera é usada somente para fotografar o local da ocorrência. A foto fica salva no dispositivo junto ao seu relato.",
                actionLabel = "Permitir câmera",
                onAction = { permissionLauncher.launch(PermissoesRegistro) },
                modifier = Modifier.padding(top = 20.dp)
            )
            Spacer(Modifier.weight(1.4f))
        }
        return
    }

    val primeiraFoto = rascunho.fotos.isEmpty()
    val interaction = remember { MutableInteractionSource() }
    val pressionado by interaction.collectIsPressedAsState()
    val escalaObturador by animateFloatAsState(if (pressionado) .9f else 1f, label = "obturador")
    val disparar: () -> Unit = disparar@{
        val imageCapture = capture ?: return@disparar
        if (capturando) return@disparar
        capturando = true
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        scope.launch {
            flash.snapTo(.8f)
            flash.animateTo(0f, tween(250))
        }
        val directory = File(context.filesDir, "ocorrencias").apply { mkdirs() }
        val file = File(directory, "${System.currentTimeMillis()}.jpg")
        imageCapture.takePicture(
            ImageCapture.OutputFileOptions.Builder(file).build(),
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onPhotoCaptured(file.absolutePath)
                }

                override fun onError(exception: ImageCaptureException) {
                    capturando = false
                    Toast.makeText(context, "Não foi possível salvar a foto. Tente novamente.", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    BarrasSobreEscuro()
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        CameraPreview(
            lifecycleOwner = lifecycleOwner,
            onImageCaptureReady = { capture = it },
            modifier = Modifier.fillMaxSize()
        )
        Molduras(Modifier.fillMaxSize().padding(horizontal = 36.dp, vertical = 210.dp))
        Box(Modifier.fillMaxSize().alpha(flash.value).background(Color.White))

        Column(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconCircleButton(Icons.Rounded.Close, "Voltar", onBack, container = Color.Black.copy(alpha = .45f), content = Color.White)
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Eyebrow(if (primeiraFoto) "Passo 1 de 2" else "Foto ${rascunho.fotos.size + 1} de $MAX_FOTOS", color = Color.White.copy(alpha = .75f))
                    Text("Foto da ocorrência", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
                Box(Modifier.size(44.dp).background(Color.Black.copy(alpha = .45f), CircleShape), contentAlignment = Alignment.Center) {
                    Text("${rascunho.fotos.size}/$MAX_FOTOS", style = Dado, color = Color.White)
                }
            }
            LocationChip(localizando = rascunho.localizando, gps = rascunho.gps, semPermissao = !hasLocation)
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .background(Color.Black.copy(alpha = .35f))
                .navigationBarsPadding().padding(top = 18.dp, bottom = 28.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                if (capture == null) "Preparando a câmera…" else "Enquadre a fumaça ou as chamas a uma distância segura",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    rascunho.fotos.lastOrNull()?.let {
                        PhotoPreview(it, Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).border(2.dp, Color.White, RoundedCornerShape(14.dp)))
                    }
                }
                Box(
                    Modifier.size(84.dp).graphicsLayer { scaleX = escalaObturador; scaleY = escalaObturador }
                        .border(4.dp, Color.White, CircleShape).padding(8.dp)
                        .clip(CircleShape)
                        .background(if (capture == null) Color.Gray else Color.White)
                        .clickable(interactionSource = interaction, indication = null, enabled = capture != null && !capturando, role = Role.Button, onClickLabel = "Capturar foto", onClick = disparar)
                        .semantics { contentDescription = "Capturar foto" },
                    contentAlignment = Alignment.Center
                ) {
                    if (capturando || capture == null) {
                        CircularProgressIndicator(color = Brasa, strokeWidth = 2.dp, modifier = Modifier.size(26.dp))
                    }
                }
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

/** Cantos de enquadramento do visor. */
@Composable
private fun Molduras(modifier: Modifier) {
    Canvas(modifier) {
        val l = 28.dp.toPx()
        val w = 3.dp.toPx()
        val cor = Color.White.copy(alpha = .85f)
        listOf(
            Offset(0f, 0f) to Offset(1f, 1f),
            Offset(size.width, 0f) to Offset(-1f, 1f),
            Offset(0f, size.height) to Offset(1f, -1f),
            Offset(size.width, size.height) to Offset(-1f, -1f)
        ).forEach { (p, d) ->
            drawLine(cor, p, Offset(p.x + l * d.x, p.y), w, StrokeCap.Round)
            drawLine(cor, p, Offset(p.x, p.y + l * d.y), w, StrokeCap.Round)
        }
    }
}

@Composable
private fun LocationChip(localizando: Boolean, gps: Coordenada?, semPermissao: Boolean, modifier: Modifier = Modifier) {
    val (icone, texto) = when {
        gps != null -> Icons.Rounded.GpsFixed to "Localização obtida" + (gps.precisaoMetros?.let { " · ±${it.toInt()} m" } ?: "")
        localizando -> Icons.Rounded.GpsFixed to "Obtendo localização…"
        semPermissao -> Icons.Rounded.GpsOff to "Sem localização · ajuste no próximo passo"
        else -> Icons.Rounded.GpsOff to "Localização indisponível · ajuste no próximo passo"
    }
    Surface(shape = CircleShape, color = Color.Black.copy(alpha = .55f), contentColor = Color.White, modifier = modifier) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 7.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            when {
                localizando && gps == null -> CircularProgressIndicator(color = Color.White, strokeWidth = 1.5.dp, modifier = Modifier.size(12.dp))
                gps != null -> Box(Modifier.size(8.dp).background(Color(0xFF6BD48A), CircleShape))
                else -> Icon(icone, contentDescription = null, tint = BrasaViva, modifier = Modifier.size(15.dp))
            }
            Text(texto, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun CameraPreview(
    lifecycleOwner: LifecycleOwner,
    onImageCaptureReady: (ImageCapture) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            PreviewView(viewContext).also { previewView ->
                startCamera(context, lifecycleOwner, previewView, onImageCaptureReady)
            }
        }
    )
}

@SuppressLint("MissingPermission")
private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val providerFuture = ProcessCameraProvider.getInstance(context)
    providerFuture.addListener({
        val cameraProvider = providerFuture.get()
        val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
        runCatching {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
            onImageCaptureReady(imageCapture)
        }
    }, ContextCompat.getMainExecutor(context))
}

internal fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

internal fun Context.hasLocationPermission(): Boolean =
    hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
