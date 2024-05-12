package unpsjb.ing.tntpm2024.login

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)


        binding.btnIngresar.setOnClickListener {
            if ((binding.username.text.toString() == "admin") && (binding.password.text.toString() == "tnt2024")) {
                findNavController().navigate(R.id.action_loginFragment_to_inicioFragment)
                Toast.makeText(activity, "Bienvenido " + binding.username.text, Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(activity, "Usuario o contraseña erronea", Toast.LENGTH_SHORT).show()
            }
        }






        return binding.root
    }
}