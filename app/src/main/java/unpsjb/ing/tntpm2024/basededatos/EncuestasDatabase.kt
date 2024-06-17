package unpsjb.ing.tntpm2024.basededatos

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
abstract class EncuestasDatabase : RoomDatabase() {

    abstract val encuestaDAO: EncuestaDAO

    abstract fun alimentoDao(): AlimentoDAO

    abstract fun alimentoEncuestaDao(): AlimentoEncuestaDao

    companion object {
        @Volatile
        private var INSTANCE: EncuestasDatabase? = null

        fun getInstance(context: Context): EncuestasDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    EncuestasDatabase::class.java,
                    "encuestas_db"
                )
                    .addCallback(EncuestasDatabaseCallback(CoroutineScope(Dispatchers.IO)))
                    .fallbackToDestructiveMigration()
                    .build().also {
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
                    populateDatabase(database.alimentoDao())
                }
            }
        }

        suspend fun populateDatabase(alimentoDao: AlimentoDAO) {

            if (alimentoDao.getCantidadAlimentos() == 0) {
                val alimentos = listOf(
                    Alimento(
                        nombre = "Manzana",
                        categoria = "Fruta",
                        medida = "Unidad",
                        porcentajeGraso = 0.2
                    ),
                    Alimento(
                        nombre = "Leche",
                        categoria = "Lácteo",
                        medida = "Litro",
                        porcentajeGraso = 3.5
                    ),
                    Alimento(
                        nombre = "Pan",
                        categoria = "Cereal",
                        medida = "Gramo",
                        porcentajeGraso = 1.0
                    )
                )
                alimentoDao.insertAll(alimentos)
            }
        }

        suspend fun cargarDatabase(encuestaDAO: EncuestaDAO) {
            Log.i("EncuestasDatabase", "cargarDatabase")
            // Descomentar para precargar datos iniciales
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
