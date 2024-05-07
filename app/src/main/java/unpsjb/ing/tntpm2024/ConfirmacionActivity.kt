package unpsjb.ing.tntpm2024

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import unpsjb.ing.tntpm2024.databinding.ActivityConfirmacionBinding

class ConfirmacionActivity : AppCompatActivity() {

    private lateinit var binding : ActivityConfirmacionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmacionBinding.inflate(layoutInflater)
        val view = binding.root
        //setContentView(R.layout.activity_confirmacion)
        setContentView(view)

        val valorPorcion:String = intent.getStringExtra("porcion").toString()
        val valorFrecuencia:String = intent.getStringExtra("frecuencia").toString()
        val valorVeces:String = intent.getStringExtra("veces").toString()
        Toast.makeText(this@ConfirmacionActivity, valorPorcion + " " + valorFrecuencia+ " "
            + valorVeces, Toast.LENGTH_SHORT).show()

        binding.tvRespuesta.text = "Usted consume " + valorPorcion + " de yogurt bebile entero, "+
                valorVeces + " veces por " + valorFrecuencia.lowercase()

        binding.btnAceptar.setOnClickListener({
            finish();
        })
    }
}