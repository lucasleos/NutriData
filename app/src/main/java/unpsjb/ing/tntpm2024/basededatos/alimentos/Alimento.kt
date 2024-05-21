package unpsjb.ing.tntpm2024.basededatos.alimentos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "tabla_alimento")
data class Alimento(
    @PrimaryKey(autoGenerate = true)
    var alimentoId: Int = 0,

    @ColumnInfo(name = "nombre")
    var nombre: String,
    @ColumnInfo(name = "tipo")
    var tipo: String,
    @ColumnInfo(name = "porcentaje_graso")
    var porcentajeGraso: Double,
)
