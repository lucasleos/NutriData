package unpsjb.ing.tntpm2024

import android.app.Application
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
class AndroidApp : Application() {
    val database: EncuestasDatabase by lazy {
        EncuestasDatabase.getInstance(applicationContext)
    }
}