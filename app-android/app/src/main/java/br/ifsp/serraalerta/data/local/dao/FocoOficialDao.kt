package br.ifsp.serraalerta.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.ifsp.serraalerta.data.local.entity.FocoOficialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocoOficialDao {
    @Query("SELECT * FROM focos_oficiais ORDER BY data_deteccao DESC")
    fun observarTodos(): Flow<List<FocoOficialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(focos: List<FocoOficialEntity>)

    @Query("DELETE FROM focos_oficiais")
    suspend fun limparTodos()
}
