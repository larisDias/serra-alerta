package br.ifsp.serraalerta.domain

import br.ifsp.serraalerta.domain.model.StatusEnvio
import org.junit.Assert.assertEquals
import org.junit.Test

class StatusEnvioTest {
    @Test
    fun `status desconhecido permanece local para não perder o relato`() {
        assertEquals(StatusEnvio.REGISTRADO_LOCALMENTE, StatusEnvio.fromValor("desconhecido"))
    }
}
