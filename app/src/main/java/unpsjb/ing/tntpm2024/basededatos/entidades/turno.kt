package unpsjb.ing.tntpm2024.basededatos.entidades
data class AsignacionTurno(
    val fecha: String = "",
    val hora: String = "",
    val lugar: String = "",
    val indicaciones: String = ""
)

data class Turno(
    var turnoId: String = "",
    val voluntarioId: String = "",
    var fcmToken: String = "",
    var estado: String = "SOLICITADO",
    val fechaSolicitud: Long = 0L,
    val asignacion: AsignacionTurno? = null,
    val encuestaAsociadaId: String? = null
)