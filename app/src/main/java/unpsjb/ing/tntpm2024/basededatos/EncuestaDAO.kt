package unpsjb.ing.tntpm2024.basededatos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface  EncuestaDAO {
    // LiveData como ya hemos visto anteriormente va a contener nuestros
    // datos y permitir que estos sean observados dentro de un ciclo de vida dado.
    // Siempre guarda / almacena en caché la última versión de los datos.
    // Notifica a sus observadores activos cuando los datos han cambiado.
    // Dado que estamos obteniendo el contenido completo de la base de datos,
    // se nos notifica cada vez que algun dato haya cambiado.
    @Query("SELECT * from tabla_encuesta WHERE alimento LIKE :searchQuery")
    fun getEncuesta(searchQuery: String): Flow<List<Encuesta>>

    @Query("SELECT * from tabla_encuesta ORDER BY encuestaId ASC")
    fun getEncuestas(): LiveData<List<Encuesta>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(encuesta: Encuesta)

    @Query("DELETE FROM tabla_encuesta")
    suspend fun borrarTodos()

    @Query("SELECT COUNT(encuestaId) from tabla_encuesta")
    suspend fun cantidadDeEncuestas(): Int
}
