package unpsjb.ing.tntpm2024.basededatos

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.AutoMigration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentoEncuesta
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta

@Database(
    version = 1,
    entities = [
        Encuesta::class,
        Alimento::class,
        AlimentoEncuesta::class
    ],
    exportSchema = true
)
abstract class EncuestasDatabase: RoomDatabase() {

    abstract val encuestaDAO: EncuestaDAO

    companion object {
        @Volatile
        private var INSTANCE: EncuestasDatabase? = null

        fun getInstance(context: Context): EncuestasDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    EncuestasDatabase::class.java,
                    "encuestas_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }

    private class EncuestasDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    cargarDatabase(database.encuestaDAO)
                }
            }
        }

        suspend fun cargarDatabase(encuestaDAO: EncuestaDAO) {
            Log.i("EncuestasDatabase", "cargarDatabase")
/*
            if(encuestaDAO.getCantidadAlimentos() == 0) {
                encuestaDAO.insertAlimento(
                    Alimento(
                        1,
                        "Yogur bebible",
                        "liquido",
                        "ml",
                        0.8
                    )
                )
            }

 */
        }

    }

}