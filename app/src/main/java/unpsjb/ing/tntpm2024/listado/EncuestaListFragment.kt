package unpsjb.ing.tntpm2024.listado

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tnt.ligadeportiva.listado.listado.EncuestaListAdapter
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.Encuesta
import unpsjb.ing.tntpm2024.databinding.FragmentInicioBinding
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModel
import unpsjb.ing.tntpm2024.inicio.InicioViewModel

class EncuestaListFragment : Fragment() {

    val TAG = "EncuestaListFragment"

    private val adapterList: EncuestaListAdapter by lazy { EncuestaListAdapter(requireContext()) }

    private val viewModel: InicioViewModel by viewModels()
    private lateinit var encuestaViewModel: EncuestaViewModel
    private lateinit var dataList: ArrayList<Encuesta>

    private lateinit var searchList: ArrayList<Encuesta>
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchQuery = "%$newText%"
                encuestaViewModel.getEncuesta(searchQuery).observe(viewLifecycleOwner) { list ->
                    list.let { adapterList.setEncuestas(it) }
                }
                return false
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView Inicio Fragment")

        val binding: FragmentInicioBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_inicio, container, false
        )

        searchView = binding.searchView
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapterList
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        encuestaViewModel = ViewModelProvider(this).get(EncuestaViewModel::class.java)

        encuestaViewModel.todasLasEncuestas
            .observe(
                viewLifecycleOwner,
                Observer { encuestas ->
                    encuestas?.let { adapterList.setEncuestas(it) }
                }
            )

        val fab = binding.botonFlotante

        fab.setOnClickListener {
            findNavController().navigate(EncuestaListFragmentDirections.actionEncuestalistToEncuestaFragment())
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        adapterList.onItemClick = {
            var asd = it.encuestaCompletada
            //Log.d(TAG, "el alimento es ${it.alimento}")
            findNavController().navigate(
                EncuestaListFragmentDirections.actionEncuestalistToDetailFragment(
                    //title = it.dataTitle,
                    //desc = it.dataDesc
                    title = "Detalle Encuesta",
                    desc = "Usted consume ${it.porcion} de ${it.alimento}, ${it.veces} cada ${it.frecuencia} \n" +
                            //"estado: ${it.encuestaCompletada}")

                            "Estado: " + if (it.encuestaCompletada) "Completada" else "Incompleta"
                )
            )
        }

        return binding.root
    }

}