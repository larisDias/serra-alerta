package br.ifsp.serraalerta.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import br.ifsp.serraalerta.domain.model.Ocorrencia
import java.io.File

fun compartilharOcorrencia(context: Context, ocorrencia: Ocorrencia) {
    val texto = buildString {
        appendLine("Serra Alerta — relato de ocorrência")
        appendLine("Categoria: ${ocorrencia.categoria.rotulo}")
        appendLine("Localização: ${formatarCoordenada(ocorrencia.latitude, ocorrencia.longitude)}")
        appendLine("Mapa: https://www.openstreetmap.org/?mlat=${ocorrencia.latitude}&mlon=${ocorrencia.longitude}#map=16/${ocorrencia.latitude}/${ocorrencia.longitude}")
        appendLine("Data: ${formatarData(ocorrencia.dataHoraRegistro, "dd/MM/yyyy HH:mm")}")
        ocorrencia.descricao?.takeIf { it.isNotBlank() }?.let { appendLine("Descrição: $it") }
        appendLine("\nAlerta preliminar — confirme a situação com os órgãos competentes.")
    }
    val fotos = ocorrencia.fotos.map(::File).filter { it.exists() }.map {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it)
    }
    val send = when (fotos.size) {
        0 -> Intent(Intent.ACTION_SEND).setType("text/plain")
        1 -> Intent(Intent.ACTION_SEND).setType("image/jpeg").putExtra(Intent.EXTRA_STREAM, fotos[0])
        else -> Intent(Intent.ACTION_SEND_MULTIPLE).setType("image/jpeg")
            .putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList<Uri>(fotos))
    }.putExtra(Intent.EXTRA_TEXT, texto).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(Intent.createChooser(send, "Compartilhar ocorrência"))
}

fun abrirDiscador(context: Context, numero: String) {
    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero")))
}
