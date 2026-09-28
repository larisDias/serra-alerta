package br.ifsp.serraalerta.domain.repository

import br.ifsp.serraalerta.domain.model.Ocorrencia
import kotlinx.coroutines.flow.Flow

interface OcorrenciaRepository {
    fun observarTodas(): Flow<List<Ocorrencia>>
    fun observarPorId(id: String): Flow<Ocorrencia?>
    suspend fun inserir(ocorrencia: Ocorrencia)
    suspend fun marcarComoCompartilhada(id: String)
    suspend fun excluir(id: String)
    suspend fun limparHistorico()
}
