package unpsjb.ing.tntpm2024.encuesta

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentoEncuesta
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.databinding.FragmentNuevaEncuestaBinding
import java.util.Date

class NuevaEncuestaFragment : Fragment() {

    private val args: NuevaEncuestaFragmentArgs by navArgs()
    private var _binding: FragmentNuevaEncuestaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EncuestaViewModel
    private lateinit var alimentoViewModel: AlimentoViewModel
    private lateinit var aeViewModel: AlimentoEncuestaViewModel

    private var listaAlimentos = emptyList<Alimento>()
    private var encuestaId = 0
    private var isSaved = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNuevaEncuestaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inicializarViewModels()
        configurarUI()
        gestionarCreacionOEdicion()
        observarAlimentos()
    }

    private fun inicializarViewModels() {
        val database = EncuestasDatabase.getInstance(requireContext())
        val factory = AppViewModelFactory(database)

        viewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]
        alimentoViewModel = ViewModelProvider(this, factory)[AlimentoViewModel::class.java]
        aeViewModel = ViewModelProvider(this, factory)[AlimentoEncuestaViewModel::class.java]

        binding.nuevaEncuestaViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun configurarUI() {
        binding.tvZona.text = args.zona
        configurarDropdowns()

        binding.btnGuardar.setOnClickListener { procesarGuardadoParcial() }
        binding.btnEditZona.setOnClickListener {
            val action = NuevaEncuestaFragmentDirections.actionNuevaEncuestaFragmentToMapsFragment(false, encuestaId)
            findNavController().navigate(action)
        }
    }

    private fun configurarDropdowns() {
        val adapterPorcion = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.opcionesPorcion))
        binding.autoCompleteTextViewPorcion.setAdapter(adapterPorcion)

        val adapterFrecuencia = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.opcionesFrecuencia))
        binding.autoCompleteTextViewFrecuencia.setAdapter(adapterFrecuencia)
    }

    private fun gestionarCreacionOEdicion() {
        encuestaId = args.encuestaId
        if (encuestaId == 0) {
            val user = FirebaseAuth.getInstance().currentUser
            val nuevaEncuesta = Encuesta(fecha = Date().time, encuestaCompletada = false, zona = args.zona, userId = user?.uid ?: "admin")
            viewModel.cargarEncuesta(nuevaEncuesta) { idGenerado -> encuestaId = idGenerado.toInt() }
        }
    }

    private fun observarAlimentos() {
        alimentoViewModel.allAlimentos.observe(viewLifecycleOwner) { alimentos ->
            listaAlimentos = alimentos

            // Observamos el índice actual en el ViewModel
            aeViewModel.indiceAlimentoActual.observe(viewLifecycleOwner) { indice ->
                if (listaAlimentos.isNotEmpty() && indice < listaAlimentos.size) {
                    binding.tvListadoEncuestas.text = listaAlimentos[indice].nombre
                }
            }
        }
    }

    private fun procesarGuardadoParcial() {
        binding.tfPorcion.error = null
        binding.tfFrecuencia.error = null
        binding.tfVeces.error = null

        if (!validarInputs()) return

        val indiceActual = aeViewModel.indiceAlimentoActual.value ?: 0

        val alimentoEncuesta = AlimentoEncuesta(
            encuestaId = encuestaId,
            alimentoId = listaAlimentos[indiceActual].alimentoId,
            porcion = binding.autoCompleteTextViewPorcion.text.toString(),
            frecuencia = binding.autoCompleteTextViewFrecuencia.text.toString(),
            veces = binding.inputVeces.text.toString()
        )
        aeViewModel.insert(alimentoEncuesta)
        Toast.makeText(requireContext(), "Consumo registrado", Toast.LENGTH_SHORT).show()

        if (indiceActual == listaAlimentos.size - 1) {
            finalizarEncuesta()
        } else {
            // Limpiar inputs y avanzar de estado
            binding.autoCompleteTextViewPorcion.text = null
            binding.autoCompleteTextViewFrecuencia.text = null
            binding.inputVeces.text = null
            aeViewModel.avanzarAlSiguienteAlimento()
        }
    }

    private fun finalizarEncuesta() {
        isSaved = true
        viewModel.editEncuesta(Encuesta(encuestaId = encuestaId, fecha = Date().time, encuestaCompletada = true, zona = args.zona))
        Toast.makeText(requireContext(), "Encuesta Finalizada", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_nuevaEncuestaFragment_to_encuestalist)
    }

    private fun validarInputs(): Boolean {
        val porcionValida = validarCampo(binding.autoCompleteTextViewPorcion, binding.tfPorcion)
        val frecValida = validarCampo(binding.autoCompleteTextViewFrecuencia, binding.tfFrecuencia)
        val vecesValida = validarCampo(binding.inputVeces, binding.tfVeces)
        return porcionValida && frecValida && vecesValida
    }

    private fun validarCampo(input: EditText, textField: TextInputLayout): Boolean {
        return if (input.text.isNullOrBlank()) {
            textField.error = "Campo requerido"
            false
        } else {
            textField.error = null
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}