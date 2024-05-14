package unpsjb.ing.tntpm2024

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import unpsjb.ing.tntpm2024.Inicio.InicioViewModel
import unpsjb.ing.tntpm2024.databinding.FragmentEncuestaBinding

class EncuestaFragment : Fragment() {

    private val viewModel: InicioViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentEncuestaBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_encuesta, container, false
        )

        val t = inflater.inflate(R.layout.fragment_encuesta,container,false)

        val spinnerPorcion = binding.spinnerPorcion

        val valoresPorcion = resources.getStringArray(R.array.opcionesPorcion)
        val adaptadorPorcion = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, valoresPorcion)
        spinnerPorcion.adapter = adaptadorPorcion

        binding.numberPiker.minValue = 0
        binding.numberPiker.maxValue = 10

        val spinnerFrecuencia = binding.spinnerFrecuencia
        val valoresFrecuencia = resources.getStringArray(R.array.opcionesFrecuencia)
        val adaptadorFrecuencia = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, valoresFrecuencia)
        spinnerFrecuencia.adapter = adaptadorFrecuencia

        binding.btnSaberMas.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://www.unp.edu.ar/ingenieria/index.php/es/"))
            startActivity(intent)
        }


        // evento boton guardar
        binding.btnGuardar.setOnClickListener{

            val valorPorcion: String = binding.spinnerPorcion.selectedItem as String
            val valorFrecuencia: String = binding.spinnerFrecuencia.selectedItem as String
            val valorVeces: String = binding.numberPiker.value.toString()

            findNavController().navigate(EncuestaFragmentDirections.actionEncuestaFragmentToConfirmacionFragment(
                porcion = valorPorcion,
                frecuencia = valorFrecuencia,
                veces =  valorVeces))
        }

        spinnerPorcion.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                //Toast.makeText(this@MainActivity, valoresPorcion[position], Toast.LENGTH_SHORT)
                Log.i("spinner", valoresPorcion[position])

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        return binding.root

    }
}



