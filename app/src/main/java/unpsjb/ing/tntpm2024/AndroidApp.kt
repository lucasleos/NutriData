package unpsjb.ing.tntpm2024

import android.app.Application
import androidx.room.Room
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase

class AndroidApp : Application() {

    // Inicialización perezosa (lazy) para no bloquear el inicio de la app
    val database: EncuestasDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            EncuestasDatabase::class.java,
            "database"
        ).build()
    }
}