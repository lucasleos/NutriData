package unpsjb.ing.tntpm2024.basededatos.encuestas

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import unpsjb.ing.tntpm2024.basededatos.encuestas.Encuesta
import unpsjb.ing.tntpm2024.basededatos.encuestas.EncuestaDAO

class RepositorioDeEncuestas(private val encuestaDAO: EncuestaDAO) {

    fun getEncuesta(searchQuery: String) : Flow<List<Encuesta>> {
        return encuestaDAO.getEncuesta(searchQuery)
    }

    // LiveData observada va a notificar a sus observadores cuando los datos cambien.
    val todasLasEncuestas: LiveData<List<Encuesta>> = encuestaDAO.getEncuestas()

    // La inserción se realiza en un hilo que no sea UI, ya que sino la aplicación se bloqueará. Para
    // informar a los métodos de llamada debemos realizar la inserción en una función suspend.
    // De esta manera, Room garantiza que no se realicen operaciones de larga ejecución
    // en el hilo principal, bloqueando la interfaz de usuario.
    suspend fun insertar(encuesta: Encuesta){
        encuestaDAO.insertar(encuesta)
    }

    suspend fun insertarEncuestaIncompleta(encuestaId: Int, aliemento: String?, porcion: String?, frecuencia: String?, veces: String?, encuestaCompletada: Boolean) {
        if (aliemento != null) {
            if (porcion != null) {
                if (frecuencia != null) {
                    if (veces != null) {
                        encuestaDAO.insertarEncustaIncompleta(encuestaId, aliemento, porcion, frecuencia, veces, encuestaCompletada)
                    }
                }
            }
        }
    }

}