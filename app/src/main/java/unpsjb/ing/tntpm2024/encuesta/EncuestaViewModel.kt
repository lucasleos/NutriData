package unpsjb.ing.tntpm2024.encuesta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.Repository
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta

class EncuestaViewModel(database: EncuestasDatabase) : ViewModel() {

    private val repository: Repository
    val todasLasEncuestas: LiveData<List<Encuesta>>

    init {
        val dao = database.encuestaDAO
        repository = Repository(dao)
        todasLasEncuestas = repository.allEncuestas
    }

    fun insert(encuesta: Encuesta) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertarEncuesta(encuesta)
    }

    fun getEncuesta(searchQuery: String): LiveData<List<Encuesta>> {
        return repository.getEncuesta(searchQuery)

    }
    fun deleteEncuesta(encuesta: Encuesta) = viewModelScope.launch(Dispatchers.IO) {
        repository.eliminarEncuesta(encuesta)
    }

    fun editEncuesta(encuesta: Encuesta) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.editarEncuesta(encuesta)
        }
    }

    private var _fecha = MutableLiveData<Long>()
    val fecha: LiveData<Long>
        get() = _fecha

    private var _encuestaCompletada = MutableLiveData<Boolean>(false)
    val encuestaCompletada: LiveData<Boolean>
        get() = _encuestaCompletada

    fun encuestaCompletada() {
        _encuestaCompletada.value = true
    }

    private val _zona = MutableLiveData<String>()
    val zona: LiveData<String> get() = _zona
    fun setZona(value: String) {
        _zona.value = value

    }

    private val _id = MutableLiveData<Int>()
    val id: LiveData<Int> get() = _id

    fun setId(value: Int) {
        _id.value = value
    }
}

class EncuestaViewModelFactory(private val database: EncuestasDatabase) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(EncuestaViewModel::class.java) -> {
                EncuestaViewModel(database) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
