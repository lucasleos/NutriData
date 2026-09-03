package unpsjb.ing.tntpm2024.listadoFireBase

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.databinding.ActivityListaEncuestasBinding
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModel
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModelFactory

class ListaEncuestasFireBaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaEncuestasBinding
    private lateinit var adapter: ExpandableRecyclerViewAdapter
    private lateinit var encuestaViewModel: EncuestaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaEncuestasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar Navigation
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.expandableRecyclerView.layoutManager = LinearLayoutManager(this)

        val application = requireNotNull(this).application
        val database = EncuestasDatabase.getInstance(application)
        val factory = EncuestaViewModelFactory(database)
        encuestaViewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]

        encuestaViewModel.obtenerEncuestasDesdeFireBase().observe(this) { encuestasList ->
            if (encuestasList.isNullOrEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.expandableRecyclerView.visibility = View.GONE
                binding.tvTituloEncuestas.text = "No hay encuestas en Firebase"
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.expandableRecyclerView.visibility = View.VISIBLE
                val count = encuestasList.size
                binding.tvTituloEncuestas.text = if (count == 1) {
                    "1 encuesta sincronizada en Firebase"
                } else {
                    "$count encuestas sincronizadas en Firebase"
                }

                adapter = ExpandableRecyclerViewAdapter(this, encuestasList)
                binding.expandableRecyclerView.adapter = adapter
            }
        }
    }
}
