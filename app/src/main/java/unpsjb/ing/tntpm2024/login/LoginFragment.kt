package unpsjb.ing.tntpm2024.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import unpsjb.ing.tntpm2024.MainActivity
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        auth = FirebaseAuth.getInstance()

        // Configuración de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Usa tu ID de cliente web de Firebase
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Listener para el botón de inicio de sesión con email y contraseña
        binding.btnIngresar.setOnClickListener {
            if (!binding.username.text.toString().isEmpty() && !binding.password.text.toString().isEmpty()) {
                loginUser(binding.username.text.toString(), binding.password.text.toString())
            } else {
                Toast.makeText(context, "Ingrese Email y Password", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener para el botón de inicio de sesión con Google
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        return binding.root
    }

    private fun signInWithGoogle() {
        // Invalida cualquier sesión anterior para forzar la selección de cuenta
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Resultado devuelto al iniciar la intención desde GoogleSignInApi.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Inicio de sesión de Google fue exitoso, autenticar con Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("LoginFragment", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // El inicio de sesión de Google falló
                Log.w("LoginFragment", "Google sign in failed", e)
                Toast.makeText(context, "Inicio de sesión Google fallo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión fue exitoso
//                    val user = auth.currentUser

                    val menu = (requireActivity() as MainActivity).navView.menu
                    (requireActivity() as MainActivity).updateDrawerMenu(menu, FirebaseAuth.getInstance().currentUser)

                    Log.d("LoginFragment", "signInWithCredential:success")
                    Toast.makeText(context, "Inicio de sesión Google exitoso", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_inicioFragment)
                } else {
                    // Si el inicio de sesión falla, muestra un mensaje al usuario
                    Log.w("LoginFragment", "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Inicio de sesión Google fallo", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val menu = (requireActivity() as MainActivity).navView.menu
                    (requireActivity() as MainActivity).updateDrawerMenu(menu, FirebaseAuth.getInstance().currentUser)

                    Log.d("Login", "Inicio de sesión exitoso: ${user?.email}")
                    Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_inicioFragment)
                } else {
                    Toast.makeText(context, "Datos ingresados incorrectos", Toast.LENGTH_SHORT).show()
                    Log.e("Login", "Error: ${task.exception?.message}")
                }
            }
    }
}

/*

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        binding.loginViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.username.text = viewModel.usuario.value
        binding.password.text = viewModel.password.value

        binding.btnIngresar.setOnClickListener {
            if(!binding.username.text.toString().isEmpty() && !binding.password.text.toString().isEmpty()){
                loginUser(binding.username.text.toString(),binding.password.text.toString())
            }else{
                Toast.makeText(context, "Ingrese Email y Password", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root

    }


    fun loginUser(email: String, password: String) {
//        findNavController().navigate(R.id.action_loginFragment_to_inicioFragment)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // El inicio de sesión fue exitoso
                    val user = auth.currentUser
                    Log.d("Login", "Inicio de sesión exitoso: ${user?.email}")
                    Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    val userId = auth.currentUser?.uid
                    val menu = (requireActivity() as MainActivity).navView.menu
                    (requireActivity() as MainActivity).updateDrawerMenu(menu, FirebaseAuth.getInstance().currentUser)
                    if (userId != null) {
                        // Obtenemos los datos del usuario desde Realtime Database
                        getUserData(user)
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_inicioFragment)
                } else {
                    // El inicio de sesión falló
                    Toast.makeText(context, "Datos ingresados incorrectos", Toast.LENGTH_SHORT).show()
                    Log.e("Login", "Error: ${task.exception?.message}")
                }
            }
    }

    // obtener datos del usuario desde Realtime Database
    fun getUserData(user: FirebaseUser?) {
        val database = FirebaseDatabase.getInstance().reference
        val userRef = user?.let { database.child("users").child(it.uid) }

        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Aquí obtienes los datos del usuario
                    val userName = snapshot.child("name").getValue(String::class.java)
                    val userEmail = snapshot.child("email").getValue(String::class.java)

                    // Puedes hacer algo con los datos del usuario
                    Log.d("Login", "Name: $userName, Email: $userEmail")
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejo de errores
                    Log.e("Login", "Error: ${error.message}")
                }
            })
        }
    }

    /*
    // metodo para registrar un usuario
    fun registerUser(email: String, password: String) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // El registro fue exitoso
                val user = auth.currentUser
                Log.d("Register", "Registro exitoso: ${user?.email}")
            } else {
                // El registro falló
                Log.e("Register", "Error: ${task.exception?.message}")
            }
        }
}
     */



}*/