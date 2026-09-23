package br.edu.ifsp.serraalerta.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import br.edu.ifsp.serraalerta.data.Regiao
import br.edu.ifsp.serraalerta.data.Relato
import br.edu.ifsp.serraalerta.data.TipoFoco
import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private const val ID_EU = "minha-posicao"

enum class Camada(val rotulo: String, val fonte: ITileSource) {
    PADRAO("Padrão", TileSourceFactory.MAPNIK),
    RELEVO("Relevo", TileSourceFactory.OpenTopo),
}

fun criarMapa(ctx: Context): MapView = MapView(ctx).apply {
    setTileSource(Camada.PADRAO.fonte)
    setMultiTouchControls(true)
    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
    isTilesScaledToDpi = true
    minZoomLevel = 9.0
    controller.setZoom(13.0)
    controller.setCenter(Regiao.CENTRO)
}

/** Recria os marcadores dos relatos, preservando o marcador da posição do usuário. */
fun MapView.mostrarRelatos(relatos: List<Relato>, onClique: (Relato) -> Unit) {
    overlays.removeAll { it is Marker && it.id != ID_EU }
    relatos.forEach { r ->
        overlays.add(Marker(this).apply {
            position = GeoPoint(r.latitude, r.longitude)
            icon = marcador(context, r.tipo)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            setOnMarkerClickListener { _, _ -> onClique(r); true }
        })
    }
    invalidate()
}

fun MapView.mostrarMinhaPosicao(p: GeoPoint) {
    overlays.removeAll { it is Marker && it.id == ID_EU }
    overlays.add(Marker(this).apply {
        id = ID_EU
        position = p
        icon = pontoAzul(context)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        setInfoWindow(null)
    })
    controller.animateTo(p, 15.0, 800L)
}

private fun marcador(ctx: Context, tipo: TipoFoco): Drawable {
    val d = ctx.resources.displayMetrics.density
    val s = (36 * d).toInt()
    val bmp = Bitmap.createBitmap(s, s, Bitmap.Config.ARGB_8888)
    val c = Canvas(bmp)
    val p = Paint(Paint.ANTI_ALIAS_FLAG)
    val cor = tipo.cor.toInt()
    p.color = cor; p.alpha = 60
    c.drawCircle(s / 2f, s / 2f, s / 2f, p)
    p.color = android.graphics.Color.WHITE
    c.drawCircle(s / 2f, s / 2f, s * 0.29f, p)
    p.color = cor
    c.drawCircle(s / 2f, s / 2f, s * 0.21f, p)
    return BitmapDrawable(ctx.resources, bmp)
}

private fun pontoAzul(ctx: Context): Drawable {
    val d = ctx.resources.displayMetrics.density
    val s = (26 * d).toInt()
    val bmp = Bitmap.createBitmap(s, s, Bitmap.Config.ARGB_8888)
    val c = Canvas(bmp)
    val p = Paint(Paint.ANTI_ALIAS_FLAG)
    p.color = 0x552F7CF6
    c.drawCircle(s / 2f, s / 2f, s / 2f, p)
    p.color = android.graphics.Color.WHITE
    c.drawCircle(s / 2f, s / 2f, s * 0.28f, p)
    p.color = 0xFF2F7CF6.toInt()
    c.drawCircle(s / 2f, s / 2f, s * 0.2f, p)
    return BitmapDrawable(ctx.resources, bmp)
}
