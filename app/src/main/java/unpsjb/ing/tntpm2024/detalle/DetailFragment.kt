package unpsjb.ing.tntpm2024.detalle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.Repository
import unpsjb.ing.tntpm2024.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: DetailFragmentArgs by navArgs()
        val encuestaId = args.encuestaId

        val database = EncuestasDatabase.getInstance(requireContext().applicationContext)
        val repository = Repository(database.encuestaDAO)
        val factory = DetailViewModelFactory(repository, encuestaId)
        val viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = AlimentoEncuestaListAdapter()
        binding.alimentoRecyclerView.adapter = adapter
        binding.alimentoRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.alimentos.observe(viewLifecycleOwner) { alimentos ->
            adapter.submitList(alimentos)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
