package unpsjb.ing.tntpm2024.listado

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private val adapterList: EncuestaListAdapter by lazy { EncuestaListAdapter(requireContext()) }
    private lateinit var encuestaViewModel: EncuestaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializar ViewModel (Una sola vez)
        val database = EncuestasDatabase.getInstance(requireContext())
        val factory = EncuestaViewModelFactory(database)
        encuestaViewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]

        // 2. Configurar RecyclerView
        binding.recyclerView.apply {
            adapter = adapterList
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(requireContext(), adapterList))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        // 3. Configurar Spinners
        configurarSpinners()

        // 4. Configurar Acciones del Adaptador
        configurarAccionesAdaptador()

        // 5. Observar Datos
        observarEncuestas()
    }

    private fun configurarSpinners() {
        val statusOptions = listOf("Todas", "Completadas", "Incompletas")
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterSpinner.adapter = statusAdapter

        val zones = listOf("Todas las zonas", "Zona Sur", "Zona Norte", "Zona Oeste")
        val zoneAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, zones)
        zoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.zoneSpinner.adapter = zoneAdapter

        val filterListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.filterSpinner.onItemSelectedListener = filterListener
        binding.zoneSpinner.onItemSelectedListener = filterListener
    }

    private fun updateFilters() {
        val selectedStatus = when (binding.filterSpinner.selectedItemPosition) {
            1 -> true // Completadas
            2 -> false // Incompletas
            else -> null // Todas
        }

        val selectedZone = if (binding.zoneSpinner.selectedItemPosition == 0) null else binding.zoneSpinner.selectedItem.toString()
        adapterList.filterEncuestas(selectedStatus, selectedZone)
    }

    private fun observarEncuestas() {
        // Obtenemos el ID de sesión. Si usamos el bypass, FirebaseAuth devuelve null,
        // por lo que usamos el ID simulado "admin". En producción, podés forzar a que no cargue si es null.
        val idUser = FirebaseAuth.getInstance().currentUser?.uid ?: "admin"

        encuestaViewModel.getEncuestasByUserId(idUser).observe(viewLifecycleOwner) { encuestas ->
            encuestas?.let {
                adapterList.setEncuestas(it)
                // updateFilters se llamará automáticamente porque el setEncuestas interno ahora invoca aplicarFiltros()
            }
        }
    }

    private fun configurarAccionesAdaptador() {
        adapterList.onItemClick = { encuesta ->
            val action = EncuestaListFragmentDirections.actionEncuestalistToDetailFragment(encuesta.encuestaId)
            findNavController().navigate(action)
        }

        adapterList.onItemClickEditEncuesta = {
            // Nota: Aquí pasas datos fijos de prueba. Deberás ajustarlo en un futuro.
            findNavController().navigate(
                EncuestaListFragmentDirections.actionEncuestalistToEditarEncuestaFragment(
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
                .observe(viewLifecycleOwner) { alimentos ->
                    alimentos?.let {
                        encuestaViewModel.uploadEncuesta(encuesta, it,
                            onSuccess = { Toast.makeText(context, "Encuesta subida con éxito", Toast.LENGTH_SHORT).show() },
                            onFailure = { e -> Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show() }
                        )
                    }
                }
        }

        adapterList.onSwipToDeleteCallback = { encuesta ->
            encuestaViewModel.deleteEncuesta(encuesta)
            Toast.makeText(context, "Encuesta borrada", Toast.LENGTH_SHORT).show()
            encuestaViewModel.deleteEncuestaFromFirebase(encuesta,
                onSuccess = {},
                onFailure = { e -> Log.e(TAG, "Error Firebase: ${e.message}") }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evitar fugas de memoria con ViewBinding
    }
}