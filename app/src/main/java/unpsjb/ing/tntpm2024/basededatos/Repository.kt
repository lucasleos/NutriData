package unpsjb.ing.tntpm2024.basededatos

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.detalle.AlimentoEncuestaDetalles

class Repository(private val encuestaDAO: EncuestaDAO) {

    val allEncuestas: LiveData<List<Encuesta>> = encuestaDAO.getEncuestas()
    private val database = FirebaseDatabase.getInstance()
    private val dbRef = database.reference


    fun getEncuesta(searchQuery: String): LiveData<List<Encuesta>> {
        return encuestaDAO.getEncuesta(searchQuery)
    }

    fun getEncuestaById(id: Int): LiveData<Encuesta> {
        return encuestaDAO.getEncuestasById(id)
    }

    fun eliminarEncuesta(encuesta: Encuesta) {
        encuestaDAO.deleteEncuesta(encuesta)
    }

    suspend fun insertarEncuesta(encuesta: Encuesta) {
        encuestaDAO.insertEncuesta(encuesta)
    }

    suspend fun cargarEncuesta(encuesta: Encuesta): Long {
        return encuestaDAO.insert(encuesta)
    }

    fun editarEncuesta(encuesta: Encuesta) {
        encuestaDAO.editEncuesta(encuesta)
    }

    fun getAlimentosByEncuestaId(encuestaId: Int): LiveData<List<AlimentoEncuestaDetalles>> {
        return encuestaDAO.getAlimentosByEncuestaId(encuestaId)
    }
//    fun uploadEncuestaToFirebase(encuesta: Encuesta, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
//
//        val db = FirebaseFirestore.getInstance()
//        Log.i("Repository", "instancia db" + db.collection("encuestas").toString())
//        db.collection("encuestas")
//            .add(encuesta)
//            .addOnSuccessListener {
//                Log.i("Repository", "encuesta se guardo con exito")
//                onSuccess()
//            }
//            .addOnFailureListener { e ->
//                Log.i("Repository", "encuesta fallo al guardar")
//                onFailure(e)
//            }
//    }


    fun uploadEncuestaToFirebase(
        encuesta: Encuesta,
        alimentoEncuestaDetalles: List<AlimentoEncuestaDetalles>,
        onSuccess: () -> Unit,
        onFailure: (DatabaseError) -> Unit
    ) { // Crear un mapa para los detalles de la encuesta
        val encuestaMap = mutableMapOf<String, Any>(
            "encuestaId" to encuesta.encuestaId,
            "fecha" to encuesta.fecha,
            "zona" to encuesta.zona,
            "encuestaCompletada" to encuesta.encuestaCompletada
        )

        // Crear una lista de mapas para los detalles de los alimentos
        val alimentosList = alimentoEncuestaDetalles.mapIndexed { index, alimento ->
            mapOf(
                "alimentoId" to alimento.alimentoId,
                "categoria" to alimento.categoria,
                "frecuencia" to alimento.frecuencia,
                "medida" to alimento.medida,
                "nombre" to alimento.nombre,
                "porcentaje_graso" to alimento.porcentaje_graso,
                "porcion" to alimento.porcion,
                "veces" to alimento.veces
            )
        }

        // Añadir la lista de alimentos al mapa de la encuesta
        encuestaMap["alimentos"] = alimentosList

        // Subir los datos a Firebase
        dbRef.child("encuestas").child("${encuesta.encuestaId}_${encuesta.fecha}")
            .setValue(encuestaMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(DatabaseError.fromException(exception)) }
    }

    fun deleteEncuestaFromFirebase(encuesta: Encuesta, onSuccess: () -> Unit, onFailure: (DatabaseError) -> Unit) {

        Log.i("Repository", ""+ encuesta.encuestaId + "" + encuesta.fecha)

        dbRef.child("encuestas").child("${encuesta.encuestaId}_${encuesta.fecha}")
            .removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(DatabaseError.fromException(exception)) }
    }
}
