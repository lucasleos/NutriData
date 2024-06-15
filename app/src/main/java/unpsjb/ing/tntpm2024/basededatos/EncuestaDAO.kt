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

//TRANSACCIONES DE ENCUESTAS

    @Query("SELECT * from tabla_encuesta")
    fun getEncuestas(): LiveData<List<Encuesta>>

    @Query("SELECT * FROM tabla_encuesta encuestas " +
            "INNER JOIN tabla_alimento_encuesta ae ON encuestas.encuestaId = ae.encuestaId " +
            "INNER JOIN tabla_alimento alimentos ON alimentos.alimentoId = ae.alimentoId " +
            "WHERE alimentos.nombre LIKE :searchQuery")
    fun getEncuesta(searchQuery: String): LiveData<List<Encuesta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncuesta(encuesta: Encuesta)

    @Delete
    fun deleteEncuesta(encuesta: Encuesta)

    @Update
    fun editEncuesta(encuesta: Encuesta)

}