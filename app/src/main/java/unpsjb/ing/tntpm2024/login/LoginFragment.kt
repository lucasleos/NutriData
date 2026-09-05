package unpsjb.ing.tntpm2024.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.databinding.FragmentLoginBinding
import unpsjb.ing.tntpm2024.util.LoadingDialogFragment

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (idToken.isNullOrEmpty()) {
                LoadingDialogFragment.hide(parentFragmentManager)
                habilitarBotones()
                mostrarMensaje("No se pudo obtener el token de Google")
                return@registerForActivityResult
            }

            firebaseAuthWithGoogle(idToken)

        } catch (e: ApiException) {
            Log.w("LoginFragment", "Google sign in failed", e)

            LoadingDialogFragment.hide(parentFragmentManager)
            habilitarBotones()
            mostrarMensaje("El inicio de sesión con Google falló")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )

        binding.loginViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        configurarGoogleSignIn()
        configurarBotones()

        return binding.root
    }

    private fun configurarGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(
                getString(R.string.default_web_client_id)
            )
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(
            requireActivity(),
            gso
        )
    }

    private fun configurarBotones() {
        binding.btnIngresar.setOnClickListener {
            val email = viewModel.usuario.value?.trim() ?: ""
            val password = viewModel.password.value?.trim() ?: ""

            // Bypass de desarrollo
            if (email == "admin" && password == "admin") {
                mostrarMensaje("Modo Desarrollador: Ingreso offline")
                navegarAInicio()
                return@setOnClickListener
            }

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                mostrarMensaje("Ingrese Email y Password")
            }
        }

        binding.btnGoogleSignIn.setOnClickListener {
            iniciarSesionConGoogle()
        }
    }

    private fun iniciarSesionConGoogle() {
        LoadingDialogFragment.show(
            parentFragmentManager,
            "Iniciando sesión con Google..."
        )

        deshabilitarBotones()

        googleSignInClient.signOut()
            .addOnCompleteListener {
                if (!isAdded) return@addOnCompleteListener

                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                LoadingDialogFragment.hide(parentFragmentManager)
                habilitarBotones()

                if (task.isSuccessful) {
                    mostrarMensaje("Inicio de sesión Google exitoso")
                    navegarAInicio()
                } else {
                    mostrarMensaje("El inicio de sesión con Google falló")
                }
            }
    }

    private fun loginUser(email: String, password: String) {
        LoadingDialogFragment.show(
            parentFragmentManager,
            "Iniciando sesión..."
        )

        deshabilitarBotones()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                LoadingDialogFragment.hide(parentFragmentManager)
                habilitarBotones()

                if (task.isSuccessful) {
                    mostrarMensaje("Inicio de sesión exitoso")
                    navegarAInicio()
                } else {
                    mostrarMensaje("Datos ingresados incorrectos")
                }
            }
    }

    private fun deshabilitarBotones() {
        binding.btnIngresar.isEnabled = false
        binding.btnGoogleSignIn.isEnabled = false
    }

    private fun habilitarBotones() {
        if (!isAdded) return

        binding.btnIngresar.isEnabled = true
        binding.btnGoogleSignIn.isEnabled = true
    }

    private fun navegarAInicio() {
        if (!isAdded) return

        findNavController().navigate(
            R.id.action_loginFragment_to_inicioFragment
        )
    }

    private fun mostrarMensaje(mensaje: String) {
        if (!isAdded) return

        Toast.makeText(
            requireContext(),
            mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        LoadingDialogFragment.hide(parentFragmentManager)
        super.onDestroyView()
    }
}
