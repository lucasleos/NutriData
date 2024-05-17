package unpsjb.ing.tntpm2024.inicio

import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import androidx.databinding.DataBindingUtil
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
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

class InicioFragment : Fragment(), SearchView.OnQueryTextListener {

    val TAG = "InicioFragment"

    //private lateinit var adapterList :  EncuestaListAdapter
    private val adapterList : EncuestaListAdapter by lazy {EncuestaListAdapter(requireContext())}

    private val viewModel: InicioViewModel by viewModels()
    private lateinit var encuestaViewModel: EncuestaViewModel
    private lateinit var dataList: ArrayList<Encuesta>

    private lateinit var searchList: ArrayList<Encuesta>
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.inicio_menu, menu)

        val search = menu?.findItem(R.id.menu_search)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentInicioBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_inicio, container, false
        )

        val recyclerView = binding.recyclerView
        //val adapterList = EncuestaListAdapter(this.requireContext())
       // adapterList = EncuestaListAdapter(this.requireContext())
        recyclerView.adapter = adapterList
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())


        encuestaViewModel = ViewModelProvider(this).get(EncuestaViewModel::class.java)

        encuestaViewModel.todasLasEncuestas
            .observe(
                viewLifecycleOwner,
                Observer {
                        encuestas ->
                    encuestas?.let{ adapterList.setEncuestas(it) }
                }
            )

        recyclerView.setOnClickListener {
            Log.d(TAG, "la cantidad de elementos adapter ${encuestaViewModel.todasLasEncuestas.value}")
        }
        //Log.d(TAG, "la cantidad de elementos adapter ${encuestaViewModel.todasLasEncuestas.value}")
        //getData(encuestaViewModel.todasLasEncuestas.value)

        val fab = binding.botonFlotante

        fab.setOnClickListener {
            // TODO llamar al fragment para crear encuesta. vincular la vista en navigation
            findNavController().navigate(InicioFragmentDirections.actionInicioFragmentToEncuestaFragment())
        }


        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)



        /*searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    dataList.forEach{
                        if (it.dataTitle.toLowerCase(Locale.getDefault()).contains(searchText)) {
                            searchList.add(it)
                        }
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    searchList.clear()
                    searchList.addAll(dataList)
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }

        })*/




        //myAdapter = AdapterClass(searchList)
        //recyclerView.adapter = myAdapter

        adapterList.onItemClick = {
            //Log.d(TAG, "el alimento es ${it.alimento}")
            findNavController().navigate(InicioFragmentDirections.actionInicioFragmentToDetailFragment(
                  //title = it.dataTitle,
                  //desc = it.dataDesc
                title = "Detalle Encuesta",
                desc = "Usted consume ${it.porcion} de ${it.alimento}, ${it.veces} cada ${it.frecuencia} \n" +
                        "estado: ${it.encuestaCompletada}")
            )
        }


        return binding.root
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.d(TAG , "entra al metodo textSubmit")
        if(query != null) {
            Log.d(TAG , "encuentra algo")
            getEncuesta(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        Log.d(TAG , "entra al metodo textChange")
        if(query != null) {
            Log.d(TAG , "encuentra algo")
            getEncuesta(query)
        }
        return true
    }

    private fun getEncuesta(query: String){
        System.out.print("entra al metodo getEncuesta")
        val searchQuery = "%$query"
        encuestaViewModel.getEncuesta(searchQuery).observe(this) { list ->
            list.let { adapterList.setEncuestas(it) }
        }
    }

}