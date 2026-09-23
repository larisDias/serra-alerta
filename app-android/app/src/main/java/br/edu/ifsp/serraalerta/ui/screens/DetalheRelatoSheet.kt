package br.edu.ifsp.serraalerta.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import br.edu.ifsp.serraalerta.ui.theme.Carvao
import br.edu.ifsp.serraalerta.ui.theme.Texto2
import br.edu.ifsp.serraalerta.util.coordenadas
import br.edu.ifsp.serraalerta.util.tempoRelativo
import coil.compose.AsyncImage
import org.osmdroid.util.GeoPoint
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheRelatoSheet(relato: Relato, onFechar: () -> Unit) {
    val ctx = LocalContext.current
    val cor = Color(relato.tipo.cor)
    val referencia = Regiao.referenciaMaisProxima(GeoPoint(relato.latitude, relato.longitude))

    fun discar(numero: String) = ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero")))

    ModalBottomSheet(
        onDismissRequest = onFechar,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
    ) {
        Column(Modifier.padding(horizontal = 20.dp).navigationBarsPadding().padding(bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier.clip(CircleShape).background(cor.copy(alpha = .12f)).padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(9.dp).clip(CircleShape).background(cor))
                    Spacer(Modifier.width(8.dp))
                    Text(relato.tipo.rotulo, color = cor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text("${relato.sinal.rotulo} · ${tempoRelativo(relato.criadoEm)}", color = Texto2, fontSize = 14.sp)
            }

            Text(
                "Próximo a $referencia",
                fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Carvao,
                modifier = Modifier.padding(top = 14.dp, bottom = 6.dp),
            )
            Text(relato.descricao, fontSize = 16.sp, color = Carvao)

            relato.fotoPath?.let { caminho ->
                AsyncImage(
                    model = File(caminho), contentDescription = "Foto do relato", contentScale = ContentScale.Crop,
                    modifier = Modifier.padding(top = 14.dp).fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)),
                )
            }

            Column(Modifier.padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LinhaInfo(Icons.Outlined.Place, coordenadas(relato.latitude, relato.longitude))
                LinhaInfo(Icons.Outlined.Schedule, "Registrado ${tempoRelativo(relato.criadoEm)}")
            }

            Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF3EEE7)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Info, null, tint = Texto2, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Alerta preliminar enviado pela comunidade. Aguarda validação da Defesa Civil / Bombeiros.",
                        fontSize = 13.sp, color = Texto2,
                    )
                }
            }

            Row(Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { discar("193") },
                    colors = ButtonDefaults.buttonColors(containerColor = Carvao),
                    modifier = Modifier.weight(1f).height(52.dp),
                ) {
                    Icon(Icons.Outlined.Phone, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Bombeiros 193")
                }
                OutlinedButton(onClick = { discar("199") }, modifier = Modifier.weight(1f).height(52.dp)) {
                    Text("Defesa Civil 199", color = Carvao)
                }
            }
        }
    }
}

@Composable
private fun LinhaInfo(icone: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icone, null, tint = Texto2, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(texto, fontSize = 14.sp, color = Texto2)
    }
}
