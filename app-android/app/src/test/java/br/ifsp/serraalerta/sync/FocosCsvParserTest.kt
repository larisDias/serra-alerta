package br.ifsp.serraalerta.sync

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FocosCsvParserTest {
    private val csv = """
        id,lat,lon,data_hora_gmt,satelite,municipio,estado,bioma,frp
        a1,-21.95,-46.80,2026-09-27 17:25:00,NOAA-20,SAO JOAO DA BOA VISTA,SAO PAULO,Mata Atlantica,12.5
        a2,-10.00,-46.80,2026-09-27 17:25:00,NOAA-20,PALMAS,TOCANTINS,Cerrado,3.0
        a3,-21.90,-47.00,2026-09-27 18:00:00,AQUA,ESPIRITO SANTO DO PINHAL,SAO PAULO,Mata Atlantica,
    """.trimIndent()

    @Test
    fun mantemSomenteFocosDentroDoRecorte() {
        val focos = FocosCsvParser.parse(csv.reader().buffered())
        assertEquals(listOf("a1", "a3"), focos.map { it.id })
    }

    @Test
    fun leCoordenadasDataEFrp() {
        val foco = FocosCsvParser.parse(csv.reader().buffered()).first()
        assertEquals(-21.95, foco.latitude, 0.0)
        assertEquals(-46.80, foco.longitude, 0.0)
        assertEquals(12.5, foco.frp!!, 0.0)
        assertEquals(1_790_529_900_000L, foco.dataDeteccao)
    }

    @Test
    fun frpVazioViraNulo() {
        assertNull(FocosCsvParser.parse(csv.reader().buffered())[1].frp)
    }

    @Test
    fun arquivoVazioNaoGeraFocos() {
        assertEquals(0, FocosCsvParser.parse("".reader().buffered()).size)
    }
}
