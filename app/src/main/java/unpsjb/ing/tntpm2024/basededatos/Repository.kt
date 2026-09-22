package unpsjb.ing.tntpm2024.basededatos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentosEnEncuestas
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.basededatos.entidades.Turno
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

    fun getEncuestasByUserId(userId: String): LiveData<List<Encuesta>> {
        return encuestaDAO.getEncuestasByUserId(userId)
    }

    suspend fun eliminarEncuesta(encuesta: Encuesta) {
        encuestaDAO.deleteEncuesta(encuesta)
    }

    /*suspend fun insertarEncuesta(encuesta: Encuesta) {
        encuestaDAO.insertEncuesta(encuesta)
    }*/

    suspend fun insertarEncuesta(encuesta: Encuesta) {
        val user = FirebaseAuth.getInstance().currentUser
        val idUser = user?.uid
        val emailUser = user?.email

        //if (idUser != null) {
        encuesta.userId = idUser
        encuesta.userEmail = emailUser
        encuestaDAO.insertEncuesta(encuesta)
        //} else {
        //    throw IllegalStateException("No authenticated user")
        // }
    }

    suspend fun cargarEncuesta(encuesta: Encuesta): Long {
        return encuestaDAO.insert(encuesta)
    }

    suspend fun editarEncuesta(encuesta: Encuesta) {
        val user = FirebaseAuth.getInstance().currentUser
        val idUser = user?.uid
        val emailUser = user?.email
        encuesta.userId = idUser
        encuesta.userEmail = emailUser
        encuestaDAO.editEncuesta(encuesta)
    }

    fun getAlimentosByEncuestaId(encuestaId: Int): LiveData<List<AlimentoEncuestaDetalles>> {
        return encuestaDAO.getAlimentosByEncuestaId(encuestaId)
    }

    fun obtenerEncuestasDesdeFirebase(): LiveData<List<AlimentosEnEncuestas>> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        val database = FirebaseDatabase.getInstance()
        val encuestasRef = database.getReference("encuestas")
        val liveData = MutableLiveData<List<AlimentosEnEncuestas>>()

        if(userId != null){
            // volver a cambiar esto, que muestre todas las encuestas que hay subidas
            //encuestasRef.orderByChild("encuesta/userId").equalTo(userId).addValueEventListener(object : ValueEventListener {
//            encuestasRef.addValueEventListener(object : ValueEventListener { // sin orden
            encuestasRef.orderByChild("encuesta/fecha").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val encuestasList = mutableListOf<AlimentosEnEncuestas>()
                    for (encuestaSnapshot in snapshot.children) {
                        val encuestaData =
                            encuestaSnapshot.child("encuesta").getValue(Encuesta::class.java)
                        val alimentosList = mutableListOf<Alimento>()
                        encuestaSnapshot.child("alimentos").children.forEach { alimentoSnapshot ->
                            val alimento = alimentoSnapshot.getValue(Alimento::class.java)
                            if (alimento != null) {
                                alimentosList.add(alimento)
                            }
                        }
                        if (encuestaData != null) {
                            val alimentosEnEncuestas =
                                AlimentosEnEncuestas(encuesta = encuestaData, alimentos = alimentosList)
                            encuestasList.add(alimentosEnEncuestas)
                        }
                    }
                    liveData.value =  encuestasList.reversed()
//                liveData.value = encuestasList

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("EncuestaRepository", "Error al obtener encuestas: ${error.message}")
                }
            })
        }else{
            Log.e("EncuestaRepository", "Usuario no autenticado")
        }
        return liveData
    }

    fun uploadEncuestaToFirebase(
        encuesta: Encuesta,
        alimentoEncuestaDetalles: List<AlimentoEncuestaDetalles>,
        onSuccess: () -> Unit,
        onFailure: (DatabaseError) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return onFailure(DatabaseError.fromException(Exception("Usuario no autenticado")))
        val userEmail = currentUser?.email ?: return onFailure(DatabaseError.fromException(Exception("Usuario no autenticado")))

        val encuestaIdFirebase = "${encuesta.encuestaId}_${encuesta.fecha}"

        // 1. Datos de la encuesta
        val encuestaMap = mutableMapOf<String, Any>(
            "encuesta" to mapOf(
                "encuestaId" to encuesta.encuestaId,
                "fecha" to encuesta.fecha,
                "zona" to encuesta.zona,
                "encuestaCompletada" to encuesta.encuestaCompletada,
                "userId" to userId,
                "userEmail" to userEmail,
                "turnoId" to (encuesta.turnoId ?: ""),
                "subida" to true
            )
        )

        // 2. Alimentos detallados
        val alimentosList = alimentoEncuestaDetalles.map { alimento ->
            mapOf(
                "alimentoId" to alimento.alimentoId,
                "categoria" to alimento.categoria,
                "frecuencia" to alimento.frecuencia,
                "medida" to alimento.medida,
                "nombre" to alimento.nombre,
                "kcal" to alimento.kcal_totales.toDouble(),
                "carbohidratos" to alimento.carbohidratos.toDouble(),
                "proteinas" to alimento.proteinas.toDouble(),
                "grasas" to alimento.grasas.toDouble(),
                "alcohol" to alimento.alcohol.toDouble(),
                "colesterol" to alimento.colesterol.toDouble(),
                "fibra" to alimento.fibra.toDouble(),
                "porcion" to alimento.porcion,
                "veces" to alimento.veces
            )
        }
        encuestaMap["alimentos"] = alimentosList

        // 3. Multi-path update en Firebase
        val childUpdates = mutableMapOf<String, Any>()
        childUpdates["/encuestas/$encuestaIdFirebase"] = encuestaMap

        // Si la encuesta tiene un turno vinculado, lo marcamos como COMPLETADO
        encuesta.turnoId?.takeIf { it.isNotEmpty() }?.let { tId ->
            childUpdates["/turnos/$tId/estado"] = "COMPLETADO"
            childUpdates["/turnos/$tId/encuestaAsociadaId"] = encuestaIdFirebase
        }

        dbRef.updateChildren(childUpdates)
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    encuesta.subida = true
                    encuestaDAO.editEncuesta(encuesta)
                }
                onSuccess()
            }
            .addOnFailureListener { exception -> onFailure(DatabaseError.fromException(exception)) }
    }

    fun deleteEncuestaFromFirebase(encuesta: Encuesta, onSuccess: () -> Unit, onFailure: (DatabaseError) -> Unit) {

        Log.i("Repository", ""+ encuesta.encuestaId + "" + encuesta.fecha)

        dbRef.child("encuestas").child("${encuesta.encuestaId}_${encuesta.fecha}")
            .removeValue()
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    encuesta.subida = false
                    encuestaDAO.editEncuesta(encuesta)
                }
                onSuccess()
            }
            .addOnFailureListener { exception -> onFailure(DatabaseError.fromException(exception)) }
    }

    fun obtenerTurnosSolicitados(): LiveData<List<Turno>> {
        val liveData = MutableLiveData<List<Turno>>()
        val turnosRef = FirebaseDatabase.getInstance().getReference("turnos")

        // Filtramos solo los turnos que están pendientes de asignación
        turnosRef.orderByChild("estado").equalTo("SOLICITADO")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaTurnos = mutableListOf<Turno>()
                    for (turnoSnapshot in snapshot.children) {
                        val turno = turnoSnapshot.getValue(Turno::class.java)
                        if (turno != null) {
                            listaTurnos.add(turno)
                        }
                    }
                    // Los mostramos ordenados por fecha de solicitud (más antiguos primero o viceversa)
                    liveData.value = listaTurnos
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Repository", "Error al obtener turnos: ${error.message}")
                }
            })

        return liveData
    }

    fun asignarTurno(
        turnoId: String,
        asignacion: unpsjb.ing.tntpm2024.basededatos.entidades.AsignacionTurno,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val turnoRef = FirebaseDatabase.getInstance().getReference("turnos").child(turnoId)

        val updates = mapOf(
            "estado" to "ASIGNADO",
            "asignacion" to mapOf(
                "fecha" to asignacion.fecha,
                "hora" to asignacion.hora,
                "lugar" to asignacion.lugar,
                "indicaciones" to asignacion.indicaciones
            )
        )

        turnoRef.updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { error -> onFailure(error) }
    }

    fun obtenerTurnosAsignados(): LiveData<List<Turno>> {
        val liveData = MutableLiveData<List<Turno>>()
        val turnosRef = FirebaseDatabase.getInstance().getReference("turnos")

        turnosRef.orderByChild("estado").equalTo("ASIGNADO")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lista = mutableListOf<Turno>()
                    for (child in snapshot.children) {
                        val turno = child.getValue(Turno::class.java)
                        if (turno != null) {
                            lista.add(turno)
                        }
                    }
                    liveData.value = lista
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Repository", "Error al leer turnos asignados: ${error.message}")
                }
            })

        return liveData
    }

}
