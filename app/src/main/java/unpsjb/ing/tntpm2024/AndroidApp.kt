package unpsjb.ing.tntpm2024

import android.app.Application
import androidx.databinding.DataBindingUtil.setContentView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.database.database
import unpsjb.ing.tnt.listado.listado.EncuestaListAdapter
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.databinding.ActivityMainBinding
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModel

class AndroidApp : Application() {

    val TAG = "MainActivity"
    private lateinit var database: EncuestasDatabase
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private val adapterList: EncuestaListAdapter by lazy { EncuestaListAdapter(this) }
    private lateinit var encuestaViewModel: EncuestaViewModel

    override fun onCreate() {
        super.onCreate()
        database = Room
            .databaseBuilder(applicationContext, EncuestasDatabase::class.java, "database")
            .build()

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        val database = Firebase.database
        val auth = Firebase.auth

        setContentView(view)

    }
}