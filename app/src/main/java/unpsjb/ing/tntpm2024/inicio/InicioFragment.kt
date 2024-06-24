package unpsjb.ing.tntpm2024.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.databinding.FragmentInicio2Binding

class InicioFragment : Fragment() {

    private lateinit var binding: FragmentInicio2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inicio2, container, false)

        var user = FirebaseAuth.getInstance().currentUser
        if(user != null)
            binding.tvTitulo.text = "Bienvenido: " + user.email?.substringBefore("@")



        binding.btnEncuesta.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_mapsFragment)
        }

        binding.btnEncuestas.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_encuestalist)
        }

        binding.btnEstadistica.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_estadisticaFragment)
        }

        return binding.root
    }

}