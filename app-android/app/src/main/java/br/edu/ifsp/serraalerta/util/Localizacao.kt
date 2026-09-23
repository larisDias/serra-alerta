package br.edu.ifsp.serraalerta.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint

object Localizacao {
    val PERMISSOES = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    fun temPermissao(ctx: Context) = PERMISSOES.any {
        ContextCompat.checkSelfPermission(ctx, it) == PackageManager.PERMISSION_GRANTED
    }

    /** Posição atual pelo FusedLocationProviderClient, ou null se indisponível. */
    @SuppressLint("MissingPermission")
    suspend fun atual(ctx: Context): GeoPoint? {
        if (!temPermissao(ctx)) return null
        val client = LocationServices.getFusedLocationProviderClient(ctx)
        return try {
            val loc = client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).await()
                ?: client.lastLocation.await()
            loc?.let { GeoPoint(it.latitude, it.longitude) }
        } catch (e: Exception) {
            null
        }
    }
}
