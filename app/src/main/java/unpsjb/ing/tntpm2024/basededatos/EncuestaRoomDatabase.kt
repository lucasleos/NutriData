package unpsjb.ing.tntpm2024.basededatos

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Anotar la clase para convertirla en una Database Room
// con una Tabla (entity) de la clase Partido
@Database(entities = arrayOf(Encuesta::class), version = 1, exportSchema = false)
public abstract class EncuestaRoomDatabase : RoomDatabase() {

    // ctrl + p para buscar archivos
    abstract fun encuestaDao(): EncuestaDAO

    companion object {
        // Singleton previene multiples instancias de
        // la base de datos abriendose al mismo tiempo
        @Volatile
        private var INSTANCIA: EncuestaRoomDatabase? = null

        fun obtenerDatabase(
            context: Context,
            viewModelScope: CoroutineScope
        ): EncuestaRoomDatabase {
            val instanciaTemporal = INSTANCIA
            if (instanciaTemporal != null) {
                return instanciaTemporal
            }
            synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    EncuestaRoomDatabase::class.java,
                    "encuesta_database"
                )
                    .addCallback(EncuestaDatabaseCallback(viewModelScope))
                    .build()
                INSTANCIA = instancia
                return instancia
            }
        }

        private class EncuestaDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /* importante: destacar el scope como parametro */

            /**
             * Lo que hacemos en esta clase es sobreescribir el método onOpen
             * para cargar la base de datos.
             *
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCIA?.let { database ->
                    scope.launch {
                        cargarBaseDeDatos(database.encuestaDao())
                    }
                }
            }

            suspend fun cargarBaseDeDatos(encuestaDAO: EncuestaDAO) {

                if(encuestaDAO.cantidadDeEncuestas() == 0) {

                    Log.i("EncuestaRoomDatabase", "cargarBaseDeDatos")
                    // Borrar el contenido de la base
                    encuestaDAO.borrarTodos()

                    // Agregar partidos de ejemplo
                    var encuesta = Encuesta(
                        1,
                        "yogur",
                        "100ml",
                        "semana",
                        "2 veces",
                        false
                    )
                    encuestaDAO.insertar(encuesta)

                    encuestaDAO.insertar(
                        Encuesta(
                            2,
                            "agua",
                            "200ml",
                            "dia",
                            "2 veces",
                            true
                        )
                    )

                    encuestaDAO.insertar(
                        Encuesta(
                            encuestaId = 3,
                            alimento = "leche",
                            porcion = "300ml",
                            frecuencia = "dia",
                            veces = "3 veces",
                            true
                        )
                    )
                } // fin comprobacion cantidad

            }
        }

    }
}