package unpsjb.ing.tntpm2024.login

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
           // if(!binding.username.text.toString().isEmpty() && !binding.password.text.toString().isEmpty()){
                loginUser(binding.username.text.toString(),binding.password.text.toString())
           // }else{
            //    Toast.makeText(context, "Ingrese Email y Password", Toast.LENGTH_SHORT).show()
           // }


        }


        return binding.root

    }


    fun loginUser(email: String, password: String) {
        findNavController().navigate(R.id.action_loginFragment_to_inicioFragment)
        /*auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // El inicio de sesión fue exitoso
                    val user = auth.currentUser
                    Log.d("Login", "Inicio de sesión exitoso: ${user?.email}")
                    Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Obtenemos los datos del usuario desde Realtime Database
                        getUserData(userId)
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_inicioFragment)
                } else {
                    // El inicio de sesión falló
                    Toast.makeText(context, "Datos ingresados incorrectos", Toast.LENGTH_SHORT).show()
                    Log.e("Login", "Error: ${task.exception?.message}")
                }
            }*/
    }

    // obtener datos del usuario desde Realtime Database
    fun getUserData(userId: String) {
        val database = FirebaseDatabase.getInstance().reference
        val userRef = database.child("users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Aquí obtienes los datos del usuario
                val userName = snapshot.child("name").getValue(String::class.java)
                val userEmail = snapshot.child("email").getValue(String::class.java)

                // Puedes hacer algo con los datos del usuario
                Log.d("UserData", "Name: $userName, Email: $userEmail")
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
                Log.e("UserData", "Error: ${error.message}")
            }
        })
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



}