package br.ifsp.serraalerta.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.ifsp.serraalerta.data.local.entity.OcorrenciaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OcorrenciaDao {
    @Query("SELECT * FROM ocorrencias ORDER BY data_hora_registro DESC")
    fun observarTodas(): Flow<List<OcorrenciaEntity>>

    @Query("SELECT * FROM ocorrencias WHERE id = :id LIMIT 1")
    fun observarPorId(id: String): Flow<OcorrenciaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(ocorrencia: OcorrenciaEntity)

    @Update
    suspend fun atualizar(ocorrencia: OcorrenciaEntity)

    @Query("UPDATE ocorrencias SET status_envio = :status WHERE id = :id")
    suspend fun atualizarStatus(id: String, status: String)

    @Query("DELETE FROM ocorrencias WHERE id = :id")
    suspend fun excluir(id: String)

    @Query("DELETE FROM ocorrencias")
    suspend fun limparTodas()
}
