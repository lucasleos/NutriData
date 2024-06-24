package unpsjb.ing.tntpm2024.basededatos.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tabla_encuesta")
data class Encuesta(
    @PrimaryKey(autoGenerate = true)
    var encuestaId: Int = 0,
    @ColumnInfo(name = "fecha")
    var fecha: Long,
    @ColumnInfo(name = "encuestaCompletada")
    var encuestaCompletada: Boolean,
    @ColumnInfo(name = "zona")
    var zona: String,
    @ColumnInfo(name = "userId")
    var userId: String? = null,
    @ColumnInfo(name = "userEmail")
    var userEmail: String? = null,
)
{
    constructor() : this(0, 0, true, "")
}
