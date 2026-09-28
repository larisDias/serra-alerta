package br.ifsp.serraalerta.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.ifsp.serraalerta.domain.model.CategoriaOcorrencia
import br.ifsp.serraalerta.ui.components.*
import br.ifsp.serraalerta.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    BarrasSobreEscuro()
    val curvas = remember { Animatable(0f) }
    val texto = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        launch { curvas.animateTo(1f, tween(1_100, easing = FastOutSlowInEasing)) }
        delay(180)
        texto.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        delay(500)
        onFinished()
    }
    Box(Modifier.fillMaxSize().background(Carvao)) {
        TopoArt(BrasaViva, Modifier.fillMaxSize(), centro = Offset(.7f, .34f), niveis = 18, progresso = curvas.value)
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 28.dp, vertical = 24.dp)
        ) {
            Eyebrow("IFSP · São João da Boa Vista", color = TextoSobreCarvaoSuave)
            Spacer(Modifier.weight(1f))
            Column(Modifier.graphicsLayer { alpha = texto.value; translationY = (1 - texto.value) * 40.dp.toPx() }) {
                Text("Serra", style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp, lineHeight = 68.sp), color = TextoSobreCarvao)
                Text("Alerta", style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp, lineHeight = 72.sp), color = BrasaViva)
                Text(
                    "Monitoramento colaborativo de queimadas",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoSobreCarvaoSuave,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }
            Spacer(Modifier.height(56.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Eyebrow("21°58′S · 46°47′W", color = TextoSobreCarvaoSuave, modifier = Modifier.weight(1f))
                Eyebrow("Serra da Paulista", color = TextoSobreCarvaoSuave)
            }
        }
    }
}

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    var page by rememberSaveable { mutableIntStateOf(0) }
    val ultima = page == 1
    Column(Modifier.fillMaxSize().background(Papel).statusBarsPadding().navigationBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(start = 24.dp, end = 12.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Eyebrow("0${page + 1} / 02", modifier = Modifier.weight(1f))
            TextButton(onClick = onFinished) { Text("Pular", color = TextoSecundario, style = MaterialTheme.typography.labelLarge) }
        }
        AnimatedContent(
            targetState = page,
            transitionSpec = {
                val direcao = if (targetState > initialState) 1 else -1
                (slideInHorizontally(tween(320)) { it / 6 * direcao } + fadeIn(tween(320)))
                    .togetherWith(slideOutHorizontally(tween(220)) { -it / 6 * direcao } + fadeOut(tween(180)))
            },
            label = "troca_de_introducao",
            modifier = Modifier.weight(1f)
        ) { atual ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
                if (atual == 0) PaginaRegistre() else PaginaAlerta()
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (0..1).forEach { index ->
                    val largura by animateDpAsState(if (page == index) 28.dp else 8.dp, label = "indicador_pagina_$index")
                    Box(Modifier.size(largura, 8.dp).clip(CircleShape).background(if (page == index) Carvao else LinhaForte))
                }
            }
            SerraButton(
                text = if (ultima) "Começar" else "Continuar",
                onClick = { if (ultima) onFinished() else page++ },
                tom = if (ultima) Tom.BRASA else Tom.CARVAO,
                icon = Icons.AutoMirrored.Rounded.ArrowForward,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PaginaRegistre() {
    Box(
        Modifier.fillMaxWidth().padding(top = 8.dp).height(250.dp).clip(RoundedCornerShape(28.dp)).background(Carvao)
    ) {
        TopoArt(BrasaViva.copy(alpha = .9f), Modifier.fillMaxSize(), centro = Offset(.55f, .55f), niveis = 12)
        Surface(shape = CircleShape, color = Superficie, modifier = Modifier.align(Alignment.TopStart).padding(18.dp)) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CategoryGlyph(CategoriaOcorrencia.QUEIMADA_IRREGULAR, size = 22.dp)
                Text("Relato · agora", style = MaterialTheme.typography.labelMedium, color = Carvao)
            }
        }
        Pill(
            "Foco INPE",
            icon = Icons.Rounded.SatelliteAlt,
            container = Inpe,
            content = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(18.dp)
        )
    }
    Text(
        "Viu fumaça ou fogo? Registre em segundos.",
        style = MaterialTheme.typography.displaySmall,
        color = Carvao,
        modifier = Modifier.padding(top = 28.dp)
    )
    Text(
        "Moradores, produtores e visitantes formam uma rede de vigilância da Serra da Paulista. Seu relato aparece no mapa junto aos focos de calor do INPE.",
        style = MaterialTheme.typography.bodyLarge,
        color = TextoSecundario,
        modifier = Modifier.padding(top = 12.dp)
    )
    Column(Modifier.padding(top = 22.dp, bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Recurso(Icons.Rounded.PhotoCamera, "Foto", Modifier.weight(1f))
            Recurso(Icons.Rounded.GpsFixed, "GPS automático", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Recurso(Icons.Rounded.PersonOff, "Sem cadastro", Modifier.weight(1f))
            Recurso(Icons.Rounded.CloudOff, "Funciona offline", Modifier.weight(1f))
        }
    }
}

@Composable
private fun Recurso(icon: ImageVector, label: String, modifier: Modifier) {
    SerraCard(modifier) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, contentDescription = null, tint = Brasa, modifier = Modifier.size(20.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = Carvao)
        }
    }
}

@Composable
private fun PaginaAlerta() {
    val context = LocalContext.current
    Text(
        "Um relato é um alerta, não uma confirmação.",
        style = MaterialTheme.typography.displaySmall,
        color = Carvao,
        modifier = Modifier.padding(top = 12.dp)
    )
    Text(
        "Fumaça ou calor nem sempre é incêndio. Cada ocorrência é um aviso preliminar para que Defesa Civil e Bombeiros verifiquem. Ao registrar, escolha o que melhor descreve o que você vê:",
        style = MaterialTheme.typography.bodyLarge,
        color = TextoSecundario,
        modifier = Modifier.padding(top = 12.dp)
    )
    SerraCard(Modifier.fillMaxWidth().padding(top = 20.dp)) {
        CategoriaOcorrencia.entries.forEachIndexed { i, categoria ->
            Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                CategoryGlyph(categoria, size = 36.dp)
                Column {
                    Text(categoria.rotulo, style = MaterialTheme.typography.titleSmall, color = Carvao)
                    Text(categoria.resumo, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                }
            }
            if (i < CategoriaOcorrencia.entries.lastIndex) Box(Modifier.fillMaxWidth().padding(start = 66.dp).height(1.dp).background(Linha))
        }
    }
    SerraCard(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp), container = Carvao, onClick = { abrirDiscador(context, "193") }) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(40.dp).background(Perigo, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Text(
                buildAnnotatedString {
                    append("Se houver risco a pessoas, animais ou casas, ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = BrasaViva)) { append("ligue 193") }
                    append(" antes de registrar.")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSobreCarvao
            )
        }
    }
}
