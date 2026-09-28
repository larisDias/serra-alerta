package br.ifsp.serraalerta.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import br.ifsp.serraalerta.ui.theme.LinhaForte
import br.ifsp.serraalerta.ui.theme.PapelFundo

@Composable
fun PhotoPreview(path: String?, modifier: Modifier = Modifier) {
    // ponytail: amostragem fixa 1/4; trocar por Coil se as fotos ficarem pesadas em listas longas.
    val bitmap = remember(path) { path?.let(::carregarFoto)?.asImageBitmap() }
    Box(modifier.background(PapelFundo), contentAlignment = Alignment.Center) {
        if (bitmap != null) {
            Image(bitmap, contentDescription = "Foto da ocorrência", contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
        } else {
            Icon(Icons.Rounded.Image, contentDescription = "Sem foto", tint = LinhaForte, modifier = Modifier.size(28.dp))
        }
    }
}

/** O CameraX grava a rotação só na tag EXIF; o BitmapFactory a ignora, então giramos aqui. */
private fun carregarFoto(path: String): Bitmap? {
    val bitmap = BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = 4 }) ?: return null
    val graus = when (runCatching { ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) }.getOrNull()) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> return bitmap
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, Matrix().apply { postRotate(graus) }, true)
}
