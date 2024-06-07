package unpsjb.ing.tntpm2024.basededatos

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentosEnEncuestas
import unpsjb.ing.tntpm2024.basededatos.entidades.EncuestasConAlimentos

@Dao
interface EncuestaDAO {

    //TRANSACCIONES DE ALIMENTOS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlimento(alimento: Alimento)

    @Transaction
    @Query("SELECT COUNT(alimentoId) from tabla_alimento")
    fun getCantidadAlimentos(): Int

    //TRANSACCIONES DE ENCUESTAS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncuesta(encuesta: Encuesta)

    @Transaction
    @Query("DELETE FROM tabla_encuesta WHERE encuestaId LIKE :encuestaId")
    fun deleteEncuesta(encuestaId: Int)

    @Transaction
    @Query("DELETE FROM tabla_encuesta WHERE encuestaId LIKE :encuestaId")
    fun editEncuesta(encuestaId: Int, fecha: Long, encuestaCompletada: Boolean)

    @Transaction
    @Query("SELECT * from tabla_encuesta")
    fun getEncuestas(): LiveData<List<Encuesta>>

    @Transaction
    @Query("SELECT * from tabla_alimento_encuesta")
    fun getEncuesta(searchQuery: String): Flow<List<Encuesta>>
    //OBTIENE POR ALIMENTO? REVISAR


    //TRANSACCIONES DE ALIMENTOS-ENCUESTAS


    //TRANSACCIONES DE ALIMENTOS-EN-ENCUESTAS
    @Transaction
    @Query("SELECT * from tabla_alimento")
    fun getAlimentosEnEncuestas(): List<AlimentosEnEncuestas>

    //TRANSACCIONES DE ENCUESTAS-CON-ALIMENTOS
    @Transaction
    @Query("SELECT * from tabla_encuesta")
    fun getEncuestasConAlimentos(): List<EncuestasConAlimentos>

    /*
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncuesta(encuesta: Encuesta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlimentoEncuesta(alimentoEncuesta: AlimentoEncuesta)

    @Transaction
    @Query("SELECT * FROM tabla_encuesta")
    fun getEncuestas(): LiveData<List<Encuesta>>

    @Query("SELECT * from tabla_encuesta WHERE nombre LIKE :searchQuery")
    fun getEncuesta(searchQuery: String): Flow<List<Encuesta>>

    @Transaction
    @Query("SELECT COUNT(encuestaId) from tabla_encuesta")
    fun getCantidadEncuestas(): Int

    @Transaction
    @Query("DELETE FROM tabla_encuesta")
    fun borrarEncuestas()

    @Transaction
    @Query("SELECT * FROM tabla_alimento")
    fun getAlimentos(): LiveData<List<Alimento>>

    @Transaction
    @Query("SELECT * FROM tabla_alimento WHERE nombre = :nombre")
    fun getAlimentoByName(nombre: String): Alimento

    @Transaction
    @Query("SELECT COUNT(alimentoId) from tabla_alimento")
    fun getCantidadAlimentos(): Int

    @Transaction
    @Query("SELECT * FROM tabla_encuesta WHERE alimento = :alimento")
    fun getEncuestasByAlimentos(alimento: Alimento): LiveData<List<AlimentoEncuesta>>
*/
}