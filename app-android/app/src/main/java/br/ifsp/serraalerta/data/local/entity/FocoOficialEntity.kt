package br.ifsp.serraalerta.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.ifsp.serraalerta.domain.model.FocoOficial

@Entity(tableName = "focos_oficiais")
data class FocoOficialEntity(
    @PrimaryKey val id: String,
    val latitude: Double,
    val longitude: Double,
    @androidx.room.ColumnInfo(name = "data_deteccao") val dataDeteccao: Long,
    val frp: Double?,
    val fonte: String
)

fun FocoOficialEntity.toDomain() = FocoOficial(
    id = id,
    latitude = latitude,
    longitude = longitude,
    dataDeteccao = dataDeteccao,
    frp = frp,
    fonte = fonte
)

fun FocoOficial.toEntity() = FocoOficialEntity(
    id = id,
    latitude = latitude,
    longitude = longitude,
    dataDeteccao = dataDeteccao,
    frp = frp,
    fonte = fonte
)
