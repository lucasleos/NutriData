package unpsjb.ing.tntpm2024.basededatos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_encuesta")
data class Encuesta(
    @PrimaryKey(autoGenerate = true)
    var encuestaId: Int = 0,

    @ColumnInfo(name = "alimento")
    var alimento: String,
    @ColumnInfo(name = "porcion")
    var porcion: String,
    @ColumnInfo(name = "frecuencia")
    var frecuencia:String,
    @ColumnInfo(name = "veces")
    var veces: String,
    @ColumnInfo(name = "encuestaCompletada")
    var encuestaCompletada: Boolean

)