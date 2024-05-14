package unpsjb.ing.tntpm2024.encuesta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EncuestaViewModel : ViewModel() {

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