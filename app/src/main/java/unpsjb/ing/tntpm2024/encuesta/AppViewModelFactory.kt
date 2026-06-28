package unpsjb.ing.tntpm2024.encuesta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase

class AppViewModelFactory(private val database: EncuestasDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(EncuestaViewModel::class.java) -> EncuestaViewModel(database) as T
            modelClass.isAssignableFrom(AlimentoViewModel::class.java) -> AlimentoViewModel(database) as T
            modelClass.isAssignableFrom(AlimentoEncuestaViewModel::class.java) -> AlimentoEncuestaViewModel(database) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}