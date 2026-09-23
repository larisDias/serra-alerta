package br.edu.ifsp.serraalerta.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifsp.serraalerta.data.Regiao
import br.edu.ifsp.serraalerta.data.Relato
import br.edu.ifsp.serraalerta.data.TipoFoco
import br.edu.ifsp.serraalerta.ui.theme.Areia
import br.edu.ifsp.serraalerta.ui.theme.Borda
import br.edu.ifsp.serraalerta.ui.theme.Carvao
import br.edu.ifsp.serraalerta.ui.theme.Laranja
import br.edu.ifsp.serraalerta.ui.theme.Texto2
import br.edu.ifsp.serraalerta.ui.theme.Verde
import br.edu.ifsp.serraalerta.util.tempoRelativo
import org.osmdroid.util.GeoPoint
import kotlin.math.floor

/** Agrupamento simples em células de ~2,5 km para destacar áreas com mais ocorrências. */
data class Nucleo(val nome: String, val centro: GeoPoint, val relatos: List<Relato>) {
    val predominante: TipoFoco get() = relatos.groupingBy { it.tipo }.eachCount().maxBy { it.value }.key
}

fun agruparEmNucleos(relatos: List<Relato>, celulaGraus: Double = 0.025): List<Nucleo> =
    relatos
        .groupBy { floor(it.latitude / celulaGraus).toInt() to floor(it.longitude / celulaGraus).toInt() }
        .values
        .map { grupo ->
            val c = GeoPoint(grupo.map { it.latitude }.average(), grupo.map { it.longitude }.average())
            Nucleo(Regiao.referenciaMaisProxima(c), c, grupo)
        }
        .sortedByDescending { it.relatos.size }

@Composable
fun AreasScreen(relatos: List<Relato>, onVerNoMapa: (GeoPoint) -> Unit) {
    val nucleos = remember(relatos) { agruparEmNucleos(relatos) }
    val ultimas24h = relatos.count { System.currentTimeMillis() - it.criadoEm < 24 * 3_600_000L }
    val maximo = nucleos.maxOfOrNull { it.relatos.size } ?: 1

    LazyColumn(
        Modifier.fillMaxSize().background(Areia).statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 130.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Áreas de ocorrência", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Carvao)
            Text(
                "Onde os relatos da comunidade se concentram.",
                fontSize = 15.sp, color = Texto2, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Numero("${relatos.size}", "relatos", Modifier.weight(1f))
                Numero("$ultimas24h", "últimas 24 h", Modifier.weight(1f))
                Numero("${nucleos.size}", "núcleos", Modifier.weight(1f))
            }
        }
        item {
            Text(
                "NÚCLEOS COM MAIS RELATOS",
                fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Texto2, letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
        items(nucleos) { n ->
            CartaoNucleo(n, n.relatos.size.toFloat() / maximo) { onVerNoMapa(n.centro) }
        }
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFE6F0EA),
                modifier = Modifier.padding(top = 12.dp),
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Park, null, tint = Verde)
                        Spacer(Modifier.width(10.dp))
                        Text("Área prioritária", fontWeight = FontWeight.Bold, color = Verde)
                    }
                    Text(
                        "A Serra da Paulista integra as Áreas Prioritárias para a Biodiversidade de importância " +
                            "\"Extremamente Alta\" (MMA) e a Reserva da Biosfera da Mata Atlântica.",
                        fontSize = 14.sp, color = Carvao, modifier = Modifier.padding(top = 8.dp),
                    )
                    Text(
                        "Ver Serra da Paulista no mapa",
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Verde,
                        modifier = Modifier.padding(top = 10.dp).clickable { onVerNoMapa(Regiao.SERRA_DA_PAULISTA) },
                    )
                }
            }
        }
    }
}

@Composable
private fun Numero(valor: String, rotulo: String, modifier: Modifier) {
    Surface(shape = RoundedCornerShape(18.dp), color = Color.White, border = BorderStroke(1.dp, Borda), modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            Text(valor, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Laranja)
            Text(rotulo, fontSize = 13.sp, color = Texto2)
        }
    }
}

@Composable
private fun CartaoNucleo(n: Nucleo, intensidade: Float, onClick: () -> Unit) {
    val cor = Color(n.predominante.cor)
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Borda),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(n.nome, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Carvao)
                    Text(
                        "${n.relatos.size} relato(s) · mais recente ${tempoRelativo(n.relatos.maxOf { it.criadoEm })}",
                        fontSize = 13.sp, color = Texto2,
                    )
                }
                Icon(Icons.AutoMirrored.Outlined.ArrowForward, "Ver no mapa", tint = Texto2)
            }
            Box(
                Modifier.padding(top = 12.dp).fillMaxWidth().height(8.dp).clip(CircleShape).background(Color(0xFFF1ECE5)),
            ) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(intensidade.coerceIn(0.08f, 1f)).clip(CircleShape).background(cor))
            }
            Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(cor))
                Spacer(Modifier.width(6.dp))
                Text("Predomínio: ${n.predominante.rotulo}", fontSize = 12.sp, color = Texto2)
            }
        }
    }
}
