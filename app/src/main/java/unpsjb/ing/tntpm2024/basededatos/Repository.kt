package unpsjb.ing.tntpm2024.basededatos

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta

class Repository(private val encuestaDAO: EncuestaDAO) {

    val allEncuestas: LiveData<List<Encuesta>> = encuestaDAO.getEncuestas()
    private val database = FirebaseDatabase.getInstance()
    private val dbRef = database.reference


    fun getEncuesta(searchQuery: String): LiveData<List<Encuesta>> {
        return encuestaDAO.getEncuesta(searchQuery)
    }

    fun eliminarEncuesta(encuesta: Encuesta) {
        encuestaDAO.deleteEncuesta(encuesta)
    }

    suspend fun insertarEncuesta(encuesta: Encuesta) {
        encuestaDAO.insertEncuesta(encuesta)
    }

    fun editarEncuesta(encuesta: Encuesta) {
        encuestaDAO.editEncuesta(encuesta)
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

    fun uploadEncuestaToFirebase(encuesta: Encuesta, onSuccess: () -> Unit, onFailure: (DatabaseError) -> Unit) {
        dbRef.child("encuestas/encuestaId" + encuesta.encuestaId + "" + encuesta.fecha)
            .setValue(encuesta)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(DatabaseError.fromException(exception)) }
    }

    fun deleteEncuestaFromFirebase(encuesta: Encuesta, onSuccess: () -> Unit, onFailure: (DatabaseError) -> Unit) {
        dbRef.child("encuestas/encuestaId" + encuesta.encuestaId + "" + encuesta.fecha)
            .removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(DatabaseError.fromException(exception)) }
    }
}
