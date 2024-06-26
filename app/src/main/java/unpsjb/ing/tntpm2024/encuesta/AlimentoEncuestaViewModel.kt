package unpsjb.ing.tntpm2024.encuesta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import unpsjb.ing.tntpm2024.basededatos.AlimentoEncuestaRepository
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentoEncuesta
import unpsjb.ing.tntpm2024.detalle.AlimentoEncuestaDetalles

class AlimentoEncuestaViewModel(database: EncuestasDatabase) : ViewModel() {
    private val repository: AlimentoEncuestaRepository

    val allAlimentoEncuestas: LiveData<List<AlimentoEncuesta>>

    private val _alimentoEncuestaDetalles = MutableLiveData<List<AlimentoEncuestaDetalles>>()
    val alimentoEncuestaDetalles: LiveData<List<AlimentoEncuestaDetalles>> get() = _alimentoEncuestaDetalles


    init {
        val alimentoEncuestaDao = database.alimentoEncuestaDao()
        repository = AlimentoEncuestaRepository(alimentoEncuestaDao)
        allAlimentoEncuestas = repository.allAlimentoEncuestas
        getAlimentoEncuestaDetalles()
    }

    fun getAlimentoEncuestaDetalles() {
        viewModelScope.launch {
            val detalles = repository.getAlimentoEncuestaDetalles()
            _alimentoEncuestaDetalles.postValue(detalles)
        }
    }

    fun insert(alimentoEncuesta: AlimentoEncuesta) = viewModelScope.launch {
        repository.insert(alimentoEncuesta)
    }

    fun insertAll(alimentoEncuestas: List<AlimentoEncuesta>) = viewModelScope.launch {
        repository.insertAll(alimentoEncuestas)
    }
}

class AlimentoEncuestaViewModelFactory(private val database: EncuestasDatabase) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(AlimentoEncuestaViewModel::class.java) -> {
                AlimentoEncuestaViewModel(database) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
