package unpsjb.ing.tntpm2024.basededatos.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "tabla_alimento_encuesta",
        primaryKeys= ["encuestaId","alimentoId"])
data class AlimentoEncuesta(
    val encuestaId: Int,
    val alimentoId: Int,
    @ColumnInfo(name = "porcion")
    var porcion: String,
    @ColumnInfo(name = "frecuencia")
    var frecuencia:String,
    @ColumnInfo(name = "veces")
    var veces: String,
)
