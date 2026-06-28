package unpsjb.ing.tntpm2024.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.databinding.FragmentInicio2Binding

class InicioFragment : Fragment() {

    private lateinit var binding: FragmentInicio2Binding
    private val viewModel: InicioViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inicio2, container, false)
        binding.inicioViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        configurarBotones()

        return binding.root
    }

    private fun configurarBotones() {
        binding.btnEncuesta.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_mapsFragment)
        }

        binding.btnEncuestas.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_encuestalist)
        }

        binding.btnEstadistica.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_estadisticaFragment)
        }
    }
}