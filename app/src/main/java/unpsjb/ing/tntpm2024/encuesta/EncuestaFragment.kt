package unpsjb.ing.tntpm2024.encuesta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.encuestas.Encuesta

import unpsjb.ing.tntpm2024.databinding.FragmentEncuestaBinding

class EncuestaFragment : Fragment() {

    private lateinit var binding: FragmentEncuestaBinding
    private val viewModel: EncuestaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_encuesta, container, false
        )

        binding.encuestaViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

//        binding.spinnerPorcion.selectedItem.toString() = viewModel.porcion.value
//        binding.spinnerFrecuencia.selectedItem.toString() = viewModel.frecuencia.value
        binding.numberPicker.value = viewModel.veces.value!!


        val t = inflater.inflate(R.layout.fragment_encuesta,container,false)

        val spinnerPorcion = binding.spinnerPorcion

        val valoresPorcion = resources.getStringArray(R.array.opcionesPorcion)
        val adaptadorPorcion = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, valoresPorcion)
        spinnerPorcion.adapter = adaptadorPorcion

        binding.numberPicker.minValue = 0
        binding.numberPicker.maxValue = 10

        val spinnerFrecuencia = binding.spinnerFrecuencia
        val valoresFrecuencia = resources.getStringArray(R.array.opcionesFrecuencia)
        val adaptadorFrecuencia = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, valoresFrecuencia)
        spinnerFrecuencia.adapter = adaptadorFrecuencia

        binding.btnGuardar.setOnClickListener{

            val valorPorcion: String = binding.spinnerPorcion.selectedItem as String
            val valorFrecuencia: String = binding.spinnerFrecuencia.selectedItem as String
            val valorVeces: String = binding.numberPicker.value.toString()


            val encuesta = Encuesta(
                alimento = "Yogur Bebible",
                porcion = valorPorcion,
                frecuencia = valorFrecuencia,
                veces = valorVeces,
                encuestaCompletada = true
            )

            viewModel.insert(encuesta)
            findNavController().navigate(R.id.action_encuestaFragment_to_encuestalist2)
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



