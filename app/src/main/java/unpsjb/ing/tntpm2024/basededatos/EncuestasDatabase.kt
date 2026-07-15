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
    version = 3,
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
                    val alimentoDao = database.alimentoDao()
                    // Si la tabla de alimentos está vacía, la poblamos (esto ayuda si no se desinstaló la app)
                    if (alimentoDao.getCantidadAlimentos() == 0) {
                        populateDatabase(alimentoDao)
                    }
                }
            }
        }

        // Mantenemos onCreate para la primera vez
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val alimentoDao = database.alimentoDao()
                    val encuestaDao = database.encuestaDAO
                    val alimentoEncuestaDao = database.alimentoEncuestaDao()

                    // 1. Primero cargamos los alimentos
                    populateDatabase(alimentoDao)

                    // 2. Luego cargamos las encuestas y consumos simulados
                    cargarEncuestasDePrueba(encuestaDao, alimentoEncuestaDao)
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
                        colesterol = 80.48,
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
                        colesterol = 10.11,
                        fibra = 0.0,
                    ),
                    Alimento(
                        nombre = "Queso de pasta dura",
                        categoria = "Grasas animales",
                        medida = "g",
                        kcal = 373.83,
                        carbohidratos = 0.34,
                        proteinas = 32.39,
                        grasas = 26.99,
                        alcohol = 0.0,
                        colesterol = 82.99,
                        fibra = 0.0,
                    ),
                    Alimento(
                        nombre = "Queso de pasta semidura/azul",
                        categoria = "Grasas animales",
                        medida = "g",
                        kcal = 328.16,
                        carbohidratos = 0.1,
                        proteinas = 25.33,
                        grasas = 25.16,
                        alcohol = 0.0,
                        colesterol = 72.14,
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
                        colesterol = 223.0,
                        fibra = 0.0,
                    ),
                    Alimento(
                        nombre = "Huevo de Gallina",
                        categoria = "Huevos",
                        medida = "g",
                        kcal = 153.8,
                        carbohidratos = 0.2,
                        proteinas = 12.7,
                        grasas = 11.4,
                        alcohol = 0.0,
                        colesterol = 449.0,
                        fibra = 0.0,
                    ),
                    Alimento(
                        nombre = "Huevo Frito",
                        categoria = "Huevos",
                        medida = "g",
                        kcal = 191.3,
                        carbohidratos = 0.8,
                        proteinas = 13.6,
                        grasas = 14.8,
                        alcohol = 0.0,
                        colesterol = 401.0,
                        fibra = 0.0,
                    ),
                    Alimento(
                        nombre = "Carne Vacuna Magra",
                        categoria = "Huevos",
                        medida = "g",
                        kcal = 130.6,
                        carbohidratos = 0.0,
                        proteinas = 21.4,
                        grasas = 5.0,
                        alcohol = 0.0,
                        colesterol = 62.3,
                        fibra = 0.0,
                    ),
                    Alimento(
                        nombre = "Empanadas de Carne",
                        categoria = "Grasas animales",
                        medida = "g",
                        kcal = 214.4,
                        carbohidratos = 14.8,
                        proteinas = 10.1,
                        grasas = 12.7,
                        alcohol = 0.0,
                        colesterol = 68.9,
                        fibra = 3.0,
                    ),
                    Alimento(
                        nombre = "Banana",
                        categoria = "Frutas",
                        medida = "g",
                        kcal = 98.7,
                        carbohidratos = 22.8,
                        proteinas = 1.1,
                        grasas = 0.3,
                        alcohol = 0.0,
                        colesterol = 0.0,
                        fibra = 2.6,
                    )
                )
                alimentoDao.insertAll(alimentos)
            }
        }

        suspend fun cargarEncuestasDePrueba(encuestaDao: EncuestaDAO, aeDao: AlimentoEncuestaDao) {
            // Creamos una primera encuesta de prueba asignada a nuestro usuario "admin" del bypass
            val idEncuesta1 = encuestaDao.insert(
                Encuesta(
                    fecha = System.currentTimeMillis() - 86400000, // Fecha: Ayer
                    encuestaCompletada = true,
                    zona = "Zona Norte",
                    userId = "admin",
                    userEmail = "admin@test.com"
                )
            )

            // Insertamos los consumos vinculados a esa encuesta generada y a los alimentos (IDs del 1 al 10)
            val consumosEncuesta1 = listOf(
                AlimentoEncuesta(
                    encuestaId = idEncuesta1.toInt(),
                    alimentoId = 10, // Banana
                    porcion = "1 Unidad mediana",
                    frecuencia = "Diariamente",
                    veces = "2"
                ),
                AlimentoEncuesta(
                    encuestaId = idEncuesta1.toInt(),
                    alimentoId = 8, // Carne Vacuna Magra
                    porcion = "200 gramos",
                    frecuencia = "Semanalmente",
                    veces = "3"
                ),
                AlimentoEncuesta(
                    encuestaId = idEncuesta1.toInt(),
                    alimentoId = 5, // Manteca
                    porcion = "1 Cucharada",
                    frecuencia = "Ocasionalmente",
                    veces = "1"
                )
            )
            aeDao.insertAll(consumosEncuesta1)

            // Creamos una segunda encuesta para tener volumen en la lista
            val idEncuesta2 = encuestaDao.insert(
                Encuesta(
                    fecha = System.currentTimeMillis(), // Fecha: Hoy
                    encuestaCompletada = true,
                    zona = "Zona Sur",
                    userId = "admin",
                    userEmail = "admin@test.com"
                )
            )

            val consumosEncuesta2 = listOf(
                AlimentoEncuesta(
                    encuestaId = idEncuesta2.toInt(),
                    alimentoId = 2, // Leche fluida entera
                    porcion = "1 Taza",
                    frecuencia = "Diariamente",
                    veces = "1"
                ),
                AlimentoEncuesta(
                    encuestaId = idEncuesta2.toInt(),
                    alimentoId = 7, // Huevo Frito
                    porcion = "2 Unidades",
                    frecuencia = "Semanalmente",
                    veces = "4"
                )
            )
            aeDao.insertAll(consumosEncuesta2)
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

