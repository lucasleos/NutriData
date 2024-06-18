package unpsjb.ing.tntpm2024.basededatos

import androidx.lifecycle.LiveData
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento

class AlimentoRepository(private val alimentoDao: AlimentoDAO) {

    val allAlimentos: LiveData<List<Alimento>> = alimentoDao.getAllAlimentos()

    suspend fun insert(alimento: Alimento) {
        alimentoDao.insert(alimento)
    }

    suspend fun insertAll(alimentos: List<Alimento>) {
        alimentoDao.insertAll(alimentos)
    }

    suspend fun update(alimento: Alimento) {
        alimentoDao.update(alimento)
    }

    suspend fun delete(alimento: Alimento) {
        alimentoDao.delete(alimento)
    }
}