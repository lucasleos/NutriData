package unpsjb.ing.tntpm2024.basededatos

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta

class Repository(private val encuestaDAO: EncuestaDAO) {

    val allEncuestas: LiveData<List<Encuesta>> = encuestaDAO.getEncuestas()

    val allAlimentos: LiveData<List<Alimento>> = encuestaDAO.getAlimentos()

    suspend fun insertarEncuesta(encuesta: Encuesta) {
        encuestaDAO.insertEncuesta(encuesta)
    }

    fun getEncuesta(searchQuery: String) : Flow<List<Encuesta>> {
        return encuestaDAO.getEncuesta(searchQuery)
    }

    fun getCantidadEncuestas(): Int {
        return encuestaDAO.getCantidadEncuestas()
    }

    fun borrarEncuestas() {
        encuestaDAO.borrarEncuestas()
    }

    suspend fun insertarAlimento(alimento: Alimento) {
        encuestaDAO.insertAlimento(alimento)
    }

    fun getAlimentoByNombre(nombre: String): Alimento {
        return encuestaDAO.getAlimentoByName(nombre)
    }

//    fun getEncuestasByAlimentos(alimentoId: Int): LiveData<List<AlimentoEncuesta>> {
//        return encuestaDAO.getEncuestasByAlimentos(alimentoId)
//    }


    fun getCantidadAlimentos(): Int {
        return encuestaDAO.getCantidadAlimentos()
    }

//    suspend fun insertarAlimentoEncuesta(alimentoEncuesta: AlimentoEncuesta) {
//        encuestaDAO.insertAlimentoEncuesta(alimentoEncuesta)
//    }

}