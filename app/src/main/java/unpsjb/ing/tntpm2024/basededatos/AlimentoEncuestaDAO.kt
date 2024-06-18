package unpsjb.ing.tntpm2024.basededatos

import androidx.lifecycle.LiveData
import androidx.room.*
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentoEncuesta

@Dao
interface AlimentoEncuestaDao {
    @Query("SELECT * FROM tabla_alimento_encuesta")
    fun getAllAlimentoEncuestas(): LiveData<List<AlimentoEncuesta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alimentoEncuesta: AlimentoEncuesta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alimentoEncuestas: List<AlimentoEncuesta>)

    @Update
    suspend fun update(alimentoEncuesta: AlimentoEncuesta)

    @Delete
    suspend fun delete(alimentoEncuesta: AlimentoEncuesta)
}
