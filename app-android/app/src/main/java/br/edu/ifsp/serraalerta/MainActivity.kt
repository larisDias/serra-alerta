package br.edu.ifsp.serraalerta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.edu.ifsp.serraalerta.ui.SerraAlertaApp
import br.edu.ifsp.serraalerta.ui.theme.SerraAlertaTheme
import org.osmdroid.config.Configuration
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // O servidor de tiles do OpenStreetMap exige um user-agent identificável.
        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidBasePath = File(cacheDir, "osmdroid")
            osmdroidTileCache = File(cacheDir, "osmdroid/tiles")
        }

        enableEdgeToEdge()
        setContent {
            SerraAlertaTheme { SerraAlertaApp() }
        }
    }
}
