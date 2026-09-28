package br.ifsp.serraalerta.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import br.ifsp.serraalerta.location.Coordenada
import br.ifsp.serraalerta.ui.theme.Brasa
import br.ifsp.serraalerta.ui.theme.Carvao
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

/** Mapa com pino fixo no centro: o usuário arrasta o mapa e o centro vira a posição escolhida. */
@Composable
fun LocationPickerMap(
    inicial: Coordenada,
    onCentroChange: (Coordenada) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val latestChange by rememberUpdatedState(onCentroChange)
    Box(modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MapView(context).apply {
                    aplicarEstiloSerra(context)
                    controller.setZoom(16.0)
                    controller.setCenter(GeoPoint(inicial.latitude, inicial.longitude))
                    addMapListener(object : MapListener {
                        private fun avisar(): Boolean {
                            latestChange(Coordenada(mapCenter.latitude, mapCenter.longitude))
                            return false
                        }
                        override fun onScroll(event: ScrollEvent?) = avisar()
                        override fun onZoom(event: ZoomEvent?) = avisar()
                    })
                }
            },
            onRelease = { it.onDetach() }
        )
        CenterPin(Modifier.align(Alignment.Center))
    }
}

/** Pino de brasa com haste; a ponta da haste marca exatamente o centro do mapa. */
@Composable
fun CenterPin(modifier: Modifier = Modifier) {
    Box(modifier.semantics { contentDescription = "Posição escolhida" }) {
        Column(
            Modifier.offset(y = (-35).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.shadow(10.dp, RoundedCornerShape(16.dp)).size(48.dp).background(Brasa, RoundedCornerShape(16.dp))
                    .border(3.dp, Color.White, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Box(Modifier.size(width = 3.dp, height = 16.dp).background(Carvao))
        }
        Box(Modifier.align(Alignment.Center).size(8.dp).background(Carvao, CircleShape).border(2.dp, Color.White, CircleShape))
    }
}
