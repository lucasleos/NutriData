package unpsjb.ing.tntpm2024

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.firebase.FirebaseApp
import unpsjb.ing.tnt.listado.listado.EncuestaListAdapter
import unpsjb.ing.tntpm2024.databinding.ActivityMainBinding
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModel
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private val adapterList: EncuestaListAdapter by lazy { EncuestaListAdapter(this) }
    private lateinit var encuestaViewModel: EncuestaViewModel

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // Colocado antes de configurar los observadores

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Obtener la instancia de la base de datos desde AndroidApp
        val database = (application as AndroidApp).database

        // Crear el ViewModel utilizando el ViewModelFactory
        val viewModelFactory = EncuestaViewModelFactory(database)
        encuestaViewModel =
            ViewModelProvider(this, viewModelFactory)[EncuestaViewModel::class.java]

        encuestaViewModel.todasLasEncuestas.observe(this) { encuestas ->
            encuestas?.let { adapterList.setEncuestas(it) }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.theNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}
