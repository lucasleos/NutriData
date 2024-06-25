package unpsjb.ing.tntpm2024.encuesta

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
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

    var isSaved: Boolean = false
    var isFirstload: Boolean = true
    private var isEmptyList: Boolean = true
    val args: EditarEncuestaFragmentArgs by navArgs()

    private lateinit var binding: FragmentEditarEncuestaBinding
    private lateinit var viewModel: EncuestaViewModel
    private lateinit var alimentoViewModel: AlimentoViewModel
    private lateinit var alimentoEncuestaViewModel: AlimentoEncuestaViewModel

    private var listaAlimentos: List<Alimento> = listOf()

    private var encuestaId = 0
    private lateinit var encuesta: Encuesta
    private var i = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_editar_encuesta, container, false
        )

        val database = EncuestasDatabase.getInstance(requireContext())

        val factory = EncuestaViewModelFactory(database)
        viewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]

        val alimentoFactory = AlimentoViewModelFactory(database)
        alimentoViewModel = ViewModelProvider(this, alimentoFactory)[AlimentoViewModel::class.java]

        val alimentoEncuestaFactory = AlimentoEncuestaViewModelFactory(database)
        alimentoEncuestaViewModel =
            ViewModelProvider(this, alimentoEncuestaFactory)[AlimentoEncuestaViewModel::class.java]

        binding.encuestaViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        alimentoViewModel.allAlimentos.observe(viewLifecycleOwner) { alimentos ->
            listaAlimentos = alimentos
            if (listaAlimentos.isNotEmpty()) {
                binding.tvListadoEncuestas.text = listaAlimentos[0].nombre
            }
        }

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

        setupObservers()

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
                    editarEncuesta(true)
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    isEmptyList = false
                    binding.tvListadoEncuestas.text = listaAlimentos[++i].nombre

                    binding.autoCompleteTextViewPorcion.setText("")
                    binding.autoCompleteTextViewFrecuencia.setText("")
                    binding.inputVeces.setText("")
                }
            }
        }

        binding.btnEditZona.setOnClickListener {
            findNavController().navigate(
                EditarEncuestaFragmentDirections.actionEditarEncuestaFragmentToMapsFragment(
                    true,
                    args.encuestaId
                )
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun setupObservers() {
        viewModel.getEncuestaById(encuestaId).observe(viewLifecycleOwner) { response ->
            if (response != null) {
                encuesta = response
                viewModel.getAlimentosByEncuestaId(encuestaId)
                    .observe(viewLifecycleOwner) { alimentos ->
                        if(isFirstload && isEmptyList) {
                            i = alimentos.size - 1
                            Log.i("Editar ENCUESTA", "ENTRE OTRA VEZ")
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

    private fun validarInputs(): Boolean {
        var esValido = true

        esValido =
            validarCampo(binding.autoCompleteTextViewPorcion, binding.tfPorcion) && esValido
        esValido =
            validarCampo(
                binding.autoCompleteTextViewFrecuencia,
                binding.tfFrecuencia
            ) && esValido
        esValido = validarCampo(binding.inputVeces, binding.tfVeces) && esValido

        return esValido
    }

    private fun validarCampo(input: EditText, textField: TextInputLayout): Boolean {
        return if (input.text.toString().isEmpty()) {
            textField.error = "Error: Input Vacío."
            false
        } else {
            textField.error = null
            true
        }
    }

    private fun editarEncuesta(encuestaCompletada: Boolean) {
        val id = args.encuestaId
        val fechaActual = Date().time

        viewModel.editEncuesta(
            Encuesta(
                id,
                fechaActual,
                encuestaCompletada,
                args.zona
            )
        )
    }

    override fun onStop() {
        super.onStop()
        if (!isSaved) {
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
                Log.i("NuevaEncuestaFragment", "No hay datos suficientes para guardar AlimentoEncuesta en onStop")
            }
        }
    }

    private fun setupAutoCompleteTextView(
        autoCompleteTextView: AutoCompleteTextView,
        arrayResId: Int,
        initialValue: String?
    ) {
        val items = resources.getStringArray(arrayResId)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setText(initialValue, false)
    }

}
