package br.edu.ifsp.serraalerta.data

import org.osmdroid.util.GeoPoint

/** Referências geográficas aproximadas de São João da Boa Vista usadas no protótipo. */
object Regiao {
    val CENTRO = GeoPoint(-21.9694, -46.7981)
    val SERRA_DA_PAULISTA = GeoPoint(-21.9280, -46.7350)

    /** Raio (km) a partir do centro considerado "dentro da área de cobertura". */
    const val RAIO_COBERTURA_KM = 45.0

    val pontosDeReferencia = listOf(
        "Serra da Paulista" to SERRA_DA_PAULISTA,
        "Centro" to CENTRO,
        "Fazenda Cachoeira" to GeoPoint(-21.9450, -46.7660),
        "Rodovia SP-342" to GeoPoint(-21.9560, -46.7560),
        "Córrego da Aliança" to GeoPoint(-21.9880, -46.7780),
        "Ribeirão dos Porcos" to GeoPoint(-21.9930, -46.8700),
        "Córrego São Pedro" to GeoPoint(-22.0120, -46.8450),
        "Ribeirão do Paraíso" to GeoPoint(-21.9300, -46.8050),
    )

    fun referenciaMaisProxima(p: GeoPoint): String =
        pontosDeReferencia.minBy { it.second.distanceToAsDouble(p) }.first

    fun dentroDaCobertura(p: GeoPoint): Boolean = CENTRO.distanceToAsDouble(p) / 1000.0 <= RAIO_COBERTURA_KM
}
