package unpsjb.ing.tntpm2024.inicio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import unpsjb.ing.tntpm2024.MainActivity
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.databinding.FragmentInicio2Binding
import unpsjb.ing.tntpm2024.listadoFireBase.ListaEncuestasFireBaseActivity

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
        binding.cardNuevaEncuesta.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_mapsFragment)
        }

        binding.cardEncuestas.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_encuestalist)
        }

        binding.cardEstadisticas.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_estadisticaFragment)
        }

        binding.cardFirebaseEncuestas.setOnClickListener {
            startActivity(Intent(requireContext(), ListaEncuestasFireBaseActivity::class.java))
        }

        binding.cardLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(requireActivity(), gso).signOut().addOnCompleteListener {
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
