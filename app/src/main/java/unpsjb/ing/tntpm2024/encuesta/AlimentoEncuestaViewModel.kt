package unpsjb.ing.tntpm2024.encuesta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import unpsjb.ing.tntpm2024.basededatos.AlimentoEncuestaRepository
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentoEncuesta

class AlimentoEncuestaViewModel(database: EncuestasDatabase) : ViewModel() {
    private val repository: AlimentoEncuestaRepository

    init {
        val alimentoEncuestaDao = database.alimentoEncuestaDao()
        repository = AlimentoEncuestaRepository(alimentoEncuestaDao)
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
