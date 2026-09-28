package br.ifsp.serraalerta

import android.os.Bundle
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import br.ifsp.serraalerta.location.LocationRepository
import br.ifsp.serraalerta.ui.navigation.SerraAlertaApp
import br.ifsp.serraalerta.ui.navigation.SerraAlertaViewModel
import br.ifsp.serraalerta.ui.navigation.SerraAlertaViewModelFactory
import br.ifsp.serraalerta.ui.theme.SerraAlertaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // O app é claro por desenho; telas escuras trocam os ícones com BarrasSobreEscuro().
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        val container = AppContainer(applicationContext)
        val preferences = Preferences(applicationContext)
        setContent {
            SerraAlertaTheme {
                val appViewModel: SerraAlertaViewModel = viewModel(
                    factory = SerraAlertaViewModelFactory(
                        ocorrenciaRepository = container.ocorrenciaRepository,
                        focoOficialRepository = container.focoOficialRepository,
                        locationRepository = LocationRepository(applicationContext),
                        preferences = preferences
                    )
                )
                SerraAlertaApp(
                    viewModel = appViewModel,
                    preferences = preferences
                )
            }
        }
    }
}
