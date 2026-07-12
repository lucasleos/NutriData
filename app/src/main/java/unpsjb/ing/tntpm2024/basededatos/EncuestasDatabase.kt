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
            val alimentos = listOf(
                Alimento(nombre = "Leche fluida entera", categoria = "Lácteos", medida = "Taza", kcal = 60.0),
                Alimento(nombre = "Yogur entero frutado", categoria = "Lácteos", medida = "Pote", kcal = 100.0),
                Alimento(nombre = "Queso de pasta blanda", categoria = "Quesos", medida = "Feta", kcal = 280.0),
                Alimento(nombre = "Huevo entero", categoria = "Huevos", medida = "Unidad", kcal = 155.0),
                Alimento(nombre = "Carne vacuna magra", categoria = "Carnes", medida = "Gramo", kcal = 120.0),
                Alimento(nombre = "Pollo sin piel", categoria = "Carnes", medida = "Gramo", kcal = 110.0),
                Alimento(nombre = "Arroz blanco cocido", categoria = "Cereales", medida = "Taza", kcal = 130.0),
                Alimento(nombre = "Pan francés", categoria = "Panificados", medida = "Unidad", kcal = 270.0),
                Alimento(nombre = "Manzana", categoria = "Frutas", medida = "Unidad", kcal = 52.0),
                Alimento(nombre = "Banana", categoria = "Frutas", medida = "Unidad", kcal = 89.0)
            )
            alimentoDao.insertAll(alimentos)
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

