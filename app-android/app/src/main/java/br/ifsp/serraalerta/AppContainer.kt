package br.ifsp.serraalerta

import android.content.Context
import br.ifsp.serraalerta.data.local.AppDatabase
import br.ifsp.serraalerta.data.repository.FocoOficialRepositoryImpl
import br.ifsp.serraalerta.data.repository.OcorrenciaRepositoryImpl
import br.ifsp.serraalerta.domain.repository.FocoOficialRepository
import br.ifsp.serraalerta.domain.repository.OcorrenciaRepository
import br.ifsp.serraalerta.sync.FocosOficiaisDataSource

class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)
    val ocorrenciaRepository: OcorrenciaRepository = OcorrenciaRepositoryImpl(database.ocorrenciaDao())
    val focoOficialRepository: FocoOficialRepository = FocoOficialRepositoryImpl(
        database = database,
        dataSource = FocosOficiaisDataSource(context.applicationContext)
    )
}
