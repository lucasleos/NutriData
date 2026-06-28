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
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentoEncuesta
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.databinding.FragmentEditarEncuestaBinding
import java.util.Date

class EditarEncuestaFragment : Fragment() {

    private val args: EditarEncuestaFragmentArgs by navArgs()
    private var _binding: FragmentEditarEncuestaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EncuestaViewModel
    private lateinit var alimentoViewModel: AlimentoViewModel
    private lateinit var aeViewModel: AlimentoEncuestaViewModel

    private var listaAlimentos = emptyList<Alimento>()
    private var encuestaId = 0
    private var isSaved = false
    private var isFirstload = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditarEncuestaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        encuestaId = args.encuestaId

        inicializarViewModels()
        configurarUI()
        setupObservers()
    }

    private fun inicializarViewModels() {
        val database = EncuestasDatabase.getInstance(requireContext())

        // Utilizamos la única fábrica unificada
        val factory = AppViewModelFactory(database)

        viewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]
        alimentoViewModel = ViewModelProvider(this, factory)[AlimentoViewModel::class.java]
        aeViewModel = ViewModelProvider(this, factory)[AlimentoEncuestaViewModel::class.java]

        binding.encuestaViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun configurarUI() {
        binding.tvZona.text = args.zona

        val adapterPorcion = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.opcionesPorcion))
        binding.autoCompleteTextViewPorcion.setAdapter(adapterPorcion)

        val adapterFrecuencia = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.opcionesFrecuencia))
        binding.autoCompleteTextViewFrecuencia.setAdapter(adapterFrecuencia)

        binding.btnGuardar.setOnClickListener { procesarGuardadoParcial() }

        binding.btnEditZona.setOnClickListener {
            val action = EditarEncuestaFragmentDirections.actionEditarEncuestaFragmentToMapsFragment(true, encuestaId)
            findNavController().navigate(action)
        }
    }

    private fun setupObservers() {
        alimentoViewModel.allAlimentos.observe(viewLifecycleOwner) { alimentos ->
            listaAlimentos = alimentos

            // Observamos el estado del progreso desde el ViewModel
            aeViewModel.indiceAlimentoActual.observe(viewLifecycleOwner) { indice ->
                if (listaAlimentos.isNotEmpty() && indice < listaAlimentos.size) {
                    binding.tvListadoEncuestas.text = listaAlimentos[indice].nombre
                }
            }
        }

        viewModel.getEncuestaById(encuestaId).observe(viewLifecycleOwner) { response ->
            if (response != null) {
                viewModel.getAlimentosByEncuestaId(encuestaId).observe(viewLifecycleOwner) { alimentosRegistrados ->
                    if (isFirstload) {
                        val ultimoIndiceRegistrado = if (alimentosRegistrados.isNotEmpty()) alimentosRegistrados.size - 1 else 0

                        // Seteamos el índice recuperado en el ViewModel central
                        aeViewModel.setIndiceInicial(ultimoIndiceRegistrado)

                        if (alimentosRegistrados.isNotEmpty()) {
                            val ultimoAlimento = alimentosRegistrados[ultimoIndiceRegistrado]
                            binding.autoCompleteTextViewPorcion.text = ultimoAlimento.porcion.toEditable()
                            binding.autoCompleteTextViewFrecuencia.text = ultimoAlimento.frecuencia.toEditable()
                            binding.inputVeces.text = ultimoAlimento.veces.toEditable()
                        }
                        isFirstload = false
                    }
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
            binding.autoCompleteTextViewPorcion.text = null
            binding.autoCompleteTextViewFrecuencia.text = null
            binding.inputVeces.text = null
            aeViewModel.avanzarAlSiguienteAlimento()
        }
    }

    private fun finalizarEncuesta() {
        isSaved = true
        viewModel.editEncuesta(Encuesta(encuestaId, Date().time, true, args.zona))
        Toast.makeText(requireContext(), "Encuesta Finalizada", Toast.LENGTH_SHORT).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun validarInputs(): Boolean {
        val porcionValida = validarCampo(binding.autoCompleteTextViewPorcion, binding.tfPorcion)
        val frecValida = validarCampo(binding.autoCompleteTextViewFrecuencia, binding.tfFrecuencia)
        val vecesValida = validarCampo(binding.inputVeces, binding.tfVeces)
        return porcionValida && frecValida && vecesValida
    }

    private fun validarCampo(input: EditText, textField: TextInputLayout): Boolean {
        return if (input.text.isNullOrBlank()) {
            textField.error = "Error: Input Vacío."
            false
        } else {
            textField.error = null
            true
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isSaved) {
            val porcion = binding.autoCompleteTextViewPorcion.text.toString()
            val frecuencia = binding.autoCompleteTextViewFrecuencia.text.toString()
            val veces = binding.inputVeces.text.toString()

            if (porcion.isNotEmpty() || frecuencia.isNotEmpty() || veces.isNotEmpty()) {
                val indiceActual = aeViewModel.indiceAlimentoActual.value ?: 0
                val alimentoEncuesta = AlimentoEncuesta(
                    encuestaId = encuestaId,
                    alimentoId = listaAlimentos.getOrNull(indiceActual)?.alimentoId ?: 0,
                    porcion = porcion,
                    frecuencia = frecuencia,
                    veces = veces
                )
                aeViewModel.insert(alimentoEncuesta)
                Log.i("EditarEncuestaFragment", "AlimentoEncuesta guardado en onStop")
            }
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}