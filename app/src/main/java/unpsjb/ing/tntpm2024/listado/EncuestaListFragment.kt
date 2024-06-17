package unpsjb.ing.tntpm2024.listado

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.databinding.FragmentInicioBinding
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModel
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModelFactory
import unpsjb.ing.tntpm2024.util.SwipToDeleteCallback

class EncuestaListFragment : Fragment() {

    val TAG = "EncuestaListFragment"

    private val adapterList: EncuestaListAdapter by lazy { EncuestaListAdapter(requireContext()) }

    private lateinit var encuestaViewModel: EncuestaViewModel
    private lateinit var searchView: SearchView

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

        val itemTouchHelper = ItemTouchHelper(SwipToDeleteCallback(adapterList))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val database = EncuestasDatabase.getInstance(requireContext())
        val factory = EncuestaViewModelFactory(database)
        encuestaViewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]

        encuestaViewModel.todasLasEncuestas
            .observe(
                viewLifecycleOwner,
                Observer { encuestas ->
                    encuestas?.let { adapterList.setEncuestas(it) }
                }
            )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        adapterList.onItemClick = {
            val encuestaId = it.encuestaId
            val porcion = "100 ml"
            val alimento = "Yogur bebible"
            val veces = "5"
            val frecuencia = "Dia"
            val estado = if (it.encuestaCompletada) "Completada" else "Incompleta"

            val title = "Detalle Encuesta $encuestaId"
            val desc = """
        Usted consume $porcion de $alimento, $veces cada $frecuencia
        Estado: $estado
    """.trimIndent()

            findNavController().navigate(
                EncuestaListFragmentDirections.actionEncuestalistToDetailFragment(
                    title = title,
                    desc = desc
                )
            )
        }

        adapterList.onItemClickEditEncuesta = {
            findNavController().navigate(
                EncuestaListFragmentDirections.actionEncuestalistToEditarEncuestaFragment(
                    // set frecuencia etc por parametros
                    encuestaId = it.encuestaId,
                    aliemento = "Yogur bebible",
                    frecuencia = "Dia",
                    porcion = "100 ml",
                    veces = "5",
                    encuestaCompletada = it.encuestaCompletada,
                    zona = it.zona
                )
            )
        }

        adapterList.onItemClickUploadInCloud = { encuesta ->
            encuestaViewModel.uploadEncuesta(encuesta,
                onSuccess = {
                    Toast.makeText(context, "Encuesta subida con éxito", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Toast.makeText(
                        context,
                        "Error al subir encuesta: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i(TAG, "Error al subir encuesta: ${e.message}")
                }
            )
        }

        adapterList.onSwipToDeleteCallback = {
            val encuesta = Encuesta(
                it.encuestaId,
                123455,
                true,
                zona = it.zona
            )

            encuestaViewModel.deleteEncuesta(encuesta)
            Toast.makeText(context, "Encuesta borrada", Toast.LENGTH_SHORT).show()
            encuestaViewModel.deleteEncuestaFromFirebase(encuesta, onSuccess = {
//                Toast.makeText(context, "Encuesta Firebase eliminada con éxito", Toast.LENGTH_SHORT).show()
            },
                onFailure = { e ->
//                    Toast.makeText(context, "Error al eliminar encuesta: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error al eliminar encuesta Firebase: ${e.message}")
                })
        }

        return binding.root
    }


}