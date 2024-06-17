package unpsjb.ing.tntpm2024.encuesta

import android.os.Bundle
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
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentoEncuesta
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.databinding.FragmentNuevaEncuestaBinding
import java.util.Date

class NuevaEncuestaFragment : Fragment() {

    private var isSaved: Boolean = false
    private var isEditZonaClicked: Boolean = false
    private val args: NuevaEncuestaFragmentArgs by navArgs()

    private lateinit var binding: FragmentNuevaEncuestaBinding
    private lateinit var viewModel: EncuestaViewModel
    private lateinit var alimentoViewModel: AlimentoViewModel
    private lateinit var alimentoEncuestaViewModel: AlimentoEncuestaViewModel

    private var listaAlimentos: List<Alimento> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_nueva_encuesta, container, false
        )

        val database = EncuestasDatabase.getInstance(requireContext())

        // EncuestaViewModel
        val factory = EncuestaViewModelFactory(database)
        viewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]

        // AlimentoViewModel
        val alimentoFactory = AlimentoViewModelFactory(database)
        alimentoViewModel = ViewModelProvider(this, alimentoFactory)[AlimentoViewModel::class.java]

        // AlimentoEncuestaViewModel
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


        // TODO: Ver eso de la encuesta completa o no.
        val encuesta = Encuesta(
            fecha = Date().time,
            encuestaCompletada = true,
            zona = args.zona
        )

        var encuestaId: Long = 0
        viewModel.cargarEncuesta(encuesta) { id ->
            encuestaId = id
        }

        var i = 0

        binding.btnGuardar.setOnClickListener {

            binding.tfPorcion.error = null
            binding.tfFrecuencia.error = null
            binding.tfVeces.error = null

            if (validarInputs()) {
                val alimentoEncuesta = AlimentoEncuesta(
                    encuestaId = encuestaId.toInt(),
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
                    //encuesta.encuestaCompletada = true
                    //completarEncuesta(encuesta)
                    findNavController().navigate(R.id.action_nuevaEncuestaFragment_to_encuestalist)
                } else {
                    binding.tvListadoEncuestas.text = listaAlimentos[++i].nombre

                    // Limpiar los campos de autocompletar y de texto
                    binding.autoCompleteTextViewPorcion.setText("")
                    binding.autoCompleteTextViewFrecuencia.setText("")
                    binding.inputVeces.setText("")
                }
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
            println("Hola")
    }

    /*
        private fun guardarEncuesta(encuestaCompletada: Boolean) {
            viewModel.insert(
                Encuesta(
                    fecha = Date().time,
                    encuestaCompletada = encuestaCompletada,
                    zona = args.zona
                )
            )
        }*/

    private fun completarEncuesta(encuesta: Encuesta) {
        Log.i("NUEVA", "ENTREEEEEEE COMPLETAR ENCUESTA")
        Log.i("NUEVA", encuesta.encuestaCompletada.toString())
        viewModel.editEncuesta(encuesta)
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
