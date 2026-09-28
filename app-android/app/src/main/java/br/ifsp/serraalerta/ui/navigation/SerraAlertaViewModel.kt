package br.ifsp.serraalerta.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.ifsp.serraalerta.Preferences
import br.ifsp.serraalerta.domain.model.CategoriaOcorrencia
import br.ifsp.serraalerta.domain.model.FiltroMapa
import br.ifsp.serraalerta.domain.model.FocoOficial
import br.ifsp.serraalerta.domain.model.Ocorrencia
import br.ifsp.serraalerta.domain.repository.FocoOficialRepository
import br.ifsp.serraalerta.domain.repository.OcorrenciaRepository
import br.ifsp.serraalerta.domain.usecase.ExcluirOcorrenciaUseCase
import br.ifsp.serraalerta.domain.usecase.LimparHistoricoUseCase
import br.ifsp.serraalerta.domain.usecase.MarcarOcorrenciaCompartilhadaUseCase
import br.ifsp.serraalerta.domain.usecase.ObservarOcorrenciaUseCase
import br.ifsp.serraalerta.domain.usecase.ObservarOcorrenciasUseCase
import br.ifsp.serraalerta.domain.usecase.SalvarOcorrenciaUseCase
import br.ifsp.serraalerta.location.Coordenada
import br.ifsp.serraalerta.location.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

const val MAX_FOTOS = 3

/** Registro em andamento, compartilhado entre câmera, formulário e ajuste de localização. */
data class Rascunho(
    val fotos: List<String> = emptyList(),
    val gps: Coordenada? = null,
    val posicaoAjustada: Coordenada? = null,
    val localizando: Boolean = false,
    val falhaLocalizacao: Boolean = false
) {
    val posicao: Coordenada? get() = posicaoAjustada ?: gps
}

enum class EstadoFocos { OCIOSO, ATUALIZANDO, FALHOU }

class SerraAlertaViewModel(
    ocorrenciaRepository: OcorrenciaRepository,
    private val focoOficialRepository: FocoOficialRepository,
    private val locationRepository: LocationRepository,
    private val preferences: Preferences
) : ViewModel() {
    private val observarOcorrencias = ObservarOcorrenciasUseCase(ocorrenciaRepository)
    private val observarOcorrencia = ObservarOcorrenciaUseCase(ocorrenciaRepository)
    private val salvarOcorrencia = SalvarOcorrenciaUseCase(ocorrenciaRepository)
    private val marcarCompartilhada = MarcarOcorrenciaCompartilhadaUseCase(ocorrenciaRepository)
    private val limparHistorico = LimparHistoricoUseCase(ocorrenciaRepository)
    private val excluirOcorrencia = ExcluirOcorrenciaUseCase(ocorrenciaRepository)

    private val _focoSelecionado = MutableStateFlow<FocoOficial?>(null)
    val focoSelecionado: StateFlow<FocoOficial?> = _focoSelecionado.asStateFlow()

    val ocorrencias: StateFlow<List<Ocorrencia>> = observarOcorrencias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val focosOficiais = focoOficialRepository.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val filtro = MutableStateFlow(FiltroMapa())

    private val _estadoFocos = MutableStateFlow(EstadoFocos.OCIOSO)
    val estadoFocos: StateFlow<EstadoFocos> = _estadoFocos.asStateFlow()
    private val _ultimaAtualizacaoFocos = MutableStateFlow(preferences.ultimaAtualizacaoFocos)
    val ultimaAtualizacaoFocos: StateFlow<Long> = _ultimaAtualizacaoFocos.asStateFlow()

    private val _rascunho = MutableStateFlow(Rascunho())
    val rascunho: StateFlow<Rascunho> = _rascunho.asStateFlow()

    fun observarPorId(id: String) = observarOcorrencia(id)

    fun selecionarFoco(foco: FocoOficial) {
        _focoSelecionado.value = foco
    }

    /** Evita baixar os focos de novo a cada volta ao mapa: só se a última atualização tiver mais de 10 min. */
    fun aoAbrirMapa() {
        val desatualizado = System.currentTimeMillis() - _ultimaAtualizacaoFocos.value > 10 * 60_000L
        if (preferences.atualizarFocosAoAbrir && desatualizado) atualizarFocosOficiais()
    }

    fun atualizarFocosOficiais() {
        if (_estadoFocos.value == EstadoFocos.ATUALIZANDO) return
        _estadoFocos.value = EstadoFocos.ATUALIZANDO
        viewModelScope.launch {
            focoOficialRepository.atualizarSeConectado()
                .onSuccess {
                    val agora = System.currentTimeMillis()
                    preferences.ultimaAtualizacaoFocos = agora
                    _ultimaAtualizacaoFocos.value = agora
                    _estadoFocos.value = EstadoFocos.OCIOSO
                }
                .onFailure { _estadoFocos.value = EstadoFocos.FALHOU }
        }
    }

    /** Começa um registro novo, descartando as fotos de um rascunho abandonado. */
    fun iniciarRascunho() {
        _rascunho.value.fotos.forEach { File(it).delete() }
        _rascunho.value = Rascunho()
    }

    fun adicionarFoto(caminho: String) = _rascunho.update { it.copy(fotos = (it.fotos + caminho).takeLast(MAX_FOTOS)) }

    fun removerFoto(caminho: String) {
        File(caminho).delete()
        _rascunho.update { it.copy(fotos = it.fotos - caminho) }
    }

    fun localizar() {
        if (_rascunho.value.localizando) return
        _rascunho.update { it.copy(localizando = true, falhaLocalizacao = false) }
        viewModelScope.launch {
            val resultado = locationRepository.obterLocalizacaoAtual(preferences.gpsAltaPrecisao)
            _rascunho.update {
                it.copy(gps = resultado.getOrNull() ?: it.gps, localizando = false, falhaLocalizacao = resultado.isFailure)
            }
        }
    }

    suspend fun localizacaoAtual(): Result<Coordenada> =
        locationRepository.obterLocalizacaoAtual(preferences.gpsAltaPrecisao)

    fun ajustarPosicao(coordenada: Coordenada) = _rascunho.update { it.copy(posicaoAjustada = coordenada) }

    fun registrar(categoria: CategoriaOcorrencia, descricao: String, onSaved: (String) -> Unit) {
        val rascunho = _rascunho.value
        val posicao = rascunho.posicao ?: return
        if (rascunho.fotos.isEmpty()) return
        val ocorrencia = Ocorrencia(
            latitude = posicao.latitude,
            longitude = posicao.longitude,
            categoria = categoria,
            descricao = descricao.trim().ifBlank { null },
            fotos = rascunho.fotos,
            dataHoraRegistro = System.currentTimeMillis(),
            precisaoMetros = posicao.precisaoMetros,
            ajustadaManualmente = rascunho.posicaoAjustada != null
        )
        viewModelScope.launch {
            salvarOcorrencia(ocorrencia)
            _rascunho.value = Rascunho()
            onSaved(ocorrencia.id)
        }
    }

    fun marcarCompartilhada(id: String) {
        viewModelScope.launch { marcarCompartilhada(id) }
    }

    /** Apaga o relato e as fotos dele do aparelho. */
    fun excluir(ocorrencia: Ocorrencia) {
        viewModelScope.launch {
            excluirOcorrencia(ocorrencia.id)
            ocorrencia.fotos.forEach { File(it).delete() }
        }
    }

    fun limparHistorico(diretorioFotos: File) {
        viewModelScope.launch {
            limparHistorico()
            diretorioFotos.deleteRecursively()
        }
    }
}

class SerraAlertaViewModelFactory(
    private val ocorrenciaRepository: OcorrenciaRepository,
    private val focoOficialRepository: FocoOficialRepository,
    private val locationRepository: LocationRepository,
    private val preferences: Preferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SerraAlertaViewModel(
        ocorrenciaRepository,
        focoOficialRepository,
        locationRepository,
        preferences
    ) as T
}
