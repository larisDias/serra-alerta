package br.ifsp.serraalerta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.SatelliteAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.ifsp.serraalerta.domain.model.FocoOficial
import br.ifsp.serraalerta.ui.components.*
import br.ifsp.serraalerta.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OfficialFocusScreen(
    focus: FocoOficial?,
    onRegister: () -> Unit,
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(Papel)) {
        ScreenHeader("Foco oficial", eyebrow = "INPE · BDQueimadas", onBack = onBack)
        if (focus == null) {
            Text("Foco oficial não encontrado.", color = TextoSecundario, modifier = Modifier.padding(20.dp))
            return
        }
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Inpe)) {
                TopoArt(InpeVivo.copy(alpha = .55f), Modifier.matchParentSize(), centro = Offset(.85f, .2f), niveis = 12)
                Column(Modifier.padding(22.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.SatelliteAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Eyebrow("Dado oficial · satélite", color = Color.White.copy(alpha = .85f))
                    }
                    Text("Foco de calor detectado", style = MaterialTheme.typography.headlineMedium, color = Color.White, modifier = Modifier.padding(top = 14.dp))
                    Pill(
                        "Intensidade ${focus.nivel.rotulo.lowercase()}",
                        icon = Icons.Rounded.LocalFireDepartment,
                        container = focus.nivel.cor,
                        content = Color.White,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Eyebrow("Potência radiativa do fogo (FRP)", color = Color.White.copy(alpha = .75f), modifier = Modifier.padding(top = 28.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            focus.frp?.let { "%.1f".format(Locale.forLanguageTag("pt-BR"), it) } ?: "—",
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White
                        )
                        if (focus.frp != null) Text("MW", style = MaterialTheme.typography.titleLarge, color = Color.White.copy(alpha = .8f), modifier = Modifier.padding(start = 6.dp, bottom = 8.dp))
                    }
                    if (focus.frp == null) Text("Não informada", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = .8f))
                }
            }
            SerraCard(Modifier.fillMaxWidth()) {
                LinhaDado("Detecção", formatOfficialDate(focus.dataDeteccao))
                Divisor()
                LinhaDado("Coordenadas", formatarCoordenada(focus.latitude, focus.longitude))
                Divisor()
                LinhaDado("Fonte", focus.fonte)
            }
            SerraCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Eyebrow("Escala de intensidade · FRP em MW", Modifier.padding(start = 4.dp, bottom = 6.dp))
                    EscalaIntensidade(atual = focus.nivel)
                }
            }
            Row(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Rounded.Info, contentDescription = null, tint = Inpe, modifier = Modifier.size(18.dp))
                Text(
                    "Um foco de calor indica temperatura elevada na superfície e pode não ser um incêndio ativo. Este dado é somente leitura.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }
        }
        SerraButton(
            "Registrar o que estou vendo",
            onRegister,
            Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
            tom = Tom.CARVAO,
            icon = Icons.Rounded.PhotoCamera
        )
    }
}

@Composable
private fun LinhaDado(rotulo: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Eyebrow(rotulo, Modifier.weight(1f))
        Text(valor, style = Dado, color = Carvao)
    }
}

@Composable
private fun Divisor() = Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(1.dp).background(Linha))

private fun formatOfficialDate(timestamp: Long): String =
    SimpleDateFormat("dd/MM/yyyy · HH:mm", Locale.forLanguageTag("pt-BR")).format(Date(timestamp))
