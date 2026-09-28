package br.ifsp.serraalerta.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Curvas de nível da serra: a assinatura visual do app. [progresso] (0–1) revela
 * as curvas de dentro para fora, para animar entradas.
 */
@Composable
fun TopoArt(
    color: Color,
    modifier: Modifier = Modifier,
    centro: Offset = Offset(.62f, .4f),
    niveis: Int = 16,
    progresso: Float = 1f
) {
    Canvas(modifier) {
        val c = Offset(size.width * centro.x, size.height * centro.y)
        val base = maxOf(size.width, size.height) * .06f
        val passo = maxOf(size.width, size.height) * .055f
        for (k in 0 until niveis) {
            val revelado = (progresso * niveis - k).coerceIn(0f, 1f)
            if (revelado <= 0f) continue
            val raio = base + passo * k
            val path = Path()
            val pontos = 96
            for (i in 0..pontos) {
                val t = (i.toFloat() / pontos) * 2 * PI.toFloat()
                val onda = 1f + .09f * sin(3 * t + k * .35f) + .05f * sin(5 * t + 1.3f + k * .12f) + .025f * cos(9 * t + k)
                val x = c.x + cos(t) * raio * onda * 1.25f
                val y = c.y + sin(t) * raio * onda * .82f
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            val indice = k % 4 == 3
            drawPath(
                path,
                color = color.copy(alpha = color.alpha * revelado * (.95f - k * .045f).coerceAtLeast(.12f)),
                style = Stroke(width = if (indice) 2.2f * density else 1.1f * density)
            )
        }
    }
}
