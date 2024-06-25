package unpsjb.ing.tntpm2024.basededatos

import androidx.lifecycle.LiveData
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentoEncuesta
import unpsjb.ing.tntpm2024.detalle.AlimentoEncuestaDetalles

class AlimentoEncuestaRepository(private val alimentoEncuestaDao: AlimentoEncuestaDao) {

    val allAlimentoEncuestas: LiveData<List<AlimentoEncuesta>> =
        alimentoEncuestaDao.getAllAlimentoEncuestas()

    suspend fun getAlimentoEncuestaDetalles(): List<AlimentoEncuestaDetalles> {
        return alimentoEncuestaDao.getAlimentoEncuestaDetalles()
    }

    suspend fun insert(alimentoEncuesta: AlimentoEncuesta) {
        alimentoEncuestaDao.insert(alimentoEncuesta)
    }

    suspend fun insertAll(alimentoEncuestas: List<AlimentoEncuesta>) {
        alimentoEncuestaDao.insertAll(alimentoEncuestas)
    }

    suspend fun update(alimentoEncuesta: AlimentoEncuesta) {
        alimentoEncuestaDao.update(alimentoEncuesta)
    }

    suspend fun delete(alimentoEncuesta: AlimentoEncuesta) {
        alimentoEncuestaDao.delete(alimentoEncuesta)
    }
}
