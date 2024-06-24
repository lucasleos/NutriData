package unpsjb.ing.tntpm2024.listadoFireBase

import ExpandableRecyclerViewAdapter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModel
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModelFactory

class ListaEncuestasFireBaseActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpandableRecyclerViewAdapter
    private lateinit var noEncuestasTextView: TextView
    private lateinit var tvTituloEncuestas: TextView
    private lateinit var encuestaViewModel: EncuestaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_encuestas)

        recyclerView = findViewById(R.id.expandable_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val application = requireNotNull(this).application
        val database = EncuestasDatabase.getInstance(application)
        //tvTituloEncuestas = findViewById(R.id.tvTituloEncuestas)

        noEncuestasTextView = findViewById(R.id.noEncuestasTextView)

       // var user = FirebaseAuth.getInstance().currentUser
       // if(user != null)
         //   tvTituloEncuestas.text = "Encuestas del usuario: " + user.email?.substringBefore("@")

        val factory = EncuestaViewModelFactory(database)
        encuestaViewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]

        encuestaViewModel.obtenerEncuestasDesdeFireBase().observe(this, Observer { encuestasList ->
            if (encuestasList.isNullOrEmpty()) {
                // Mostrar un TextView indicando que no hay encuestas
                noEncuestasTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                // Actualiza tu RecyclerView con los datos
                noEncuestasTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter = ExpandableRecyclerViewAdapter(this, encuestasList)
                adapter.notifyDataSetChanged()
                recyclerView.adapter = adapter
            }
        })
    }
}
