package unpsjb.ing.tntpm2024.basededatos.alimentos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento

@Dao
interface AlimentoDAO {

    @Query("SELECT * from tabla_alimento WHERE nombre LIKE :searchQuery")
    fun getAlimento(searchQuery: String): Flow<List<Alimento>>

    @Query("SELECT * from tabla_alimento ORDER BY alimentoId DESC")
    fun getAlimentos(): LiveData<List<Alimento>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(alimento: Alimento)

    @Query("DELETE FROM tabla_alimento")
    suspend fun borrarTodos()

    @Query("SELECT COUNT(alimentoId) from tabla_alimento")
    suspend fun cantidadDeAlimentos(): Int

}