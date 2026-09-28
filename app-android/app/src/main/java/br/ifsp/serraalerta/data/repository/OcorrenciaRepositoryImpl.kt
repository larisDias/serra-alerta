package br.ifsp.serraalerta.data.repository

import br.ifsp.serraalerta.data.local.dao.OcorrenciaDao
import br.ifsp.serraalerta.data.local.entity.toDomain
import br.ifsp.serraalerta.data.local.entity.toEntity
import br.ifsp.serraalerta.domain.model.Ocorrencia
import br.ifsp.serraalerta.domain.model.StatusEnvio
import br.ifsp.serraalerta.domain.repository.OcorrenciaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OcorrenciaRepositoryImpl(
    private val dao: OcorrenciaDao
) : OcorrenciaRepository {
    override fun observarTodas(): Flow<List<Ocorrencia>> = dao.observarTodas().map { entidades ->
        entidades.map { it.toDomain() }
    }

    override fun observarPorId(id: String): Flow<Ocorrencia?> = dao.observarPorId(id).map { it?.toDomain() }

    override suspend fun inserir(ocorrencia: Ocorrencia) = dao.inserir(ocorrencia.toEntity())

    override suspend fun marcarComoCompartilhada(id: String) =
        dao.atualizarStatus(id, StatusEnvio.COMPARTILHADO.valor)

    override suspend fun excluir(id: String) = dao.excluir(id)

    override suspend fun limparHistorico() = dao.limparTodas()
}
