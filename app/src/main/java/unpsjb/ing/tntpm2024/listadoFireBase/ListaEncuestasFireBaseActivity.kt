package unpsjb.ing.tntpm2024.listadoFireBase

import ExpandableRecyclerViewAdapter
import android.os.Bundle
import android.util.Log
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
    private lateinit var encuestaViewModel: EncuestaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_encuestas)

        recyclerView = findViewById(R.id.expandable_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val application = requireNotNull(this).application
        val database = EncuestasDatabase.getInstance(application)
        noEncuestasTextView = findViewById(R.id.noEncuestasTextView)
        val factory = EncuestaViewModelFactory(database)
        encuestaViewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]

        encuestaViewModel.obtenerEncuestasDesdeFireBase().observe(this, Observer { encuestasList ->
            Log.i("ListEncuestasActivity", "listado de alimentosEnEncuestas: " + encuestasList.toString())
            if (encuestasList.isEmpty()) {
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


/*        obtenerEncuestasDesdeFirebase { encuestasList ->
            runOnUiThread {
                if (encuestasList.isEmpty()) {
                    noEncuestasTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    noEncuestasTextView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
//                    parentItemList.clear()
//                    parentItemList.addAll(encuestasList)
                    adapter = ExpandableRecyclerViewAdapter(this, encuestasList)
                    adapter.notifyDataSetChanged()
                    recyclerView.adapter = adapter
                }

//                adapter = ExpandableRecyclerViewAdapter(this, encuestas)
//                recyclerView.adapter = adapter
            }
        }*/
    }

/*
metodo andando, necesito pasarlo al repository
private fun obtenerEncuestasDesdeFirebase(callback: (List<AlimentosEnEncuestas>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val encuestasRef = database.getReference("encuestas")

        encuestasRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val encuestasList = mutableListOf<AlimentosEnEncuestas>()
                for (encuestaSnapshot in snapshot.children) {
                    val encuestaData = encuestaSnapshot.child("encuesta").getValue(Encuesta::class.java)
                    val alimentosList = mutableListOf<Alimento>()
                    encuestaSnapshot.child("alimentos").children.forEach { alimentoSnapshot ->
                        val alimento = alimentoSnapshot.getValue(Alimento::class.java)
                        if (alimento != null) {
                            Log.i("ListaEncuestasActivty","alimento recuperado: " + alimento.toString())
                            alimentosList.add(alimento)
                        }
                    }
                    if (encuestaData != null) {
                        val alimentosEnEncuestas = AlimentosEnEncuestas(encuesta = encuestaData, alimentos = alimentosList)
                        encuestasList.add(alimentosEnEncuestas)
                    }
                }
                callback(encuestasList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListaEncuestasActivity, "Error al obtener encuestas: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }*/
}
