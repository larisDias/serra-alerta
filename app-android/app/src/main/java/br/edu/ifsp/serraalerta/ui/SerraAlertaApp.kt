package br.edu.ifsp.serraalerta.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifsp.serraalerta.RelatosViewModel
import br.edu.ifsp.serraalerta.data.Relato
import br.edu.ifsp.serraalerta.data.TipoFoco
import br.edu.ifsp.serraalerta.ui.screens.AreasScreen
import br.edu.ifsp.serraalerta.ui.screens.BarraInferior
import br.edu.ifsp.serraalerta.ui.screens.DetalheRelatoSheet
import br.edu.ifsp.serraalerta.ui.screens.MapaScreen
import br.edu.ifsp.serraalerta.ui.screens.ReportarScreen
import br.edu.ifsp.serraalerta.ui.theme.Areia
import br.edu.ifsp.serraalerta.util.Localizacao
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

enum class Aba { MAPA, AREAS }

@Composable
fun SerraAlertaApp(vm: RelatosViewModel = viewModel()) {
    val ctx = LocalContext.current
    val escopo = rememberCoroutineScope()
    val relatos by vm.relatos.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    val mapa = remember { criarMapa(ctx) }
    CicloDeVidaDoMapa(mapa)

    var aba by rememberSaveable { mutableStateOf(Aba.MAPA) }
    var filtro by rememberSaveable { mutableStateOf<TipoFoco?>(null) }
    var selecionado by remember { mutableStateOf<Relato?>(null) }
    var marcandoNoMapa by remember { mutableStateOf(false) }
    var reportando by remember { mutableStateOf(false) }
    var localManual by remember { mutableStateOf<GeoPoint?>(null) }

    val pedirPermissao = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { r ->
        if (r.values.any { it }) escopo.launch {
            Localizacao.atual(ctx)?.let { mapa.mostrarMinhaPosicao(it) }
                ?: snackbar.showSnackbar("Não foi possível obter o GPS agora.")
        }
    }

    fun localizar() {
        if (!Localizacao.temPermissao(ctx)) {
            pedirPermissao.launch(Localizacao.PERMISSOES)
            return
        }
        escopo.launch {
            Localizacao.atual(ctx)?.let { mapa.mostrarMinhaPosicao(it) }
                ?: snackbar.showSnackbar("Não foi possível obter o GPS agora.")
        }
    }

    BackHandler(enabled = aba == Aba.AREAS || marcandoNoMapa) {
        aba = Aba.MAPA; marcandoNoMapa = false
    }

    Box(Modifier.fillMaxSize().background(Areia)) {
        MapaScreen(
            mapa = mapa,
            relatos = relatos,
            filtro = filtro,
            onFiltro = { filtro = it },
            onSelecionar = { selecionado = it },
            onLocalizar = ::localizar,
            marcandoNoMapa = marcandoNoMapa,
            onMarcarNoMapa = { marcandoNoMapa = it },
            onConfirmarLocal = { p ->
                marcandoNoMapa = false
                localManual = p
                reportando = true
            },
        )

        AnimatedVisibility(aba == Aba.AREAS, enter = fadeIn(), exit = fadeOut()) {
            AreasScreen(
                relatos = relatos,
                onVerNoMapa = { p ->
                    aba = Aba.MAPA
                    mapa.controller.animateTo(p, 14.5, 700L)
                },
            )
        }

        if (!marcandoNoMapa) {
            BarraInferior(
                aba = aba,
                onAba = { aba = it },
                onReportar = { localManual = null; reportando = true },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).padding(bottom = 120.dp))

        AnimatedVisibility(
            reportando,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
        ) {
            ReportarScreen(
                localManual = localManual,
                centroDoMapa = GeoPoint(mapa.mapCenter.latitude, mapa.mapCenter.longitude),
                onFechar = { reportando = false },
                onEnviar = { novo ->
                    vm.salvar(novo)
                    reportando = false
                    aba = Aba.MAPA
                    filtro = null
                    mapa.controller.animateTo(GeoPoint(novo.latitude, novo.longitude), 15.0, 800L)
                    escopo.launch { snackbar.showSnackbar("Relato enviado! Ele já aparece no mapa.") }
                },
            )
        }

        selecionado?.let { r ->
            DetalheRelatoSheet(relato = r, onFechar = { selecionado = null })
        }
    }
}

@Composable
private fun CicloDeVidaDoMapa(mapa: org.osmdroid.views.MapView) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val obs = LifecycleEventObserver { _, e ->
            when (e) {
                Lifecycle.Event.ON_RESUME -> mapa.onResume()
                Lifecycle.Event.ON_PAUSE -> mapa.onPause()
                else -> Unit
            }
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }
}
