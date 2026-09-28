package br.ifsp.serraalerta.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.ifsp.serraalerta.Preferences
import br.ifsp.serraalerta.ui.screens.AboutScreen
import br.ifsp.serraalerta.ui.screens.AdjustLocationScreen
import br.ifsp.serraalerta.ui.screens.CameraScreen
import br.ifsp.serraalerta.ui.screens.ConfirmationScreen
import br.ifsp.serraalerta.ui.screens.DetailScreen
import br.ifsp.serraalerta.ui.screens.EducationScreen
import br.ifsp.serraalerta.ui.screens.EmergencyScreen
import br.ifsp.serraalerta.ui.screens.MapScreen
import br.ifsp.serraalerta.ui.screens.MyReportsScreen
import br.ifsp.serraalerta.ui.screens.OnboardingScreen
import br.ifsp.serraalerta.ui.screens.OfficialFocusScreen
import br.ifsp.serraalerta.ui.screens.RegistrationScreen
import br.ifsp.serraalerta.ui.screens.SettingsScreen
import br.ifsp.serraalerta.ui.screens.SplashScreen

@Composable
fun SerraAlertaApp(
    viewModel: SerraAlertaViewModel,
    preferences: Preferences
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val voltar: () -> Unit = { navController.popBackStack() }
    // Abas da barra inferior: uma instância de cada, voltar sempre leva ao mapa.
    val navegarAba: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(Routes.MAPA) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    // Registro começa pela câmera (passo 1) e segue para o formulário (passo 2).
    val iniciarRegistro: () -> Unit = {
        viewModel.iniciarRascunho()
        navController.navigate(Routes.CAMERA)
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = { fadeIn(animationSpec = tween(220)) },
        exitTransition = { fadeOut(animationSpec = tween(160)) },
        popEnterTransition = { fadeIn(animationSpec = tween(220)) },
        popExitTransition = { fadeOut(animationSpec = tween(160)) }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen {
                val destination = if (preferences.onboardingConcluido()) Routes.MAPA else Routes.ONBOARDING
                navController.navigate(destination) { popUpTo(Routes.SPLASH) { inclusive = true } }
            }
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen {
                preferences.marcarOnboardingConcluido()
                navController.navigate(Routes.MAPA) { popUpTo(navController.graph.id) { inclusive = true } }
            }
        }
        composable(Routes.MAPA) {
            MapScreen(
                viewModel = viewModel,
                onNavigate = navegarAba,
                onSettings = { navController.navigate(Routes.CONFIGURACOES) },
                onOccurrenceClick = { id -> navController.navigate(Routes.detalhes(id)) },
                onFocoClick = { focus ->
                    viewModel.selecionarFoco(focus)
                    navController.navigate(Routes.FOCO_OFICIAL)
                },
                onRegister = iniciarRegistro
            )
        }
        composable(Routes.CAMERA) {
            CameraScreen(
                viewModel = viewModel,
                onPhotoCaptured = { path ->
                    viewModel.adicionarFoto(path)
                    if (navController.previousBackStackEntry?.destination?.route == Routes.REGISTRO) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Routes.REGISTRO) { popUpTo(Routes.CAMERA) { inclusive = true } }
                    }
                },
                onBack = voltar
            )
        }
        composable(Routes.REGISTRO) {
            RegistrationScreen(
                viewModel = viewModel,
                onOpenCamera = { navController.navigate(Routes.CAMERA) },
                onAdjustLocation = { navController.navigate(Routes.AJUSTAR_LOCALIZACAO) },
                onSaved = { id ->
                    navController.navigate(Routes.confirmacao(id)) {
                        popUpTo(Routes.REGISTRO) { inclusive = true }
                    }
                },
                onBack = voltar
            )
        }
        composable(Routes.AJUSTAR_LOCALIZACAO) {
            AdjustLocationScreen(viewModel = viewModel, onConfirm = voltar, onBack = voltar)
        }
        composable(
            route = Routes.CONFIRMACAO,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id") ?: return@composable
            val occurrence by viewModel.observarPorId(id).collectAsStateWithLifecycle(initialValue = null)
            ConfirmationScreen(
                occurrence = occurrence,
                context = context,
                onShare = { viewModel.marcarCompartilhada(id) },
                onMap = { navController.popBackStack(Routes.MAPA, inclusive = false) }
            )
        }
        composable(Routes.FOCO_OFICIAL) {
            val focus by viewModel.focoSelecionado.collectAsStateWithLifecycle()
            OfficialFocusScreen(focus = focus, onRegister = iniciarRegistro, onBack = voltar)
        }
        composable(
            route = Routes.DETALHES,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id") ?: return@composable
            DetailScreen(
                viewModel = viewModel,
                id = id,
                context = context,
                onShare = { viewModel.marcarCompartilhada(id) },
                onBack = voltar
            )
        }
        composable(Routes.MEUS_RELATOS) {
            MyReportsScreen(viewModel, onOccurrenceClick = { id -> navController.navigate(Routes.detalhes(id)) }, onNavigate = navegarAba, onRegister = iniciarRegistro)
        }
        composable(Routes.EDUCACAO) { EducationScreen(onNavigate = navegarAba, onRegister = iniciarRegistro) }
        composable(Routes.EMERGENCIA) { EmergencyScreen(onNavigate = navegarAba, onRegister = iniciarRegistro) }
        composable(Routes.CONFIGURACOES) {
            SettingsScreen(
                viewModel = viewModel,
                preferences = preferences,
                onAbout = { navController.navigate(Routes.SOBRE) },
                onReplayIntro = { navController.navigate(Routes.ONBOARDING) },
                onBack = voltar
            )
        }
        composable(Routes.SOBRE) { AboutScreen(onBack = voltar) }
    }
}
