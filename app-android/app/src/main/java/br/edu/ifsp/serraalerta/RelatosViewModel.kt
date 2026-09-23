package br.edu.ifsp.serraalerta

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifsp.serraalerta.data.AppDatabase
import br.edu.ifsp.serraalerta.data.DadosExemplo
import br.edu.ifsp.serraalerta.data.Relato
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RelatosViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).relatoDao()

    val relatos: StateFlow<List<Relato>> = dao.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (dao.contar() == 0) dao.inserirTodos(DadosExemplo.relatos())
        }
    }

    fun salvar(relato: Relato) {
        viewModelScope.launch(Dispatchers.IO) { dao.inserir(relato) }
    }
}
