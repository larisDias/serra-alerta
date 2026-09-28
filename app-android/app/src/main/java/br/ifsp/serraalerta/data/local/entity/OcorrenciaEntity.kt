package br.ifsp.serraalerta.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.ifsp.serraalerta.domain.model.CategoriaOcorrencia
import br.ifsp.serraalerta.domain.model.Ocorrencia
import br.ifsp.serraalerta.domain.model.StatusEnvio

@Entity(tableName = "ocorrencias")
data class OcorrenciaEntity(
    @PrimaryKey val id: String,
    val latitude: Double,
    val longitude: Double,
    val categoria: String,
    val descricao: String?,
    // Até 3 caminhos separados por quebra de linha; a coluna antiga guardava um só.
    @ColumnInfo(name = "caminho_foto") val caminhoFoto: String,
    @ColumnInfo(name = "data_hora_registro") val dataHoraRegistro: Long,
    @ColumnInfo(name = "status_envio") val statusEnvio: String,
    @ColumnInfo(name = "precisao_metros") val precisaoMetros: Float? = null,
    @ColumnInfo(name = "ajustada_manualmente", defaultValue = "0") val ajustadaManualmente: Boolean = false
)

fun OcorrenciaEntity.toDomain() = Ocorrencia(
    id = id,
    latitude = latitude,
    longitude = longitude,
    categoria = CategoriaOcorrencia.fromValor(categoria),
    descricao = descricao,
    fotos = caminhoFoto.split('\n').filter { it.isNotBlank() },
    dataHoraRegistro = dataHoraRegistro,
    statusEnvio = StatusEnvio.fromValor(statusEnvio),
    precisaoMetros = precisaoMetros,
    ajustadaManualmente = ajustadaManualmente
)

fun Ocorrencia.toEntity() = OcorrenciaEntity(
    id = id,
    latitude = latitude,
    longitude = longitude,
    categoria = categoria.valor,
    descricao = descricao,
    caminhoFoto = fotos.joinToString("\n"),
    dataHoraRegistro = dataHoraRegistro,
    statusEnvio = statusEnvio.valor,
    precisaoMetros = precisaoMetros,
    ajustadaManualmente = ajustadaManualmente
)
