package unpsjb.ing.tntpm2024.encuesta

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import unpsjb.ing.tntpm2024.basededatos.AlimentoRepository
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento

class AlimentoViewModel(database: EncuestasDatabase) : ViewModel() {
    private val repository: AlimentoRepository
    val allAlimentos: LiveData<List<Alimento>>

    init {
        val alimentoDao = database.alimentoDao()
        repository = AlimentoRepository(alimentoDao)
        allAlimentos = repository.allAlimentos
    }

    fun insert(alimento: Alimento) = viewModelScope.launch {
        repository.insert(alimento)
    }

}

class AlimentoViewModelFactory(private val database: EncuestasDatabase) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(AlimentoViewModel::class.java) -> {
                AlimentoViewModel(database) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
