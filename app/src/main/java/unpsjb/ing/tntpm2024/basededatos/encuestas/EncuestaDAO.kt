package unpsjb.ing.tntpm2024.basededatos.encuestas

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta

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

    @Query("SELECT * from tabla_encuesta ORDER BY fecha DESC")
    fun getEncuestas(): LiveData<List<Encuesta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(encuesta: Encuesta)

    @Query("INSERT OR REPLACE INTO tabla_encuesta (encuestaId, alimento, porcion, frecuencia, veces, fecha, encuestaCompletada) VALUES (:encuestaId, :alimento, :porcion, :frecuencia, :veces, :fecha, :encuestaCompletada)")
    suspend fun editEncuesta(encuestaId: Int, alimento: String, porcion: String, frecuencia: String, veces: String, fecha: Long, encuestaCompletada: Boolean)

    @Query("DELETE FROM tabla_encuesta")
    suspend fun borrarTodos()

    @Query("SELECT COUNT(encuestaId) from tabla_encuesta")
    suspend fun cantidadDeEncuestas(): Int

    @Query("DELETE FROM tabla_encuesta WHERE encuestaId=:encuestaId")
    suspend fun deleteEncuesta(encuestaId: Int)
}
