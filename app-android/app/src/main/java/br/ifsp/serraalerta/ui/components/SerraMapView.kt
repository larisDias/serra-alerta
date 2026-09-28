package br.ifsp.serraalerta.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import br.ifsp.serraalerta.domain.model.CategoriaOcorrencia
import br.ifsp.serraalerta.domain.model.FocoOficial
import br.ifsp.serraalerta.domain.model.NivelFoco
import br.ifsp.serraalerta.domain.model.Ocorrencia
import br.ifsp.serraalerta.location.CentroSerraPaulista
import br.ifsp.serraalerta.location.Coordenada
import br.ifsp.serraalerta.ui.theme.Carvao
import br.ifsp.serraalerta.ui.theme.Inpe
import br.ifsp.serraalerta.ui.theme.Linha
import br.ifsp.serraalerta.ui.theme.Papel
import br.ifsp.serraalerta.ui.theme.PapelFundo
import br.ifsp.serraalerta.ui.theme.Superficie
import br.ifsp.serraalerta.ui.theme.cor
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/** Tiles do OSM dessaturados e aquecidos para conversar com a paleta de papel do app. */
internal fun MapView.aplicarEstiloSerra(context: Context) {
    Configuration.getInstance().userAgentValue = context.packageName
    setTileSource(TileSourceFactory.MAPNIK)
    setMultiTouchControls(true)
    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
    val filtro = ColorMatrix().apply { setSaturation(.22f) }
    filtro.postConcat(ColorMatrix(floatArrayOf(1.01f, 0f, 0f, 0f, 3f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, .97f, 0f, -2f, 0f, 0f, 0f, 1f, 0f)))
    overlayManager.tilesOverlay.setColorFilter(ColorMatrixColorFilter(filtro))
    overlayManager.tilesOverlay.loadingBackgroundColor = Papel.toArgb()
    overlayManager.tilesOverlay.loadingLineColor = PapelFundo.toArgb()
}

/** Contorno da chama em coordenadas 24×24; a base fica em y ≈ 19,6. */
private val Chama = Path().apply {
    moveTo(12f, 2.5f)
    cubicTo(12.9f, 5.8f, 17.6f, 8f, 17.6f, 13.6f)
    arcTo(RectF(6.4f, 8.4f, 17.6f, 19.6f), 0f, 180f, false)
    cubicTo(6.4f, 11.2f, 7.8f, 9.3f, 9.3f, 8f)
    cubicTo(9.5f, 9.9f, 10.4f, 11.1f, 11.6f, 11.7f)
    cubicTo(11.1f, 8.4f, 11.1f, 5.3f, 12f, 2.5f)
    close()
}

private val NucleoChama = Path().apply {
    moveTo(12f, 12.2f)
    cubicTo(12.5f, 13.9f, 14.7f, 14.9f, 14.7f, 16.9f)
    arcTo(RectF(9.3f, 14.2f, 14.7f, 19.6f), 0f, 180f, false)
    cubicTo(9.3f, 15.6f, 10f, 14.8f, 10.7f, 14.3f)
    cubicTo(10.9f, 15f, 11.3f, 15.4f, 11.8f, 15.6f)
    cubicTo(11.6f, 14.5f, 11.6f, 13.3f, 12f, 12.2f)
    close()
}

/** Âncora vertical do foguinho: a base da chama marca o ponto exato. */
private const val BaseChama = 19.6f / 24f

private class Marcadores(context: Context) {
    private val d = context.resources.displayMetrics.density
    private val res = context.resources
    private val relatos = mutableMapOf<CategoriaOcorrencia, Drawable>()
    private val focos = mutableMapOf<NivelFoco, Drawable>()

    fun relato(categoria: CategoriaOcorrencia): Drawable = relatos.getOrPut(categoria) { foguinho(categoria.cor.toArgb(), selo = null) }

    fun foco(nivel: NivelFoco): Drawable = focos.getOrPut(nivel) { foguinho(nivel.cor.toArgb(), selo = Inpe.toArgb()) }

    val voce: Drawable by lazy {
        desenhar(36) { c, s, p ->
            p.color = 0x29161311
            c.drawCircle(s / 2f, s / 2f, s / 2f, p)
            p.color = android.graphics.Color.WHITE
            c.drawCircle(s / 2f, s / 2f, 8 * d, p)
            p.color = Carvao.toArgb()
            c.drawCircle(s / 2f, s / 2f, 5.5f * d, p)
        }
    }

    private fun foguinho(cor: Int, selo: Int?): Drawable = desenhar(40) { c, s, p ->
        c.save()
        c.scale(s / 24f, s / 24f)
        p.style = Paint.Style.FILL
        p.color = 0x40161311
        c.save(); c.translate(0f, .9f); c.drawPath(Chama, p); c.restore()
        p.style = Paint.Style.STROKE
        p.strokeJoin = Paint.Join.ROUND
        p.strokeWidth = 2.4f
        p.color = android.graphics.Color.WHITE
        c.drawPath(Chama, p)
        p.style = Paint.Style.FILL
        p.color = cor
        c.drawPath(Chama, p)
        p.color = 0xFFFFE7A3.toInt()
        c.drawPath(NucleoChama, p)
        c.restore()
        selo?.let {
            p.color = android.graphics.Color.WHITE
            c.drawCircle(s * .76f, s * .74f, 5.5f * d, p)
            p.color = it
            c.drawCircle(s * .76f, s * .74f, 3.5f * d, p)
        }
    }

    private fun desenhar(tamanhoDp: Int, bloco: (Canvas, Float, Paint) -> Unit): Drawable {
        val s = (tamanhoDp * d).toInt()
        val bitmap = Bitmap.createBitmap(s, s, Bitmap.Config.ARGB_8888)
        bloco(Canvas(bitmap), s.toFloat(), Paint(Paint.ANTI_ALIAS_FLAG))
        return BitmapDrawable(res, bitmap)
    }
}

/**
 * [centralizar] é um contador: cada incremento move o mapa até [minhaPosicao],
 * mesmo que a posição seja igual à anterior.
 */
@Composable
fun SerraMapView(
    ocorrencias: List<Ocorrencia>,
    focosOficiais: List<FocoOficial>,
    modifier: Modifier = Modifier,
    minhaPosicao: Coordenada? = null,
    centralizar: Int = 0,
    localizando: Boolean = false,
    controlsPadding: PaddingValues = PaddingValues(12.dp),
    onLocalizar: () -> Unit = {},
    onOcorrenciaClick: (String) -> Unit,
    onFocoClick: (FocoOficial) -> Unit = {}
) {
    val context = LocalContext.current
    var mapa by remember { mutableStateOf<MapView?>(null) }
    val marcadores = remember { Marcadores(context) }

    LaunchedEffect(centralizar) {
        val posicao = minhaPosicao ?: return@LaunchedEffect
        if (centralizar > 0) mapa?.controller?.animateTo(GeoPoint(posicao.latitude, posicao.longitude), 15.0, 600L)
    }

    Box(modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MapView(context).apply {
                    aplicarEstiloSerra(context)
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    controller.setZoom(11.5)
                    controller.setCenter(GeoPoint(CentroSerraPaulista.latitude, CentroSerraPaulista.longitude))
                    mapa = this
                }
            },
            update = { map ->
                map.overlays.clear()
                focosOficiais.forEach { focus ->
                    map.overlays.add(Marker(map).apply {
                        position = GeoPoint(focus.latitude, focus.longitude)
                        title = "Foco oficial — ${focus.fonte}"
                        icon = marcadores.foco(focus.nivel)
                        setAnchor(Marker.ANCHOR_CENTER, BaseChama)
                        relatedObject = focus
                        setOnMarkerClickListener { marker, _ ->
                            onFocoClick(marker.relatedObject as FocoOficial)
                            true
                        }
                    })
                }
                ocorrencias.forEach { occurrence ->
                    map.overlays.add(Marker(map).apply {
                        position = GeoPoint(occurrence.latitude, occurrence.longitude)
                        title = "Relato: ${occurrence.categoria.rotulo}"
                        icon = marcadores.relato(occurrence.categoria)
                        setAnchor(Marker.ANCHOR_CENTER, BaseChama)
                        relatedObject = occurrence.id
                        setOnMarkerClickListener { marker, _ ->
                            onOcorrenciaClick(marker.relatedObject as String)
                            true
                        }
                    })
                }
                minhaPosicao?.let { posicao ->
                    map.overlays.add(Marker(map).apply {
                        position = GeoPoint(posicao.latitude, posicao.longitude)
                        title = "Você está aqui"
                        icon = marcadores.voce
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    })
                }
                map.invalidate()
            },
            onRelease = { map -> map.onDetach() }
        )
        Column(
            modifier = Modifier.align(Alignment.CenterEnd).padding(controlsPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = RoundedCornerShape(16.dp), color = Superficie, contentColor = Carvao, border = BorderStroke(1.dp, Linha), shadowElevation = 4.dp) {
                Column(Modifier.width(48.dp)) {
                    IconButton(onClick = { mapa?.controller?.zoomIn() }, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Rounded.Add, contentDescription = "Aproximar")
                    }
                    HorizontalDivider(color = Linha, modifier = Modifier.padding(horizontal = 10.dp))
                    IconButton(onClick = { mapa?.controller?.zoomOut() }, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Rounded.Remove, contentDescription = "Afastar")
                    }
                }
            }
            Surface(
                onClick = { if (!localizando) onLocalizar() },
                shape = RoundedCornerShape(16.dp),
                color = Superficie,
                contentColor = Carvao,
                border = BorderStroke(1.dp, Linha),
                shadowElevation = 4.dp,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (localizando) CircularProgressIndicator(color = Carvao, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    else Icon(Icons.Rounded.MyLocation, contentDescription = "Centralizar na minha localização", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

