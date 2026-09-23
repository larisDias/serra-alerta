package br.edu.ifsp.serraalerta.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RelatoDao {
    @Query("SELECT * FROM relatos ORDER BY criadoEm DESC")
    fun observarTodos(): Flow<List<Relato>>

    @Query("SELECT COUNT(*) FROM relatos")
    suspend fun contar(): Int

    @Insert
    suspend fun inserir(relato: Relato): Long

    @Insert
    suspend fun inserirTodos(relatos: List<Relato>)
}
