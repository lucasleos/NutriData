package unpsjb.ing.tntpm2024.encuesta

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.databinding.FragmentEditarEncuestaBinding
import java.util.Date

class EditarEncuestaFragment : Fragment() {

    var isSaved: Boolean = false
    val args: EditarEncuestaFragmentArgs by navArgs()

    private lateinit var binding: FragmentEditarEncuestaBinding
    private lateinit var viewModel: EncuestaViewModel

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

        binding.encuestaViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.inputVeces.text =
            Editable.Factory.getInstance().newEditable(args.veces?.toString() ?: "")

        setupAutoCompleteTextView(
            binding.autoCompleteTextViewPorcion,
            R.array.opcionesPorcion,
            args.porcion
        )

        setupAutoCompleteTextView(
            binding.autoCompleteTextViewFrecuencia,
            R.array.opcionesFrecuencia,
            args.frecuencia
        )

        binding.btnGuardar.setOnClickListener {
            if (validarInputs()) {
                isSaved = true
                editarEncuesta(true)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        return binding.root
    }

    private fun setupAutoCompleteTextView(
        autoCompleteTextView: AutoCompleteTextView,
        arrayResId: Int,
        initialValue: String?
    ) {
        val items = resources.getStringArray(arrayResId)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setText(initialValue, false)
    }

    private fun validarInputs(): Boolean {
        var esValido = true

        esValido = validarCampo(binding.autoCompleteTextViewPorcion, binding.tfPorcion) && esValido
        esValido =
            validarCampo(binding.autoCompleteTextViewFrecuencia, binding.tfFrecuencia) && esValido
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

    override fun onStop() {
        super.onStop()
        if (!isSaved) editarEncuesta(false)
    }

    private fun editarEncuesta(encuestaCompletada: Boolean) {
        val id = args.encuestaId
        val fechaActual = Date().time

        viewModel.editEncuesta(
            Encuesta(
                id,
                fechaActual,
                encuestaCompletada
            )
        )
    }
}
