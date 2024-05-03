package unpsjb.ing.tntpm2024

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import unpsjb.ing.tntpm2024.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

       // binding.textView.text = "viewBinding ok"

        binding.btnIngresar.setOnClickListener{
            // && binding.password.text.equals("hola")
            if((binding.username.text.toString() == "admin") && (binding.password.text.toString() == "tnt2024")){
                val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this@LoginActivity, "Bienvenido " + binding.username.text, Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }else{
                Toast.makeText(this@LoginActivity, "Usuario o contraseña erronea", Toast.LENGTH_SHORT).show()
            }
        }
    }



}