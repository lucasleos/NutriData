package unpsjb.ing.tntpm2024

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import unpsjb.ing.tntpm2024.databinding.ActivityMainBinding
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModel
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModelFactory
import unpsjb.ing.tntpm2024.listado.EncuestaListAdapter
import unpsjb.ing.tntpm2024.listadoFireBase.ListaEncuestasFireBaseActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private val adapterList: EncuestaListAdapter by lazy { EncuestaListAdapter(this) }
    private lateinit var encuestaViewModel: EncuestaViewModel
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) // Colocado antes de configurar los observadores

        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.tool_bar)
        drawerLayout = findViewById(R.id.drawer_layout)

        // Configura la toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_lista_encuestas -> {
                    val intent = Intent(this, ListaEncuestasFireBaseActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawers()
            true
        }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.theNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }


}
