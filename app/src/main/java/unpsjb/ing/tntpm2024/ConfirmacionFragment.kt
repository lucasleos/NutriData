package unpsjb.ing.tntpm2024

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import unpsjb.ing.tntpm2024.Inicio.InicioViewModel
import unpsjb.ing.tntpm2024.databinding.FragmentConfirmacionBinding
import unpsjb.ing.tntpm2024.databinding.FragmentEncuestaBinding

class ConfirmacionFragment : Fragment() {

    private val viewModel: InicioViewModel by viewModels()

    val args : ConfirmacionFragmentArgs by navArgs()

    // para recuperar parametros del fragmento anterior
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val valorPorcion = args.porcion
        val valorFrecuencia = args.frecuencia
        val valorVeces = args.veces

        val tvRespuesta: TextView = view.findViewById(R.id.tvRespuesta)
        tvRespuesta.text = "Usted consume " + valorPorcion + " de yogurt bebile entero, "+
                valorVeces + " veces por " + valorFrecuencia.lowercase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentConfirmacionBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_confirmacion, container, false
        )

        binding.btnAceptar.setOnClickListener {
            findNavController().navigate(R.id.action_confirmacionFragment_to_inicioFragment)
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirmacion, container, false)
    }
}