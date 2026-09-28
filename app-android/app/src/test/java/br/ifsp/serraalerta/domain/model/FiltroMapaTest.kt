package br.ifsp.serraalerta.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FiltroMapaTest {
    private val dia = 24L * 60L * 60L * 1_000L
    private val agora = 100 * dia

    private fun relato(dataHora: Long, categoria: CategoriaOcorrencia = CategoriaOcorrencia.QUEIMADA_IRREGULAR) =
        Ocorrencia(latitude = 0.0, longitude = 0.0, categoria = categoria, descricao = null, fotos = emptyList(), dataHoraRegistro = dataHora)

    @Test
    fun `periodo relativo exclui relatos mais antigos`() {
        val filtro = FiltroMapa(periodo = Periodo.HORAS_24)
        assertTrue(filtro.inclui(relato(agora - dia / 2), agora))
        assertFalse(filtro.inclui(relato(agora - 2 * dia), agora))
    }

    @Test
    fun `intervalo de datas inclui o dia final inteiro`() {
        val filtro = FiltroMapa(periodo = Periodo.DATAS, inicio = 10 * dia, fim = 12 * dia)
        assertTrue(filtro.inclui(relato(12 * dia + dia - 1), agora))
        assertFalse(filtro.inclui(relato(13 * dia), agora))
        assertFalse(filtro.inclui(relato(10 * dia - 1), agora))
    }

    @Test
    fun `camada e categoria desligadas escondem o relato`() {
        assertFalse(FiltroMapa(relatos = false).inclui(relato(agora), agora))
        val soControlada = FiltroMapa(categorias = setOf(CategoriaOcorrencia.QUEIMA_CONTROLADA))
        assertFalse(soControlada.inclui(relato(agora), agora))
    }
}
