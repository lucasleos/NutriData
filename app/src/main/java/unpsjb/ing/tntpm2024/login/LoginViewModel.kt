package unpsjb.ing.tntpm2024.login

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _usuario = MutableLiveData<Editable>()
    val usuario: LiveData<Editable>
        get() = _usuario


    private val _password = MutableLiveData<Editable>()
    val password: LiveData<Editable>
        get() = _password



}