package unpsjb.ing.tntpm2024.basededatos.alimentos

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento

class RepositorioAlimento(private val alimentoDAO: AlimentoDAO) {

    fun getAlimento(searchQuery: String) : Flow<List<Alimento>> {
        return alimentoDAO.getAlimento(searchQuery)
    }

    val allAlimentos: LiveData<List<Alimento>> = alimentoDAO.getAlimentos()

    suspend fun insertar(alimento: Alimento){
        alimentoDAO.insertar(alimento)
    }

}