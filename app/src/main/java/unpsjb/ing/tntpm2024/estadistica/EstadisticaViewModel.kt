package unpsjb.ing.tntpm2024.estadistica

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import unpsjb.ing.tntpm2024.AndroidApp
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.Repository
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModel

class EstadisticaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository
    private val database: EncuestasDatabase

    init {

        database = (application as AndroidApp).database

        val dao = database.encuestaDAO
        repository = Repository(dao)

    }

}
