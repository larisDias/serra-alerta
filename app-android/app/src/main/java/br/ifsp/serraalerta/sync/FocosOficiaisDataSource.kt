package br.ifsp.serraalerta.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import br.ifsp.serraalerta.domain.model.FocoOficial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale

class FocosOficiaisDataSource(private val context: Context) {
    companion object {
        private const val ENDPOINT =
            "https://queimadas.dgi.inpe.br/queimadas/portal-static/estaticos/focos/focos.json"
        private const val MIN_LAT = -22.2
        private const val MAX_LAT = -21.5
        private const val MIN_LON = -47.4
        private const val MAX_LON = -46.7
    }

    suspend fun buscarFocos(): List<FocoOficial> = withContext(Dispatchers.IO) {
        check(estaConectado()) { "Sem conexão com a internet" }
        val connection = (URL(ENDPOINT).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8_000
            readTimeout = 12_000
            doInput = true
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "SerraAlerta/1.0 Android")
        }
        try {
            check(connection.responseCode in 200..299) { "Fonte oficial indisponível (${connection.responseCode})" }
            val payload = connection.inputStream.bufferedReader().use { it.readText() }
            parse(payload)
        } finally {
            connection.disconnect()
        }
    }

    private fun parse(payload: String): List<FocoOficial> {
        val root = payload.trim()
        val array = when {
            root.startsWith("[") -> JSONArray(root)
            else -> {
                val objectRoot = JSONObject(root)
                when {
                    objectRoot.optJSONArray("features") != null -> objectRoot.optJSONArray("features")!!
                    objectRoot.optJSONArray("data") != null -> objectRoot.optJSONArray("data")!!
                    objectRoot.optJSONArray("focos") != null -> objectRoot.optJSONArray("focos")!!
                    else -> JSONArray()
                }
            }
        }
        return buildList {
            for (index in 0 until array.length()) {
                val raw = array.optJSONObject(index) ?: continue
                val properties = raw.optJSONObject("properties") ?: raw
                val latitude = properties.number("latitude", "lat") ?: continue
                val longitude = properties.number("longitude", "lon", "long") ?: continue
                if (latitude !in MIN_LAT..MAX_LAT || longitude !in MIN_LON..MAX_LON) continue
                val date = parseDate(properties.string("datahora", "data_hora", "date"))
                add(
                    FocoOficial(
                        id = properties.string("id", "fid") ?: "inpe-$index-$latitude-$longitude",
                        latitude = latitude,
                        longitude = longitude,
                        dataDeteccao = date,
                        frp = properties.number("frp", "potencia_radiativa"),
                        fonte = "INPE/BDQueimadas"
                    )
                )
            }
        }
    }

    private fun JSONObject.number(vararg keys: String): Double? = keys.firstNotNullOfOrNull { key ->
        when (val value = opt(key)) {
            is Number -> value.toDouble()
            is String -> value.replace(',', '.').toDoubleOrNull()
            else -> null
        }
    }

    private fun JSONObject.string(vararg keys: String): String? = keys.firstNotNullOfOrNull { key ->
        optString(key).takeIf { it.isNotBlank() && it != "null" }
    }

    private fun parseDate(value: String?): Long {
        if (value == null) return System.currentTimeMillis()
        val patterns = listOf("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy HH:mm:ss")
        return patterns.firstNotNullOfOrNull { pattern ->
            runCatching { SimpleDateFormat(pattern, Locale.US).parse(value)?.time }.getOrNull()
        } ?: System.currentTimeMillis()
    }

    private fun estaConectado(): Boolean {
        val manager = context.getSystemService(ConnectivityManager::class.java)
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
