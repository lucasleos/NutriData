package unpsjb.ing.tntpm2024

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import unpsjb.ing.tntpm2024.databinding.ActivityEncuestaBinding

class EncuestaActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEncuestaBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEncuestaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

       // binding.textView.text = "viewBinding ok"

        val spinnerPorcion = findViewById<Spinner>(R.id.spinnerPorcion)
        val valoresPorcion = resources.getStringArray(R.array.opcionesPorcion)
        val adaptadorPorcion = ArrayAdapter(this, android.R.layout.simple_spinner_item, valoresPorcion)
        spinnerPorcion.adapter = adaptadorPorcion

        binding.numberPiker.minValue = 0
        binding.numberPiker.maxValue = 10

        val spinnerFrecuencia = findViewById<Spinner>(R.id.spinnerFrecuencia)
        val valoresFrecuencia = resources.getStringArray(R.array.opcionesFrecuencia)
        val adaptadorFrecuencia = ArrayAdapter(this, android.R.layout.simple_spinner_item, valoresFrecuencia)
        spinnerFrecuencia.adapter = adaptadorFrecuencia

        binding.btnSaberMas.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://www.unp.edu.ar/ingenieria/index.php/es/"))
            startActivity(intent)
        }

        binding.btnGuardar.setOnClickListener{

            //Toast.makeText(this@MainActivity, ""+binding.spinnerPorcion.selectedItem, Toast.LENGTH_SHORT).show()

            val valorPorcion: String = binding.spinnerPorcion.selectedItem as String
            val valorFrecuencia: String = binding.spinnerFrecuencia.selectedItem as String

            val intent = Intent(this, ConfirmacionActivity::class.java)

            intent.putExtra("porcion", valorPorcion)
            intent.putExtra("frecuencia", valorFrecuencia)
            intent.putExtra("veces", binding.numberPiker.value.toString())

            startActivity(intent)
            finish();

            //binding.textView.text = binding.editText.text
        }


        spinnerPorcion.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                //Toast.makeText(this@MainActivity, valoresPorcion[position], Toast.LENGTH_SHORT)
                Log.i("spinner", valoresPorcion[position])

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

    }

    override fun onStart() {
        super.onStart()
        //Log.i("Actividad principal", "mensaje en onStart - al iniciar")
    }

    override fun onStop() {
        super.onStop()
        //Log.i("Actividad principal", "mensaje en onStop - en segundo plano")
    }

    override fun onRestart() {
        super.onRestart()
        //Log.i("Actividad principal", "mensaje en onRestart - app visible")
    }

    override fun onResume() {
        super.onResume()
        //Log.i("Actividad principal", "mensaje en onResume - primer plano")
    }

    override fun onDestroy() {
        super.onDestroy()
        //Log.i("Actividad principal", "mensaje en onDestroy - cuando finaliza app")
    }


}