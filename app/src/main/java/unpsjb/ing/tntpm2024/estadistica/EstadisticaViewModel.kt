package unpsjb.ing.tntpm2024.estadistica

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import unpsjb.ing.tntpm2024.AndroidApp
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.Repository

class EstadisticaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository
    private val database: EncuestasDatabase = (application as AndroidApp).database

    init {
        val dao = database.encuestaDAO
        repository = Repository(dao)
    }

}
