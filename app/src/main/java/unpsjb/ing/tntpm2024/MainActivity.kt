package unpsjb.ing.tntpm2024

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import unpsjb.ing.tntpm2024.databinding.ActivityMainBinding
import unpsjb.ing.tntpm2024.listadoFireBase.ListaEncuestasFireBaseActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Uso puro de ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)

        // 2. Configuración moderna del Navigation Component
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.theNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // Enlaza el DrawerLayout y la Toolbar con el grafo de navegación automáticamente
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.inicioFragment, R.id.encuestalist),
            binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // Control de visibilidad del Drawer y bloqueo en pantallas específicas
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment) {
                binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            actualizarVisibilidadMenu(destination.id)
        }

        // 3. Manejo de eventos del menú lateral
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_lista_encuestas -> {
                    startActivity(Intent(this, ListaEncuestasFireBaseActivity::class.java))
                }
                R.id.nav_logout -> logout()
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        actualizarVisibilidadMenu()
    }

    override fun onStart() {
        super.onStart()
        // Asegura que el menú se actualice si el usuario vuelve a la app
        actualizarVisibilidadMenu()
    }

    private fun actualizarVisibilidadMenu(currentDestinationId: Int? = null) {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val menu = binding.navView.menu

        // Consideramos "autenticado" si hay un usuario de Firebase 
        // O si ya navegamos fuera del Login (indicativo de que se usó el bypass "admin")
        val destinationId = currentDestinationId ?: navController.currentDestination?.id
        val isNotAtLogin = destinationId != R.id.loginFragment && destinationId != null
        val showMenu = user != null || isNotAtLogin

        menu.findItem(R.id.nav_logout)?.isVisible = showMenu
        menu.findItem(R.id.nav_lista_encuestas)?.isVisible = showMenu
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    // 4. Delega el botón "Atrás" / "Hamburguesa" al NavController
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}