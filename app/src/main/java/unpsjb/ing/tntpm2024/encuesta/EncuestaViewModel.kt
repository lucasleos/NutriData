package unpsjb.ing.tntpm2024.encuesta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.Repository
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.detalle.AlimentoEncuestaDetalles

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

    fun cargarEncuesta(encuesta: Encuesta, callback: (Long) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.cargarEncuesta(encuesta)
            viewModelScope.launch(Dispatchers.Main) {
                callback(id)
            }
        }

    //    fun uploadEncuesta(encuesta: Encuesta, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    fun uploadEncuesta(
        encuesta: Encuesta,
        onSuccess: () -> Unit,
        onFailure: (DatabaseError) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        repository.uploadEncuestaToFirebase(encuesta, onSuccess, onFailure)
    }

    fun deleteEncuestaFromFirebase(
        encuesta: Encuesta,
        onSuccess: () -> Unit,
        onFailure: (DatabaseError) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteEncuestaFromFirebase(encuesta, onSuccess, onFailure)
    }

    fun getEncuesta(searchQuery: String): LiveData<List<Encuesta>> {
        return repository.getEncuesta(searchQuery)
    }

    fun getAlimentosByEncuestaId(encuestaId: Int): LiveData<List<AlimentoEncuestaDetalles>> {
        return repository.getAlimentosByEncuestaId(encuestaId)
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