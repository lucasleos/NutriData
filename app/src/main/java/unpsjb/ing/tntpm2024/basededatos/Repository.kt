package unpsjb.ing.tntpm2024.basededatos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentosEnEncuestas
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

    fun obtenerEncuestasDesdeFirebase(): LiveData<List<AlimentosEnEncuestas>> {
        val database = FirebaseDatabase.getInstance()
        val encuestasRef = database.getReference("encuestas")
        val liveData = MutableLiveData<List<AlimentosEnEncuestas>>()

        encuestasRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val encuestasList = mutableListOf<AlimentosEnEncuestas>()
                for (encuestaSnapshot in snapshot.children) {
                    val encuestaData = encuestaSnapshot.child("encuesta").getValue(Encuesta::class.java)
                    val alimentosList = mutableListOf<Alimento>()
                    encuestaSnapshot.child("alimentos").children.forEach { alimentoSnapshot ->
                        val alimento = alimentoSnapshot.getValue(Alimento::class.java)
                        if (alimento != null) {
                            Log.i("EncuestaRepository", "alimento recuperado: " + alimento.toString())
                            alimentosList.add(alimento)
                        }
                    }
                    if (encuestaData != null) {
                        val alimentosEnEncuestas = AlimentosEnEncuestas(encuesta = encuestaData, alimentos = alimentosList)
                        Log.i("EncuestaRepository", "obtener encuestas en repository: " + alimentosEnEncuestas.toString())
                        encuestasList.add(alimentosEnEncuestas)
                    }
                }
                liveData.value = encuestasList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("EncuestaRepository", "Error al obtener encuestas: ${error.message}")
            }
        })

        return liveData
    }

    fun uploadEncuestaToFirebase(
        encuesta: Encuesta,
        alimentoEncuestaDetalles: List<AlimentoEncuestaDetalles>,
        onSuccess: () -> Unit,
        onFailure: (DatabaseError) -> Unit
    ) {
        // Crear un mapa para los detalles de la encuesta
        val encuestaMap = mutableMapOf<String, Any>(
            "encuesta" to mapOf(
                "encuestaId" to encuesta.encuestaId,
                "fecha" to encuesta.fecha,
                "zona" to encuesta.zona,
                "encuestaCompletada" to encuesta.encuestaCompletada
            )
        )

        // Crear una lista de mapas para los detalles de los alimentos
        val alimentosList = alimentoEncuestaDetalles.map { alimento ->
            mapOf(
                "alimentoId" to alimento.alimentoId,
                "categoria" to alimento.categoria,
                "frecuencia" to alimento.frecuencia,
                "medida" to alimento.medida,
                "nombre" to alimento.nombre,
                "porcentajeGraso" to alimento.porcentaje_graso.toDouble(),
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
