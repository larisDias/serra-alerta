package br.ifsp.serraalerta.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import br.ifsp.serraalerta.domain.model.FocoOficial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/** Busca os CSVs diários públicos do INPE dos últimos [DIAS] dias, o mesmo recorte usado pelo site. */
class FocosOficiaisDataSource(private val context: Context) {
    companion object {
        private const val URL_DIARIO =
            "https://dataserver-coids.inpe.br/queimadas/queimadas/focos/csv/diario/Brasil/focos_diario_br_%s.csv"
        private const val DIAS = 7
    }

    suspend fun buscarFocos(): List<FocoOficial> = withContext(Dispatchers.IO) {
        check(estaConectado()) { "Sem conexão com a internet" }
        val resultados = coroutineScope {
            datasRecentes().map { data -> async { runCatching { baixarDia(data) } } }.awaitAll()
        }
        // O arquivo do dia corrente pode ainda não existir; só falha se nenhum dia respondeu.
        check(resultados.any { it.isSuccess }) {
            "Fonte oficial indisponível (${resultados.firstNotNullOfOrNull { it.exceptionOrNull()?.message }})"
        }
        resultados.flatMap { it.getOrDefault(emptyList()) }.distinctBy { it.id }
    }

    private fun datasRecentes(): List<String> {
        val formato = SimpleDateFormat("yyyyMMdd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
        val calendario = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        return List(DIAS) {
            formato.format(calendario.time).also { calendario.add(Calendar.DAY_OF_MONTH, -1) }
        }
    }

    private fun baixarDia(data: String): List<FocoOficial> {
        val connection = (URL(URL_DIARIO.format(data)).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8_000
            readTimeout = 25_000
            doInput = true
            setRequestProperty("User-Agent", "SerraAlerta/1.0 Android")
        }
        try {
            check(connection.responseCode in 200..299) { "HTTP ${connection.responseCode}" }
            return connection.inputStream.bufferedReader().use { FocosCsvParser.parse(it) }
        } finally {
            connection.disconnect()
        }
    }

    private fun estaConectado(): Boolean {
        val manager = context.getSystemService(ConnectivityManager::class.java)
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
