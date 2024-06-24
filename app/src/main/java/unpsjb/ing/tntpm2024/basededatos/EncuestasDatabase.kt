package unpsjb.ing.tntpm2024.basededatos

import android.content.Context
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
    version = 2,
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
                    .fallbackToDestructiveMigration()
                    .addCallback(EncuestasDatabaseCallback(CoroutineScope(Dispatchers.IO)))
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
                    populateDatabase(database.alimentoDao())
                    // cargarDatabase(database.encuestaDAO)
                }
            }
        }

        suspend fun populateDatabase(alimentoDao: AlimentoDAO) {
            if (alimentoDao.getCantidadAlimentos() == 0) {
                val alimentos = listOf(
                    Alimento(
                        nombre = "Leche en polvo entera",
                        categoria = "Leche y yogur",
                        medida = "ml",
                        kcal = 494.04,
                        carbohidratos = 38.05,
                        proteinas = 26.15,
                        grasas = 26.36,
                        alcohol = 0.0,
                        coresterol = 80.48,
                        fibra = 0.0,
                    ),
                    Alimento(
                        nombre = "Leche fluida entera",
                        categoria = "Leche y yogur",
                        medida = "ml",
                        kcal = 57.92,
                        carbohidratos = 4.63,
                        proteinas = 3.1,
                        grasas = 3.0,
                        alcohol = 0.0,
                        coresterol = 10.11,
                        fibra = 0.0,
                    ),
                    Alimento(
                        nombre = "De pasta dura (ej. Sardo, Romano, Provolone, Reggianito, Parmesano)",
                        categoria = "Grasas animales",
                        medida = "g",
                        kcal = 373.83,
                        carbohidratos = 0.34,
                        proteinas = 32.39,
                        grasas = 26.99,
                        alcohol = 0.0,
                        coresterol = 82.99,
                        fibra = 0.0,
                    ),
                    Alimento(
                        nombre = "De pasta semidura/azul (ej. Holanda, Gouda, Fontina, Pategras, Dambo)",
                        categoria = "Grasas animales",
                        medida = "g",
                        kcal = 328.16,
                        carbohidratos = 0.1,
                        proteinas = 25.33,
                        grasas = 25.16,
                        alcohol = 0.0,
                        coresterol = 72.14,
                        fibra = 0.0,
                    ),
                    Alimento(
                        nombre = "Manteca",
                        categoria = "Grasas animales",
                        medida = "g",
                        kcal = 745.35,
                        carbohidratos = 0.0,
                        proteinas = 0.33,
                        grasas = 82.67,
                        alcohol = 0.0,
                        coresterol = 223.0,
                        fibra = 0.0,
                    )
                )
                alimentoDao.insertAll(alimentos)
            }
        }
/*
        suspend fun cargarDatabase(encuestaDAO: EncuestaDAO) {
            Log.i("EncuestasDatabase", "cargarDatabase")
            // Uncomment to preload data
            if (encuestaDAO.getCantidadAlimentos() == 0) {
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
        }
*/
    }
}
