package unpsjb.ing.tntpm2024.encuesta

import android.os.Bundle
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
import unpsjb.ing.tntpm2024.basededatos.entidades.Turno
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
    private var listaTurnosAsignados = listOf<Turno>()
    private var turnoIdSeleccionado: String? = null
    private var encuestaActual: Encuesta? = null
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
        configurarObservadorTurnos()
        gestionarCreacionOEdicion()
        observarAlimentos()
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
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

    private fun configurarObservadorTurnos() {
        viewModel.obtenerTurnosAsignados().observe(viewLifecycleOwner) { turnos ->
            listaTurnosAsignados = turnos
            val nombresOpciones = turnos.map { turno ->
                val voluntarioCorto = turno.voluntarioId.take(6)
                val fecha = turno.asignacion?.fecha ?: "Sin fecha"
                val hora = turno.asignacion?.hora ?: ""
                "Voluntario: $voluntarioCorto ($fecha $hora)"
            }

            val adapterTurnos = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombresOpciones)
            binding.autoCompleteTextViewTurno.setAdapter(adapterTurnos)

            binding.autoCompleteTextViewTurno.setOnItemClickListener { _, _, position, _ ->
                turnoIdSeleccionado = listaTurnosAsignados[position].turnoId
                encuestaActual?.let { encuesta ->
                    encuesta.turnoId = turnoIdSeleccionado
                    viewModel.editEncuesta(encuesta)
                }
                Toast.makeText(requireContext(), "Turno vinculado a la encuesta", Toast.LENGTH_SHORT).show()
            }

            actualizarTextoTurno()
        }
    }

    private fun actualizarTextoTurno() {
        val currentTurnoId = turnoIdSeleccionado ?: encuestaActual?.turnoId ?: return
        val turnoEncontrado = listaTurnosAsignados.find { it.turnoId == currentTurnoId }
        if (turnoEncontrado != null) {
            val voluntarioCorto = turnoEncontrado.voluntarioId.take(6)
            val fecha = turnoEncontrado.asignacion?.fecha ?: "Sin fecha"
            val hora = turnoEncontrado.asignacion?.hora ?: ""
            val textoTurno = "Voluntario: $voluntarioCorto ($fecha $hora)"
            binding.autoCompleteTextViewTurno.setText(textoTurno, false)
        } else {
            binding.autoCompleteTextViewTurno.setText("Turno ID: ${currentTurnoId.take(8)}...", false)
        }
    }

    private fun gestionarCreacionOEdicion() {
        encuestaId = args.encuestaId
        if (encuestaId == 0) {
            val user = FirebaseAuth.getInstance().currentUser
            val nuevaEncuesta = Encuesta(
                fecha = Date().time,
                encuestaCompletada = false,
                zona = args.zona,
                userId = user?.uid ?: "admin",
                turnoId = turnoIdSeleccionado
            )
            viewModel.cargarEncuesta(nuevaEncuesta) { idGenerado ->
                encuestaId = idGenerado.toInt()
                observarEncuestaActual()
            }
        } else {
            observarEncuestaActual()
        }
    }

    private fun observarEncuestaActual() {
        if (encuestaId != 0) {
            viewModel.getEncuestaById(encuestaId).observe(viewLifecycleOwner) { response ->
                if (response != null) {
                    encuestaActual = response
                    if (turnoIdSeleccionado == null && !response.turnoId.isNullOrEmpty()) {
                        turnoIdSeleccionado = response.turnoId
                    }
                    if (turnoIdSeleccionado != null && response.turnoId != turnoIdSeleccionado) {
                        response.turnoId = turnoIdSeleccionado
                        viewModel.editEncuesta(response)
                    }
                    actualizarTextoTurno()
                }
            }
        }
    }

    private fun observarAlimentos() {
        alimentoViewModel.allAlimentos.observe(viewLifecycleOwner) { alimentos ->
            listaAlimentos = alimentos
            binding.btnGuardar.isEnabled = alimentos.isNotEmpty()
            if (alimentos.isEmpty()) {
                Toast.makeText(requireContext(), "No hay alimentos cargados.", Toast.LENGTH_LONG).show()
            }
            actualizarNombreAlimentoActual()
        }

        aeViewModel.indiceAlimentoActual.observe(viewLifecycleOwner) {
            actualizarNombreAlimentoActual()
        }
    }

    private fun actualizarNombreAlimentoActual() {
        val indice = aeViewModel.indiceAlimentoActual.value ?: 0
        if (listaAlimentos.isNotEmpty() && indice < listaAlimentos.size) {
            val alimento = listaAlimentos[indice]
            binding.tvListadoEncuestas.text = alimento.nombre
            cargarImagenAlimento(alimento.imagenNombre)
        }
    }

    private fun cargarImagenAlimento(nombreImagen: String?) {
        if (nombreImagen.isNullOrEmpty()) {
            binding.ivAlimento.setImageResource(R.drawable.ic_food_logo)
            return
        }

        val resId = resources.getIdentifier(nombreImagen, "drawable", requireContext().packageName)
        binding.ivAlimento.setImageResource(if (resId != 0) resId else R.drawable.ic_food_logo)
    }

    private fun procesarGuardadoParcial() {
        if (listaAlimentos.isEmpty()) return
        val indiceActual = aeViewModel.indiceAlimentoActual.value ?: 0
        if (!validarInputs()) return

        val alimentoEncuesta = AlimentoEncuesta(
            encuestaId = encuestaId,
            alimentoId = listaAlimentos[indiceActual].alimentoId,
            porcion = binding.autoCompleteTextViewPorcion.text.toString(),
            frecuencia = binding.autoCompleteTextViewFrecuencia.text.toString(),
            veces = binding.inputVeces.text.toString()
        )
        aeViewModel.insert(alimentoEncuesta)

        if (indiceActual == listaAlimentos.size - 1) {
            finalizarEncuesta()
        } else {
            limpiarCampos()
            aeViewModel.avanzarAlSiguienteAlimento()
        }
    }

    private fun limpiarCampos() {
        binding.autoCompleteTextViewPorcion.setText("", false)
        binding.autoCompleteTextViewFrecuencia.setText("", false)
        binding.inputVeces.text = null

        binding.tfPorcion.error = null
        binding.tfFrecuencia.error = null
        binding.tfVeces.error = null
    }

    private fun finalizarEncuesta() {
        isSaved = true
        val user = FirebaseAuth.getInstance().currentUser

        val encuestaFinalizada = Encuesta(
            encuestaId = encuestaId,
            fecha = Date().time,
            encuestaCompletada = true,
            zona = args.zona,
            userId = user?.uid,
            userEmail = user?.email,
            turnoId = turnoIdSeleccionado,
            subida = encuestaActual?.subida ?: false
        )

        viewModel.editEncuesta(encuestaFinalizada)
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

    override fun onStop() {
        super.onStop()
        if (!isSaved) {
            encuestaActual?.let { encuesta ->
                if (turnoIdSeleccionado != null && encuesta.turnoId != turnoIdSeleccionado) {
                    encuesta.turnoId = turnoIdSeleccionado
                    viewModel.editEncuesta(encuesta)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}