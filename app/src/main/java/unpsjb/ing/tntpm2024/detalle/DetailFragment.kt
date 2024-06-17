package unpsjb.ing.tntpm2024.detalle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.databinding.FragmentDetailBinding
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModel
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModelFactory

class DetailFragment : Fragment() {

//    val args:DetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailBinding
    private lateinit var encuestaViewModel: EncuestaViewModel
    private lateinit var adapter: AlimentoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: DetailFragmentArgs by navArgs()
        val encuestaId = args.encuestaId

        val application = requireNotNull(this.activity).application
        val database = EncuestasDatabase.getInstance(application)
        val factory = EncuestaViewModelFactory(database)
        encuestaViewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]

        adapter = AlimentoListAdapter(requireContext())
        binding.alimentoRecyclerView.adapter = adapter
        binding.alimentoRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        encuestaViewModel.getAlimentosByEncuestaId(encuestaId).observe(viewLifecycleOwner, Observer { alimentos ->
            alimentos?.let {
                adapter.setAlimentos(it) }
        })
    }
//        val tvDetailTitle = view.findViewById<TextView>(R.id.detailTitle)
//        val tvDetailDesc = view.findViewById<TextView>(R.id.detailDesc)
//            tvDetailTitle.text = args.title
//            tvDetailDesc.text = args.desc



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_detail, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return binding.root
    }
}