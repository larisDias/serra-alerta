package br.ifsp.serraalerta.data.local

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.ifsp.serraalerta.data.local.dao.FocoOficialDao
import br.ifsp.serraalerta.data.local.dao.OcorrenciaDao
import br.ifsp.serraalerta.data.local.entity.FocoOficialEntity
import br.ifsp.serraalerta.data.local.entity.OcorrenciaEntity

@Database(
    entities = [OcorrenciaEntity::class, FocoOficialEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ocorrenciaDao(): OcorrenciaDao
    abstract fun focoOficialDao(): FocoOficialDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "serra_alerta.db"
            ).build().also { instance = it }
        }
    }
}
