package br.ifsp.serraalerta.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class OcorrenciaTest {
    @Test
    fun `categoria persiste e recupera pelo valor de armazenamento`() {
        CategoriaOcorrencia.entries.forEach { category ->
            assertEquals(category, CategoriaOcorrencia.fromValor(category.valor))
        }
    }

    @Test
    fun `categoria desconhecida usa fumaça não identificada como fallback seguro`() {
        assertEquals(
            CategoriaOcorrencia.FUMACA_NAO_IDENTIFICADA,
            CategoriaOcorrencia.fromValor("categoria-inexistente")
        )
    }

    @Test
    fun `ocorrencia cria identificadores diferentes por padrão`() {
        val first = Ocorrencia(
            latitude = 0.0,
            longitude = 0.0,
            categoria = CategoriaOcorrencia.QUEIMA_CONTROLADA,
            descricao = null,
            fotos = listOf("/tmp/a.jpg"),
            dataHoraRegistro = 1L
        )
        val second = Ocorrencia(
            latitude = 0.0,
            longitude = 0.0,
            categoria = CategoriaOcorrencia.QUEIMA_CONTROLADA,
            descricao = null,
            fotos = listOf("/tmp/a.jpg"),
            dataHoraRegistro = 1L
        )
        assertNotEquals(first.id, second.id)
    }
}
