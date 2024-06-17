package unpsjb.ing.tntpm2024.basededatos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento

@Dao
interface AlimentoDAO {

    @Query("SELECT * FROM tabla_alimento")
    fun getAllAlimentos(): LiveData<List<Alimento>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alimento: Alimento)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alimentos: List<Alimento>)

    @Update
    suspend fun update(alimento: Alimento)

    @Delete
    suspend fun delete(alimento: Alimento)

    @Query("SELECT COUNT(*) FROM tabla_alimento")
    suspend fun getCantidadAlimentos(): Int
}