package unpsjb.ing.tntpm2024.detalle

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import unpsjb.ing.tntpm2024.basededatos.Repository

class DetailViewModel(private val repository: Repository, val encuestaId: Int) : ViewModel() {

    val alimentos: LiveData<List<AlimentoEncuestaDetalles>> = repository.getAlimentosByEncuestaId(encuestaId)
}

class DetailViewModelFactory(private val repository: Repository, private val encuestaId: Int) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(repository, encuestaId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
