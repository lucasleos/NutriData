package unpsjb.ing.tntpm2024.estadistica

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import unpsjb.ing.tntpm2024.basededatos.encuestas.EncuestaRoomDatabase
import unpsjb.ing.tntpm2024.basededatos.encuestas.RepositorioDeEncuestas

class EstadisticaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RepositorioDeEncuestas

    init {
        val dao = EncuestaRoomDatabase
            .obtenerDatabase(application, viewModelScope).encuestaDao()

        repository = RepositorioDeEncuestas(dao)
    }

}