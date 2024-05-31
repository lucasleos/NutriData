package unpsjb.ing.tntpm2024

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import unpsjb.ing.tnt.listado.listado.EncuestaListAdapter
import unpsjb.ing.tntpm2024.databinding.ActivityMainBinding
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModel

class MainActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val adapterList : EncuestaListAdapter by lazy {EncuestaListAdapter(this)}
    private lateinit var encuestaViewModel: EncuestaViewModel

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        // Inicializar Firebase
        val database = Firebase.database
        val auth = Firebase.auth

        encuestaViewModel = ViewModelProvider(this).get(EncuestaViewModel::class.java)

        encuestaViewModel.todasLasEncuestas
            .observe(
                this,
                Observer {
                        encuestas ->
                    encuestas?.let{ adapterList.setEncuestas(it) }
                }
            )

        setContentView(view)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.theNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
        //return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    }
}