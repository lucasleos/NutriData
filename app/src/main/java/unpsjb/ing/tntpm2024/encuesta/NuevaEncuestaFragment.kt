package unpsjb.ing.tntpm2024.encuesta

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentoEncuesta
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.databinding.FragmentNuevaEncuestaBinding
import java.util.Date

class NuevaEncuestaFragment : Fragment() {

    private var isSaved: Boolean = false
    var isFirstload: Boolean = true
    private val args: NuevaEncuestaFragmentArgs by navArgs()

    private lateinit var binding: FragmentNuevaEncuestaBinding
    private lateinit var viewModel: EncuestaViewModel
    private lateinit var alimentoViewModel: AlimentoViewModel
    private lateinit var alimentoEncuestaViewModel: AlimentoEncuestaViewModel

    private var listaAlimentos: List<Alimento> = listOf()

    private var encuestaId = 0
    private lateinit var encuesta: Encuesta
    private var i = 0

    var user = FirebaseAuth.getInstance().currentUser

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

        val alimentoFactory = AlimentoViewModelFactory(database)
        alimentoViewModel = ViewModelProvider(this, alimentoFactory)[AlimentoViewModel::class.java]

        val alimentoEncuestaFactory = AlimentoEncuestaViewModelFactory(database)
        alimentoEncuestaViewModel =
            ViewModelProvider(this, alimentoEncuestaFactory)[AlimentoEncuestaViewModel::class.java]

        binding.nuevaEncuestaViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        alimentoViewModel.allAlimentos.observe(viewLifecycleOwner, Observer { alimentos ->
            listaAlimentos = alimentos
            if (listaAlimentos.isNotEmpty()) {
                binding.tvListadoEncuestas.text = listaAlimentos[0].nombre
            }
        })

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

        encuestaId = args.encuestaId

        fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

        if (encuestaId == 0) {
            encuesta = Encuesta(
                fecha = Date().time,
                encuestaCompletada = false,
                zona = args.zona,
                userId = user?.uid,
                userEmail = user?.email
            )

            viewModel.cargarEncuesta(encuesta) { id ->
                encuestaId = id.toInt()
            }

        } else {
            setupObservers()
        }

        binding.btnGuardar.setOnClickListener {

            binding.tfPorcion.error = null
            binding.tfFrecuencia.error = null
            binding.tfVeces.error = null

            if (validarInputs()) {
                val alimentoEncuesta = AlimentoEncuesta(
                    encuestaId = encuestaId,
                    alimentoId = listaAlimentos[i].alimentoId,
                    porcion = binding.autoCompleteTextViewPorcion.text.toString(),
                    frecuencia = binding.autoCompleteTextViewFrecuencia.text.toString(),
                    veces = binding.inputVeces.text.toString()
                )
                alimentoEncuestaViewModel.insert(alimentoEncuesta)

                Toast.makeText(
                    requireContext(),
                    "Consumo de " + listaAlimentos[i].nombre + " registrado",
                    Toast.LENGTH_SHORT
                ).show()

                if (i == listaAlimentos.size - 1) {
                    Toast.makeText(
                        requireContext(),
                        "Encuesta Finalizada",
                        Toast.LENGTH_SHORT
                    ).show()
                    isSaved = true
                    encuesta.encuestaCompletada = true
                    viewModel.editEncuesta(
                        Encuesta(
                            encuestaId = encuestaId,
                            fecha = Date().time,
                            encuestaCompletada = true,
                            zona = args.zona
                        )
                    )
                    findNavController().navigate(R.id.action_nuevaEncuestaFragment_to_encuestalist)
                } else {
                    binding.tvListadoEncuestas.text = listaAlimentos[++i].nombre

                    binding.autoCompleteTextViewPorcion.setText("")
                    binding.autoCompleteTextViewFrecuencia.setText("")
                    binding.inputVeces.setText("")
                }
            }
        }

        binding.btnEditZona.setOnClickListener {
            findNavController().navigate(
                NuevaEncuestaFragmentDirections.actionNuevaEncuestaFragmentToMapsFragment(
                    false,
                    encuestaId
                )
            )
        }

        return binding.root
    }

    private fun setupObservers() {
        viewModel.getEncuestaById(encuestaId).observe(viewLifecycleOwner) { response ->
            if (response != null) {
                encuesta = response
                viewModel.getAlimentosByEncuestaId(encuestaId)
                    .observe(viewLifecycleOwner) { alimentos ->
                        if (isFirstload) {
                            i = alimentos.size - 1
                            binding.tvListadoEncuestas.text = listaAlimentos[i].nombre
                            if (alimentos.isNotEmpty()) {
                                binding.tvListadoEncuestas.text = listaAlimentos[i].nombre
                                if (alimentos[i].porcion.isNotEmpty()) binding.autoCompleteTextViewPorcion.text =
                                    alimentos[i].porcion.toEditable()
                                if (alimentos[i].frecuencia.isNotEmpty()) binding.autoCompleteTextViewFrecuencia.text =
                                    alimentos[i].frecuencia.toEditable()
                                if (alimentos[i].veces.isNotEmpty()) binding.inputVeces.text =
                                    alimentos[i].veces.toEditable()
                                isFirstload = false
                            }
                        }
                    }
            }
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onStop() {
        super.onStop()
        Log.i("NUEVA ENCUESTA", "ENTRE ONSTOP")
        if (!isSaved) {
            Log.i("NUEVA ENCUESTA", "ENTRE ONSTOP")
            val porcion = binding.autoCompleteTextViewPorcion.text.toString()
            val frecuencia = binding.autoCompleteTextViewFrecuencia.text.toString()
            val veces = binding.inputVeces.text.toString()

            // Guardar el AlimentoEncuesta si al menos uno de los campos tiene datos.
            if (porcion.isNotEmpty() || frecuencia.isNotEmpty() || veces.isNotEmpty()) {
                val alimentoEncuesta = AlimentoEncuesta(
                    encuestaId = encuestaId,
                    alimentoId = listaAlimentos.getOrNull(i)?.alimentoId ?: 0,
                    porcion = porcion,
                    frecuencia = frecuencia,
                    veces = veces
                )
                alimentoEncuestaViewModel.insert(alimentoEncuesta)
                Log.i("NuevaEncuestaFragment", "AlimentoEncuesta guardado en onStop")
            } else {
                Log.i(
                    "NuevaEncuestaFragment",
                    "No hay datos suficientes para guardar AlimentoEncuesta en onStop"
                )
            }
        }
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
