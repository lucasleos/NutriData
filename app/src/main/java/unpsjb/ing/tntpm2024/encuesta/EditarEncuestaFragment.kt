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
import androidx.navigation.fragment.navArgs
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.databinding.FragmentEditarEncuestaBinding
import java.util.Date


class EditarEncuestaFragment : Fragment() {

    var isSaved : Boolean = false
    val args: EditarEncuestaFragmentArgs by navArgs()
    // debe vincularse con el nombre del xml
    private lateinit var binding: FragmentEditarEncuestaBinding
    private val viewModel: EncuestaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_editar_encuesta, container, false
        )

        binding.encuestaViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.numberPicker.value = viewModel.veces.value!!

        val t = inflater.inflate(R.layout.fragment_nueva_encuesta,container,false)

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


        binding.numberPicker.value = args.veces!!.toInt()

        // setear valores que vinieron de la otra pantalla
        for((index, item) in valoresFrecuencia.withIndex()){
            if(item == args.frecuencia){
                binding.spinnerFrecuencia.setSelection(index)
            }
        }

        for((index, item) in valoresPorcion.withIndex()){
            if(item == args.porcion){
                binding.spinnerPorcion.setSelection(index)
            }
        }

        binding.btnGuardar.setOnClickListener{
            isSaved = true
            editarEncuesta(true)
        }

        spinnerPorcion.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                Log.i("spinner", valoresPorcion[position])
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        return binding.root

    }

    override fun onStop() {
        super.onStop()
        if(!isSaved)
            editarEncuesta(false)
    }

    private fun editarEncuesta(encuestaCompletada: Boolean) {

        val valorPorcion: String = binding.spinnerPorcion.selectedItem as String
        val valorFrecuencia: String = binding.spinnerFrecuencia.selectedItem as String
        val valorVeces: String = binding.numberPicker.value.toString()
        val fechaActual: Date = Date() // Crea un objeto Date con la fecha actual
        val fechaLong: Long = fechaActual.time // Convierte Date a Long


        // para guardar encuesta
        viewModel.editEncuesta(args.encuestaId,"Yogur Bebible", valorPorcion, valorFrecuencia, valorVeces, fechaLong, encuestaCompletada)
        requireActivity().supportFragmentManager.popBackStack()
    }
}



