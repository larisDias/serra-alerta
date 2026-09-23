package br.edu.ifsp.serraalerta.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Classificação preliminar informada por quem reporta. Não substitui a validação oficial. */
enum class TipoFoco(val rotulo: String, val descricao: String, val cor: Long) {
    CONTROLADA("Controlada", "Queima de manejo, aparentemente sob vigilância", 0xFFE8A33D),
    IRREGULAR("Irregular", "Queima de lixo, pasto ou restos sem controle", 0xFFE4572E),
    INCENDIO("Incêndio", "Fogo se espalhando em vegetação nativa", 0xFFA61E1E),
}

enum class Sinal(val rotulo: String) { FUMACA("Fumaça"), FOGO("Fogo") }

@Entity(tableName = "relatos")
data class Relato(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipo: TipoFoco,
    val sinal: Sinal,
    val latitude: Double,
    val longitude: Double,
    val descricao: String,
    val fotoPath: String? = null,
    val criadoEm: Long = System.currentTimeMillis(),
)
