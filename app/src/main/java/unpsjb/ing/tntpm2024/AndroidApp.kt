package unpsjb.ing.tntpm2024

import android.app.Application
import androidx.room.Room
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase

class AndroidApp : Application() {
    lateinit var database: EncuestasDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            EncuestasDatabase::class.java,
            "database"
        ).build()
    }
}