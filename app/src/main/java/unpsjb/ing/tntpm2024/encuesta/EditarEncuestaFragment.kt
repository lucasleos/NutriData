package unpsjb.ing.tntpm2024.encuesta

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.databinding.FragmentEditarEncuestaBinding
import java.util.Date

class EditarEncuestaFragment : Fragment() {

    var isSaved: Boolean = false
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

        // Setear el valor del campo "Veces"
        binding.inputVeces.text =
            Editable.Factory.getInstance().newEditable(args.veces?.toString() ?: "")

        // Configuración del AutoCompleteTextView para la Porción
        val autoCompletePorcion = binding.autoCompleteTextViewPorcion
        val itemsPorcion = resources.getStringArray(R.array.opcionesPorcion)
        val adapterPorcion =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsPorcion)
        autoCompletePorcion.setAdapter(adapterPorcion)

        // Configuración del AutoCompleteTextView para la Frecuencia
        val autoCompleteFrecuencia = binding.autoCompleteTextViewFrecuencia
        val itemsFrecuencia = resources.getStringArray(R.array.opcionesFrecuencia)
        val adapterFrecuencia =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsFrecuencia)
        autoCompleteFrecuencia.setAdapter(adapterFrecuencia)

        // Setear los valores que vinieron de la otra pantalla
        autoCompletePorcion.setText(args.porcion, false)
        autoCompleteFrecuencia.setText(args.frecuencia, false)

        binding.btnGuardar.setOnClickListener {
            if (validarInputs()) {
                isSaved = true
                editarEncuesta(true)
            }
        }
        return binding.root
    }

    private fun validarInputs(): Boolean {
        val valorPorcion: String = binding.autoCompleteTextViewPorcion.text.toString()
        val valorFrecuencia: String = binding.autoCompleteTextViewFrecuencia.text.toString()
        val valorVeces: String = binding.inputVeces.text.toString()
        var esValido: Boolean = true

        if (valorPorcion.isEmpty()) {
            binding.tfPorcion.error = "Error: Input Vacío."
            esValido = false
        }

        if (valorFrecuencia.isEmpty()) {
            binding.tfFrecuencia.error = "Error: Input Vacío."
            esValido = false
        }

        if (valorVeces.isEmpty()) {
            binding.tfVeces.error = "Error: Input Vacío."
            esValido = false
        }

        return esValido
    }

    override fun onStop() {
        super.onStop()
        if (!isSaved)
            editarEncuesta(false)
    }

    private fun editarEncuesta(encuestaCompletada: Boolean) {
        val valorPorcion: String = binding.autoCompleteTextViewPorcion.text.toString()
        val valorFrecuencia: String = binding.autoCompleteTextViewFrecuencia.text.toString()
        val valorVeces: String = binding.inputVeces.text.toString()
        val fechaActual: Date = Date() // Crea un objeto Date con la fecha actual
        val fechaLong: Long = fechaActual.time // Convierte Date a Long

        // Para guardar encuesta
        viewModel.editEncuesta(
            args.encuestaId,
            "Yogur Bebible",
            valorPorcion,
            valorFrecuencia,
            valorVeces,
            fechaLong,
            encuestaCompletada
        )
        requireActivity().supportFragmentManager.popBackStack()
    }
}
