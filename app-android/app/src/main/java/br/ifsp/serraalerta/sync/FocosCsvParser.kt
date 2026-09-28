package br.ifsp.serraalerta.sync

import br.ifsp.serraalerta.domain.model.FocoOficial
import java.io.BufferedReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/** Lê os CSVs diários do INPE (`focos_diario_br_AAAAMMDD.csv`) e mantém só os focos do recorte do app. */
object FocosCsvParser {
    const val MIN_LAT = -22.2
    const val MAX_LAT = -21.5
    const val MIN_LON = -47.4
    const val MAX_LON = -46.7
    private const val FONTE = "INPE/BDQueimadas"

    fun parse(reader: BufferedReader): List<FocoOficial> {
        val cabecalho = reader.readLine()?.split(',')?.map { it.trim().trim('"') } ?: return emptyList()
        val iId = cabecalho.indexOf("id")
        val iLat = cabecalho.indexOf("lat")
        val iLon = cabecalho.indexOf("lon")
        val iData = cabecalho.indexOf("data_hora_gmt")
        val iFrp = cabecalho.indexOf("frp")
        if (iLat < 0 || iLon < 0) return emptyList()

        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return buildList {
            reader.forEachLine { linha ->
                val colunas = linha.split(',')
                val lat = colunas.getOrNull(iLat)?.trim()?.toDoubleOrNull() ?: return@forEachLine
                val lon = colunas.getOrNull(iLon)?.trim()?.toDoubleOrNull() ?: return@forEachLine
                if (lat !in MIN_LAT..MAX_LAT || lon !in MIN_LON..MAX_LON) return@forEachLine
                val data = colunas.getOrNull(iData)?.trim()
                add(
                    FocoOficial(
                        id = colunas.getOrNull(iId)?.trim()?.takeIf { it.isNotEmpty() } ?: "inpe-$lat-$lon-$data",
                        latitude = lat,
                        longitude = lon,
                        dataDeteccao = data?.let { runCatching { formato.parse(it)?.time }.getOrNull() }
                            ?: System.currentTimeMillis(),
                        frp = colunas.getOrNull(iFrp)?.trim()?.toDoubleOrNull(),
                        fonte = FONTE
                    )
                )
            }
        }
    }
}
