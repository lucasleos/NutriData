package unpsjb.ing.tntpm2024.login

import android.content.Intent
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
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var googleSignInClient: GoogleSignInClient

    // 1. Nueva API para reemplazar onActivityResult
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Se muestra el loading apenas vuelve del selector de cuentas de Google
        LoadingDialogFragment.show(parentFragmentManager, "Conectando con Google...")

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w("LoginFragment", "Google sign in failed", e)
            LoadingDialogFragment.hide(parentFragmentManager)
            mostrarMensaje("El inicio de sesión con Google falló")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        configurarBotones()
        return binding.root
    }

    private fun configurarBotones() {
        binding.btnIngresar.setOnClickListener {
            // Obtenemos los textos actuales sincronizados desde el ViewModel
            val email = viewModel.usuario.value?.trim() ?: ""
            val pwd = viewModel.password.value?.trim() ?: ""

            // --- INICIO DEL BYPASS (PUERTA TRASERA DE DESARROLLO) ---
            if (email == "admin" && pwd == "admin") {
                mostrarMensaje("Modo Desarrollador: Ingreso offline")
                navegarAInicio()
                return@setOnClickListener // Fundamental: Cortamos la ejecución aquí para que no llame a Firebase
            }
            // --- FIN DEL BYPASS ---

            // Flujo normal de producción
            if (email.isNotEmpty() && pwd.isNotEmpty()) {
                loginUser(email, pwd)
            } else {
                mostrarMensaje("Ingrese Email y Password")
            }
        }

        binding.btnGoogleSignIn.setOnClickListener {
            LoadingDialogFragment.show(parentFragmentManager, "Iniciando sesión con Google...")
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                LoadingDialogFragment.hide(parentFragmentManager)
                if (task.isSuccessful) {
                    mostrarMensaje("Inicio de sesión Google exitoso")
                    navegarAInicio()
                } else {
                    mostrarMensaje("El inicio de sesión con Google falló")
                }
            }
    }

    private fun loginUser(email: String, password: String) {
        LoadingDialogFragment.show(parentFragmentManager, "Iniciando sesión...")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                LoadingDialogFragment.hide(parentFragmentManager)
                if (task.isSuccessful) {
                    mostrarMensaje("Inicio de sesión exitoso")
                    navegarAInicio()
                } else {
                    mostrarMensaje("Datos ingresados incorrectos")
                }
            }
    }

    private fun navegarAInicio() {
        findNavController().navigate(R.id.action_loginFragment_to_inicioFragment)
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }
}