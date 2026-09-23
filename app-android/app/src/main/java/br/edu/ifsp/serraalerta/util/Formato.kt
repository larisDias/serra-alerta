package br.edu.ifsp.serraalerta.util

import java.util.Locale

fun tempoRelativo(momento: Long, agora: Long = System.currentTimeMillis()): String {
    val min = (agora - momento) / 60_000
    return when {
        min < 1 -> "agora"
        min < 60 -> "há $min min"
        min < 60 * 24 -> "há ${min / 60} h"
        else -> "há ${min / (60 * 24)} d"
    }
}

fun coordenadas(lat: Double, lon: Double): String =
    String.format(Locale.US, "%.5f, %.5f", lat, lon)
