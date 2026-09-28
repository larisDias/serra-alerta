package br.ifsp.serraalerta.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class Coordenada(val latitude: Double, val longitude: Double, val precisaoMetros: Float? = null) {
    fun distanciaAte(outra: Coordenada): Float = FloatArray(1).also {
        Location.distanceBetween(latitude, longitude, outra.latitude, outra.longitude, it)
    }[0]
}

val CentroSerraPaulista = Coordenada(-21.9695, -46.7985)

class LocationRepository(context: Context) {
    private val client = LocationServices.getFusedLocationProviderClient(context.applicationContext)

    @SuppressLint("MissingPermission")
    suspend fun obterLocalizacaoAtual(altaPrecisao: Boolean = true): Result<Coordenada> = runCatching {
        suspendCancellableCoroutine { continuation ->
            val cancellation = CancellationTokenSource()
            val prioridade = if (altaPrecisao) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY
            client.getCurrentLocation(prioridade, cancellation.token)
                .addOnSuccessListener { location: Location? ->
                    if (location == null) {
                        continuation.resumeWithException(IllegalStateException("Não foi possível obter sua localização."))
                    } else {
                        continuation.resume(Coordenada(location.latitude, location.longitude, location.accuracy.takeIf { location.hasAccuracy() }))
                    }
                }
                .addOnFailureListener { error -> continuation.resumeWithException(error) }
            continuation.invokeOnCancellation { cancellation.cancel() }
        }
    }
}
