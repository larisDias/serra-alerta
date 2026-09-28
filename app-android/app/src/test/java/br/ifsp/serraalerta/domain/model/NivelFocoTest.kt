package br.ifsp.serraalerta.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class NivelFocoTest {
    @Test
    fun `frp define o nível pelas faixas de intensidade`() {
        assertEquals(NivelFoco.BAIXO, NivelFoco.deFrp(0.0))
        assertEquals(NivelFoco.BAIXO, NivelFoco.deFrp(9.9))
        assertEquals(NivelFoco.MODERADO, NivelFoco.deFrp(10.0))
        assertEquals(NivelFoco.ALTO, NivelFoco.deFrp(50.0))
        assertEquals(NivelFoco.EXTREMO, NivelFoco.deFrp(100.0))
    }

    @Test
    fun `frp ausente ou inválido vira nível desconhecido`() {
        assertEquals(NivelFoco.DESCONHECIDO, NivelFoco.deFrp(null))
        assertEquals(NivelFoco.DESCONHECIDO, NivelFoco.deFrp(-1.0))
    }
}
