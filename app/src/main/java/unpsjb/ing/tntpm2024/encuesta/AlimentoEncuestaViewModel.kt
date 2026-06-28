package unpsjb.ing.tntpm2024.encuesta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import unpsjb.ing.tntpm2024.basededatos.AlimentoEncuestaRepository
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentoEncuesta
import unpsjb.ing.tntpm2024.detalle.AlimentoEncuestaDetalles

class AlimentoEncuestaViewModel(database: EncuestasDatabase) : ViewModel() {
    private val repository = AlimentoEncuestaRepository(database.alimentoEncuestaDao())

    val allAlimentoEncuestas: LiveData<List<AlimentoEncuesta>> = repository.allAlimentoEncuestas

    private val _alimentoEncuestaDetalles = MutableLiveData<List<AlimentoEncuestaDetalles>>()
    val alimentoEncuestaDetalles: LiveData<List<AlimentoEncuestaDetalles>> get() = _alimentoEncuestaDetalles

    // Nuevo: Estado de la encuesta en curso (sobrevive a rotación)
    private val _indiceAlimentoActual = MutableLiveData(0)
    val indiceAlimentoActual: LiveData<Int> = _indiceAlimentoActual

    init {
        getAlimentoEncuestaDetalles()
    }

    fun avanzarAlSiguienteAlimento() {
        _indiceAlimentoActual.value = (_indiceAlimentoActual.value ?: 0) + 1
    }

    fun setIndiceInicial(indice: Int) {
        _indiceAlimentoActual.value = indice
    }

    private fun getAlimentoEncuestaDetalles() = viewModelScope.launch {
        _alimentoEncuestaDetalles.postValue(repository.getAlimentoEncuestaDetalles())
    }

    fun insert(alimentoEncuesta: AlimentoEncuesta) = viewModelScope.launch {
        repository.insert(alimentoEncuesta)
    }
}