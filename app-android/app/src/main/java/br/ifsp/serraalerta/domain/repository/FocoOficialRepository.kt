package br.ifsp.serraalerta.domain.repository

import br.ifsp.serraalerta.domain.model.FocoOficial
import kotlinx.coroutines.flow.Flow

interface FocoOficialRepository {
    fun observarTodos(): Flow<List<FocoOficial>>
    suspend fun atualizarSeConectado(): Result<Int>
}
