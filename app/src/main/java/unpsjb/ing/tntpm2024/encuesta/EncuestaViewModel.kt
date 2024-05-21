package unpsjb.ing.tntpm2024.encuesta

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unpsjb.ing.tntpm2024.basededatos.encuestas.Encuesta
import unpsjb.ing.tntpm2024.basededatos.encuestas.EncuestaRoomDatabase
import unpsjb.ing.tntpm2024.basededatos.encuestas.RepositorioDeEncuestas

class EncuestaViewModel(application: Application) : AndroidViewModel(application) {


    private val repository: RepositorioDeEncuestas
    val todasLasEncuestas: LiveData<List<Encuesta>>

    init {
        val encuestasDao = EncuestaRoomDatabase
            .obtenerDatabase(application, viewModelScope).encuestaDao()

        repository = RepositorioDeEncuestas(encuestasDao)
        todasLasEncuestas = repository.todasLasEncuestas
    }
    fun insert(encuesta: Encuesta) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertar(encuesta)
    }

    fun getEncuesta(searchQuery: String) : LiveData<List<Encuesta>>{
        return repository.getEncuesta(searchQuery).asLiveData()
    }



    private var _alimento = MutableLiveData<String>("")
    val alimento: LiveData<String>
        get() = _alimento

    private var _porcion = MutableLiveData<String>("")
    val porcion: LiveData<String>
        get() = _porcion

    private var _frecuencia = MutableLiveData<String>("")
    val frecuencia: LiveData<String>
        get() = _frecuencia

    private var _veces = MutableLiveData<Int>(0)
    val veces: LiveData<Int>
        get() = _veces

    private var _encuestaCompletada = MutableLiveData<Boolean>(false)
    val encuestaCompletada: LiveData<Boolean>
        get() = _encuestaCompletada

    fun encuestaCompletada() {
        _encuestaCompletada.value = true
    }

}