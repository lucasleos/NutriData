package unpsjb.ing.tntpm2024.basededatos

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta

class Repository(private val encuestaDAO: EncuestaDAO) {

    val allEncuestas: LiveData<List<Encuesta>> = encuestaDAO.getEncuestas()

    fun getEncuesta(searchQuery: String): LiveData<List<Encuesta>> {
        return encuestaDAO.getEncuesta(searchQuery)
    }

    fun eliminarEncuesta(encuesta: Encuesta) {
        encuestaDAO.deleteEncuesta(encuesta)
    }

    suspend fun insertarEncuesta(encuesta: Encuesta) {
        encuestaDAO.insertEncuesta(encuesta)
    }

    fun editarEncuesta(encuesta: Encuesta) {
        encuestaDAO.editEncuesta(encuesta)
    }
}
