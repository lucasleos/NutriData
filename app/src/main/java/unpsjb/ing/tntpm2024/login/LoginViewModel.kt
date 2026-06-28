package unpsjb.ing.tntpm2024.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    // Usamos String en lugar de Editable. DataBinding se encargará de la conversión.
    val usuario = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

}