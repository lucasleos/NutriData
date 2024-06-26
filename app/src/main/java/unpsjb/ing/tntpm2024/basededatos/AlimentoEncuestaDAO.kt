package unpsjb.ing.tntpm2024.basededatos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentoEncuesta
import unpsjb.ing.tntpm2024.detalle.AlimentoEncuestaDetalles

@Dao
interface AlimentoEncuestaDao {
    @Query("SELECT * FROM tabla_alimento_encuesta")

    fun getAllAlimentoEncuestas(): LiveData<List<AlimentoEncuesta>>

    @Query(
        """
    SELECT 
        ae.encuestaId, 
        ae.alimentoId, 
        ae.porcion, 
        ae.frecuencia, 
        ae.veces, 
        a.nombre, 
        a.categoria, 
        a.medida, 
        a.kcal_totales, 
        a.carbohidratos, 
        a.proteinas, 
        a.grasas, 
        a.alcohol, 
        a.coresterol, 
        a.fibra 
    FROM tabla_alimento_encuesta ae
    INNER JOIN tabla_alimento a ON ae.alimentoId = a.alimentoId
    WHERE 
        (ae.porcion IS NOT NULL AND ae.porcion <> '') AND
        (ae.frecuencia IS NOT NULL AND ae.frecuencia <> '') AND 
        (ae.veces IS NOT NULL AND ae.veces <> '')
"""
    )
    suspend fun getAlimentoEncuestaDetalles(): List<AlimentoEncuestaDetalles>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alimentoEncuesta: AlimentoEncuesta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alimentoEncuestas: List<AlimentoEncuesta>)

    @Update
    suspend fun update(alimentoEncuesta: AlimentoEncuesta)

    @Delete
    suspend fun delete(alimentoEncuesta: AlimentoEncuesta)
}
