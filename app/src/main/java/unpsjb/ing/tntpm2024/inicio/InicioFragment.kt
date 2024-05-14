package unpsjb.ing.tntpm2024.inicio

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.databinding.FragmentInicioBinding

class InicioFragment : Fragment() {

    private val viewModel: InicioViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentInicioBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_inicio, container, false
        )

        //ejecuto una accion
        binding.btnRealizarEncuesta.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_encuestaFragment)
        }

        binding.btnListaEncuestas.setOnClickListener{
            findNavController().navigate(R.id.action_inicioFragment_to_listaEncuestasFragment)
        }

        return binding.root

    }
}