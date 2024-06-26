package unpsjb.ing.tntpm2024.detalle

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class AlimentoEncuestaDetalles(
    val encuestaId: Int,
    val alimentoId: Int,
    val categoria: String,
    val nombre: String,
    val medida: String,
    val kcal_totales: String,
    val carbohidratos: String,
    val proteinas: String,
    val grasas: String,
    val alcohol: String,
    val coresterol: String,
    val fibra: String,
    val porcion: String,
    val frecuencia: String,
    val veces: String
){
    constructor() : this(0, 0, "", "", "", "", "", "", "", "", "", "", "", "", "")
}