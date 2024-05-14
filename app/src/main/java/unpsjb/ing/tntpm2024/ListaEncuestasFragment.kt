package unpsjb.ing.tntpm2024

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tntpm2024.databinding.FragmentListaEncuestasBinding
import java.util.Locale

class ListaEncuestasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>
    lateinit var imageList:Array<Int>
    lateinit var titleList:Array<String>
    lateinit var descList: Array<String>
    lateinit var detailImageList: Array<Int>
    private lateinit var myAdapter: AdapterClass
    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<DataClass>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    //    val binding: FragmentListaEncuestasBinding = DataBindingUtil.inflate(
      //      inflater, R.layout.fragment_lista_encuestas, container, false
       // )

        imageList = arrayOf(
            R.drawable.ic_list,
            R.drawable.ic_list,
            R.drawable.ic_list,
            R.drawable.ic_list,
            R.drawable.ic_list)

        titleList = arrayOf(
            "Encuesta 1",
            "Encuesta 2",
            "Encuesta 3",
            "Encuesta 4",
            "Encuesta 5")

        descList = arrayOf(
            getString(R.string.listview),
            getString(R.string.listview),
            getString(R.string.listview),
            getString(R.string.listview),
            getString(R.string.listview))

        detailImageList = arrayOf(
            R.drawable.list_detail,
            R.drawable.list_detail,
            R.drawable.list_detail,
            R.drawable.list_detail,
            R.drawable.list_detail)

        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.search)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf<DataClass>()
        searchList = arrayListOf<DataClass>()
        getData()

        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
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

        })

        myAdapter = AdapterClass(searchList)
        recyclerView.adapter = myAdapter

        myAdapter.onItemClick = {
            //hacer el fragment de detail activity
            // val intent = Intent(this, DetailActivity::class.java)
            //intent.putExtra("android", it)
            //startActivity(intent)

            findNavController().navigate(ListaEncuestasFragmentDirections.actionListaEncuestasFragmentToDetailFragment(
                title = it.dataTitle,
                desc = it.dataDesc))

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        return inflater.inflate(R.layout.fragment_lista_encuestas, container, false)
    }

    private fun getData(){
        for (i in imageList.indices){
            val dataClass = DataClass(imageList[i], titleList[i], descList[i], detailImageList[i])
            dataList.add(dataClass)
        }
        searchList.addAll(dataList)
        recyclerView.adapter = AdapterClass(searchList)
    }
}