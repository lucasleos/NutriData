package unpsjb.ing.tntpm2024.basededatos.encuestas

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import unpsjb.ing.tntpm2024.util.CalendarConverters

// Anotar la clase para convertirla en una Database Room
// con una Tabla (entity) de la clase Partido

@TypeConverters(CalendarConverters::class)
@Database(entities = [Encuesta::class], version = 1, exportSchema = false)
public abstract class EncuestaRoomDatabase : RoomDatabase() {

    // ctrl + p para buscar archivos
    abstract fun encuestaDao(): EncuestaDAO

    companion object {
        // Singleton previene multiples instancias de
        // la base de datos abriendose al mismo tiempo
        @Volatile
        var INSTANCIA: EncuestaRoomDatabase? = null

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
                } // fin comprobacion cantidad

            }
        }

    }
}