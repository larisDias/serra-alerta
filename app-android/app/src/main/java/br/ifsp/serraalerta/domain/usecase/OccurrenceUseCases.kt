package br.ifsp.serraalerta.domain.usecase

import br.ifsp.serraalerta.domain.model.Ocorrencia
import br.ifsp.serraalerta.domain.repository.OcorrenciaRepository

class ObservarOcorrenciasUseCase(private val repository: OcorrenciaRepository) {
    operator fun invoke() = repository.observarTodas()
}

class ObservarOcorrenciaUseCase(private val repository: OcorrenciaRepository) {
    operator fun invoke(id: String) = repository.observarPorId(id)
}

class SalvarOcorrenciaUseCase(private val repository: OcorrenciaRepository) {
    suspend operator fun invoke(ocorrencia: Ocorrencia) = repository.inserir(ocorrencia)
}

class MarcarOcorrenciaCompartilhadaUseCase(private val repository: OcorrenciaRepository) {
    suspend operator fun invoke(id: String) = repository.marcarComoCompartilhada(id)
}

class ExcluirOcorrenciaUseCase(private val repository: OcorrenciaRepository) {
    suspend operator fun invoke(id: String) = repository.excluir(id)
}

class LimparHistoricoUseCase(private val repository: OcorrenciaRepository) {
    suspend operator fun invoke() = repository.limparHistorico()
}
