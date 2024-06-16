package unpsjb.ing.tntpm2024.encuesta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.databinding.FragmentNuevaEncuestaBinding
import java.util.Date

class NuevaEncuestaFragment : Fragment() {

    private var isSaved: Boolean = false
    private var isEditZonaClicked: Boolean =
        false // Variable para controlar si se hace clic en btnEditZona
    private val args: NuevaEncuestaFragmentArgs by navArgs()

    private lateinit var binding: FragmentNuevaEncuestaBinding
    private lateinit var viewModel: EncuestaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_nueva_encuesta, container, false
        )

        val database = EncuestasDatabase.getInstance(requireContext())
        val factory = EncuestaViewModelFactory(database)
        viewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]

        binding.nuevaEncuestaViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val t = inflater.inflate(R.layout.fragment_nueva_encuesta, container, false)

        val tvZona = binding.tvZona
        tvZona.text = args.zona

        val autoCompletePorcion = binding.autoCompleteTextViewPorcion
        val itemsPorcion = resources.getStringArray(R.array.opcionesPorcion)
        val adapterPorcion =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsPorcion)
        autoCompletePorcion.setAdapter(adapterPorcion)

        val autoCompleteFrecuencia = binding.autoCompleteTextViewFrecuencia
        val itemsFrecuencia = resources.getStringArray(R.array.opcionesFrecuencia)
        val adapterFrecuencia =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsFrecuencia)
        autoCompleteFrecuencia.setAdapter(adapterFrecuencia)

        binding.btnGuardar.setOnClickListener {
            if (validarInputs()) {
                isSaved = true
                guardarEncuesta(true)
                findNavController().navigate(R.id.action_nuevaEncuestaFragment_to_encuestalist)
            }
        }

        binding.btnEditZona.setOnClickListener {
            isEditZonaClicked = true
            findNavController().navigate(
                NuevaEncuestaFragmentDirections.actionNuevaEncuestaFragmentToMapsFragment(
                    false
                )
            )
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        if (!isSaved && !isEditZonaClicked)
            guardarEncuesta(false)
    }

    private fun guardarEncuesta(encuestaCompletada: Boolean) {
        val valorPorcion: String = binding.autoCompleteTextViewPorcion.text.toString()
        val valorFrecuencia: String = binding.autoCompleteTextViewFrecuencia.text.toString()
        val fechaActual: Date = Date() // Crea un objeto Date con la fecha actual
        val fechaLong: Long = fechaActual.time // Convierte Date a Long
        val valorVeces: String = binding.inputVeces.text.toString()

        val encuesta = Encuesta(
            fecha = fechaLong,
            encuestaCompletada = encuestaCompletada,
            zona = args.zona
        )

        viewModel.insert(encuesta)
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
}
