package unpsjb.ing.tntpm2024.listado

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.databinding.FragmentInicioBinding
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModel
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModelFactory
import unpsjb.ing.tntpm2024.util.SwipeToDeleteCallback

class EncuestaListFragment : Fragment() {

    val TAG = "EncuestaListFragment"

    private lateinit var filterSpinner: Spinner
    private lateinit var zoneSpinner: Spinner

    private val adapterList: EncuestaListAdapter by lazy { EncuestaListAdapter(requireContext()) }

    private lateinit var encuestaViewModel: EncuestaViewModel
//    private lateinit var searchView: SearchView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Inicializar los Spinners
        val statusOptions = listOf("Todas", "Completadas", "Incompletas")
        val statusAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = statusAdapter


        val zones = listOf("Todas las zonas", "Zona Sur", "Zona Norte", "Zona Oeste")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, zones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        zoneSpinner.adapter = adapter

        encuestaViewModel = ViewModelProvider(this)[EncuestaViewModel::class.java]
        filterSpinner = view.findViewById(R.id.filterSpinner)
        zoneSpinner = view.findViewById(R.id.zoneSpinner)
        // Configuración del Spinner de filtro
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        zoneSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        val idUser = FirebaseAuth.getInstance().currentUser?.uid
        //if (idUser != null) {
        if (idUser != null) {
            encuestaViewModel.getEncuestasByUserId(idUser).observe(viewLifecycleOwner) { encuestas ->
                encuestas?.let {
                    adapterList.setEncuestas(it)
                    updateFilters() // Aplicar filtros actuales
                }
            }
        }
    }

    private fun updateFilters() {
        val selectedStatus = when (filterSpinner.selectedItemPosition) {
            0 -> null // Todas las encuestas
            1 -> true // Encuestas completadas
            2 -> false // Encuestas incompletas
            else -> null
        }

        val selectedZone =
            if (zoneSpinner.selectedItem == null || zoneSpinner.selectedItemPosition == 0) {
                null // Todas las zonas
            } else {
                zoneSpinner.selectedItem.toString()
            }

        adapterList.filterEncuestas(selectedStatus, selectedZone)
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

        filterSpinner = binding.filterSpinner
        zoneSpinner = binding.zoneSpinner
//        searchView = binding.searchView
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapterList
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        //val itemTouchHelper = ItemTouchHelper(SwipToDeleteCallback(adapterList))
        //itemTouchHelper.attachToRecyclerView(recyclerView)

//        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapterList))
//        itemTouchHelper.attachToRecyclerView(recyclerView)


        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(requireContext(), adapterList))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val database = EncuestasDatabase.getInstance(requireContext())
        val factory = EncuestaViewModelFactory(database)
        encuestaViewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        adapterList.onItemClick = { encuesta ->
            val action =
                EncuestaListFragmentDirections.actionEncuestalistToDetailFragment(encuesta.encuestaId)
            findNavController().navigate(action)
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

            encuestaViewModel.getAlimentosByEncuestaId(encuesta.encuestaId)
                .observe(viewLifecycleOwner, Observer { alimentosEncuesta ->
                    alimentosEncuesta?.let { alimentosEncuestaDetalles ->

                        encuestaViewModel.uploadEncuesta(encuesta, alimentosEncuestaDetalles,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Encuesta subida con éxito",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFailure = { e ->
                                Toast.makeText(
                                    context,
                                    "Error al subir encuesta: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e(TAG, "Error al subir encuesta: ${e.message}")
                            }
                        )
                    }
                })
        }

        adapterList.onSwipToDeleteCallback = { encuesta ->

            encuestaViewModel.deleteEncuesta(encuesta)
            Toast.makeText(context, "Encuesta borrada", Toast.LENGTH_SHORT).show()
           // elimina tambien de firebase
            encuestaViewModel.deleteEncuestaFromFirebase(encuesta, onSuccess = {
            },
                onFailure = { e ->
                    Log.e(TAG, "Error al eliminar encuesta Firebase: ${e.message}")
                })
        }

        return binding.root
    }


}