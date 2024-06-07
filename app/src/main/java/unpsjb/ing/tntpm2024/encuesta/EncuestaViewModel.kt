package unpsjb.ing.tntpm2024.encuesta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.basededatos.encuestas.RepositorioDeEncuestas

class EncuestaViewModel(private val database: EncuestasDatabase) : ViewModel() {

    private val repository: RepositorioDeEncuestas
    val todasLasEncuestas: LiveData<List<Encuesta>>

    init {

        val encuestasDao = database.encuestaDAO

        repository = RepositorioDeEncuestas(encuestasDao)
        todasLasEncuestas = repository.todasLasEncuestas

    }

    fun insert(encuesta: Encuesta) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertar(encuesta)
    }

    fun getEncuesta(searchQuery: String): LiveData<List<Encuesta>> {
        return repository.getEncuesta(searchQuery).asLiveData()
    }

    fun deleteEncuesta(encuestaId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteEncuesta(encuestaId)
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

class EncuestaViewModelFactory(private val database: EncuestasDatabase) : ViewModelProvider.Factory {
    fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EncuestaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EncuestaViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}