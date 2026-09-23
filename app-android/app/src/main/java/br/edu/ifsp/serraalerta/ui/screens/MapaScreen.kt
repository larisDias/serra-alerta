package br.edu.ifsp.serraalerta.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.EditLocationAlt
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import br.edu.ifsp.serraalerta.data.Relato
import br.edu.ifsp.serraalerta.data.TipoFoco
import br.edu.ifsp.serraalerta.ui.Aba
import br.edu.ifsp.serraalerta.ui.Camada
import br.edu.ifsp.serraalerta.ui.mostrarRelatos
import br.edu.ifsp.serraalerta.ui.theme.Borda
import br.edu.ifsp.serraalerta.ui.theme.Carvao
import br.edu.ifsp.serraalerta.ui.theme.Laranja
import br.edu.ifsp.serraalerta.ui.theme.Texto2
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun MapaScreen(
    mapa: MapView,
    relatos: List<Relato>,
    filtro: TipoFoco?,
    onFiltro: (TipoFoco?) -> Unit,
    onSelecionar: (Relato) -> Unit,
    onLocalizar: () -> Unit,
    marcandoNoMapa: Boolean,
    onMarcarNoMapa: (Boolean) -> Unit,
    onConfirmarLocal: (GeoPoint) -> Unit,
) {
    val visiveis = if (filtro == null) relatos else relatos.filter { it.tipo == filtro }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapa },
            update = { it.mostrarRelatos(visiveis, onSelecionar) },
            modifier = Modifier.fillMaxSize(),
        )

        Column(Modifier.statusBarsPadding().padding(top = 8.dp)) {
            Cabecalho(total = relatos.size, mapa = mapa, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))
            if (!marcandoNoMapa) FiltrosTipo(relatos, filtro, onFiltro)
            Text(
                "© OpenStreetMap",
                fontSize = 11.sp,
                color = Texto2,
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = .8f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }

        Column(
            Modifier.align(Alignment.CenterEnd).padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(shape = RoundedCornerShape(16.dp), shadowElevation = 3.dp, color = Color.White) {
                Column {
                    BotaoMapa(Icons.Outlined.Add, "Aproximar", semFundo = true) { mapa.controller.zoomIn() }
                    HorizontalDivider(Modifier.width(52.dp), color = Borda)
                    BotaoMapa(Icons.Outlined.Remove, "Afastar", semFundo = true) { mapa.controller.zoomOut() }
                }
            }
            BotaoMapa(Icons.Outlined.MyLocation, "Minha localização", onClick = onLocalizar)
            BotaoMapa(
                Icons.Outlined.EditLocationAlt,
                "Marcar local no mapa",
                ativo = marcandoNoMapa,
            ) { onMarcarNoMapa(!marcandoNoMapa) }
        }

        if (marcandoNoMapa) {
            // Pino fixo no centro: o usuário arrasta o mapa por baixo dele.
            Icon(
                Icons.Filled.Place, null, tint = Laranja,
                modifier = Modifier.align(Alignment.Center).size(48.dp).offset(y = (-24).dp),
            )
            Surface(
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 8.dp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Marcar local do foco", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        "Arraste o mapa até o ponto onde você vê fumaça ou fogo. Útil quando o foco está longe de você.",
                        color = Texto2, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { onMarcarNoMapa(false) }, modifier = Modifier.weight(1f).height(52.dp)) {
                            Text("Cancelar", color = Carvao)
                        }
                        Button(
                            onClick = { onConfirmarLocal(GeoPoint(mapa.mapCenter.latitude, mapa.mapCenter.longitude)) },
                            modifier = Modifier.weight(1.4f).height(52.dp),
                        ) { Text("Usar este local", fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
        } else {
            AvisoPreliminar(
                Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(start = 16.dp, end = 16.dp, bottom = 104.dp),
            )
        }
    }
}

@Composable
private fun Cabecalho(total: Int, mapa: MapView, modifier: Modifier = Modifier) {
    var menuCamadas by remember { mutableStateOf(false) }
    var camada by remember { mutableStateOf(Camada.PADRAO) }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = .96f),
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(Laranja),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.LocalFireDepartment, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Column(Modifier.weight(1f).padding(start = 14.dp)) {
                Text("Serra Alerta", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Carvao)
                Text("$total relatos · São João da Boa Vista", fontSize = 14.sp, color = Texto2)
            }
            Box {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Borda, RoundedCornerShape(14.dp))
                        .clickable { menuCamadas = true },
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Outlined.Layers, "Camadas do mapa", tint = Carvao) }
                DropdownMenu(expanded = menuCamadas, onDismissRequest = { menuCamadas = false }) {
                    Camada.entries.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.rotulo, fontWeight = if (c == camada) FontWeight.Bold else FontWeight.Normal) },
                            onClick = {
                                camada = c
                                mapa.setTileSource(c.fonte)
                                menuCamadas = false
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FiltrosTipo(relatos: List<Relato>, filtro: TipoFoco?, onFiltro: (TipoFoco?) -> Unit) {
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ChipFiltro("Todos", relatos.size, null, filtro == null) { onFiltro(null) }
        TipoFoco.entries.forEach { t ->
            ChipFiltro(t.rotulo, relatos.count { it.tipo == t }, Color(t.cor), filtro == t) {
                onFiltro(if (filtro == t) null else t)
            }
        }
    }
}

@Composable
private fun ChipFiltro(texto: String, qtd: Int, cor: Color?, ativo: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (ativo) Carvao else Color.White,
        border = if (ativo) null else BorderStroke(1.dp, Borda),
        shadowElevation = 2.dp,
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            if (cor != null) {
                Box(Modifier.size(9.dp).clip(CircleShape).background(cor))
                Spacer(Modifier.width(8.dp))
            }
            Text(texto, color = if (ativo) Color.White else Carvao, fontWeight = FontWeight.Medium)
            Spacer(Modifier.width(8.dp))
            Text("$qtd", color = if (ativo) Color.White.copy(alpha = .75f) else Texto2)
        }
    }
}

@Composable
private fun BotaoMapa(
    icone: ImageVector,
    descricao: String,
    semFundo: Boolean = false,
    ativo: Boolean = false,
    onClick: () -> Unit,
) {
    val mod = if (semFundo) Modifier.size(52.dp) else Modifier.size(52.dp).shadow(3.dp, RoundedCornerShape(16.dp))
    Box(
        mod
            .clip(RoundedCornerShape(16.dp))
            .background(if (ativo) Laranja else Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icone, descricao, tint = if (ativo) Color.White else Carvao)
    }
}

@Composable
private fun AvisoPreliminar(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = .94f),
        border = BorderStroke(1.dp, Borda),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Info, null, tint = Texto2, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text("Relato é um alerta preliminar — não confirma um incêndio.", fontSize = 14.sp, color = Carvao)
        }
    }
}

@Composable
fun BarraInferior(aba: Aba, onAba: (Aba) -> Unit, onReportar: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ItemBarra(Icons.Outlined.Map, "Mapa", aba == Aba.MAPA, Modifier.weight(1f)) { onAba(Aba.MAPA) }
            Button(
                onClick = onReportar,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Laranja),
                modifier = Modifier.weight(2.2f).height(60.dp),
            ) {
                Icon(Icons.Outlined.LocalFireDepartment, null)
                Spacer(Modifier.width(10.dp))
                Text("Reportar foco", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            ItemBarra(Icons.Outlined.TrackChanges, "Áreas", aba == Aba.AREAS, Modifier.weight(1f)) { onAba(Aba.AREAS) }
        }
    }
}

@Composable
private fun ItemBarra(icone: ImageVector, texto: String, ativo: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier.clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .clip(CircleShape)
                .background(if (ativo) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) { Icon(icone, texto, tint = Carvao) }
        Text(texto, fontSize = 13.sp, fontWeight = if (ativo) FontWeight.SemiBold else FontWeight.Normal, color = Carvao)
    }
}
