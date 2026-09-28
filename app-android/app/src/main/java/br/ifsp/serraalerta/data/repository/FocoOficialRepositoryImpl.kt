package br.ifsp.serraalerta.data.repository

import androidx.room.withTransaction
import br.ifsp.serraalerta.data.local.AppDatabase
import br.ifsp.serraalerta.data.local.entity.FocoOficialEntity
import br.ifsp.serraalerta.data.local.entity.toDomain
import br.ifsp.serraalerta.domain.model.FocoOficial
import br.ifsp.serraalerta.domain.repository.FocoOficialRepository
import br.ifsp.serraalerta.sync.FocosOficiaisDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FocoOficialRepositoryImpl(
    private val database: AppDatabase,
    private val dataSource: FocosOficiaisDataSource
) : FocoOficialRepository {
    override fun observarTodos(): Flow<List<FocoOficial>> =
        database.focoOficialDao().observarTodos().map { entities -> entities.map { it.toDomain() } }

    override suspend fun atualizarSeConectado(): Result<Int> = runCatching {
        val focos = dataSource.buscarFocos()
        database.withTransaction {
            database.focoOficialDao().limparTodos()
            database.focoOficialDao().inserirTodos(focos.map { foco ->
                FocoOficialEntity(
                    id = foco.id,
                    latitude = foco.latitude,
                    longitude = foco.longitude,
                    dataDeteccao = foco.dataDeteccao,
                    frp = foco.frp,
                    fonte = foco.fonte
                )
            })
        }
        focos.size
    }
}
