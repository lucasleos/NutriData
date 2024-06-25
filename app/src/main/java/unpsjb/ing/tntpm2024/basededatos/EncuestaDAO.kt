package unpsjb.ing.tntpm2024.basededatos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.detalle.AlimentoEncuestaDetalles

@Dao
interface EncuestaDAO {

//TRANSACCIONES DE ENCUESTAS

//    @Query("SELECT * from tabla_encuesta")
    @Query("SELECT * from tabla_encuesta ORDER BY fecha DESC")
    fun getEncuestas(): LiveData<List<Encuesta>>

    @Query("SELECT * from tabla_encuesta e WHERE e.encuestaId == :id")
    fun getEncuestasById(id: Int): LiveData<Encuesta>

    @Query(
        "SELECT * FROM tabla_encuesta encuestas " +
                "INNER JOIN tabla_alimento_encuesta ae ON encuestas.encuestaId = ae.encuestaId " +
                "INNER JOIN tabla_alimento alimentos ON alimentos.alimentoId = ae.alimentoId " +
                "WHERE alimentos.nombre LIKE :searchQuery"
    )
    fun getEncuesta(searchQuery: String): LiveData<List<Encuesta>>

    @Query("SELECT * FROM tabla_encuesta WHERE userId = :userId ORDER BY fecha DESC")
    fun getEncuestasByUserId(userId: String): LiveData<List<Encuesta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncuesta(encuesta: Encuesta)

    @Insert
    suspend fun insert(encuesta: Encuesta): Long

    @Delete
    fun deleteEncuesta(encuesta: Encuesta)

    @Update
    fun editEncuesta(encuesta: Encuesta)

//    @Query("SELECT * FROM tabla_alimento_encuesta WHERE encuestaId = :encuestaId")
//    fun getAlimentosByEncuestaId(encuestaId: Int): LiveData<List<AlimentoEncuesta>>

    @Transaction
    @Query(
        """
    SELECT tabla_alimento_encuesta.encuestaId, tabla_alimento.alimentoId, tabla_alimento.nombre, 
            tabla_alimento.categoria, tabla_alimento.medida, tabla_alimento.kcal_totales,
            tabla_alimento.carbohidratos, tabla_alimento.proteinas, tabla_alimento.grasas, 
            tabla_alimento.alcohol, tabla_alimento.coresterol, tabla_alimento.fibra, 
            tabla_alimento_encuesta.porcion, tabla_alimento_encuesta.frecuencia, 
            tabla_alimento_encuesta.veces
    FROM tabla_alimento_encuesta
    INNER JOIN tabla_alimento ON tabla_alimento.alimentoId = tabla_alimento_encuesta.alimentoId
    WHERE tabla_alimento_encuesta.encuestaId = :encuestaId
    """
    )
    fun getAlimentosByEncuestaId(encuestaId: Int): LiveData<List<AlimentoEncuestaDetalles>>


}