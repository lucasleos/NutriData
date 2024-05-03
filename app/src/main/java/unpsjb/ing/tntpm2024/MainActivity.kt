package unpsjb.ing.tntpm2024

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import unpsjb.ing.tntpm2024.databinding.ActivityLoginBinding
import unpsjb.ing.tntpm2024.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnRealizarEncuesta.setOnClickListener{
            val intent = Intent(this, EncuestaActivity::class.java)
            startActivity(intent)
        }

        binding.btnListaEncuestas.setOnClickListener{
            val intent = Intent(this, ListaEncuestasActivity::class.java)
            startActivity(intent)
        }
    }
}