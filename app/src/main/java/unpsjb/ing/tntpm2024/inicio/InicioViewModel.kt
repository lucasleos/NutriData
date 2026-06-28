package unpsjb.ing.tntpm2024.inicio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class InicioViewModel : ViewModel() {

    private val _mensajeBienvenida = MutableLiveData<String>()
    val mensajeBienvenida: LiveData<String> = _mensajeBienvenida

    init {
        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {
        val user = FirebaseAuth.getInstance().currentUser
        val nombre = user?.email?.substringBefore("@") ?: "Usuario"
        _mensajeBienvenida.value = "Bienvenido: $nombre"
    }
}