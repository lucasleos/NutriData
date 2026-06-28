package unpsjb.ing.tntpm2024.basededatos.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_alimento")
data class Alimento(
    @PrimaryKey(autoGenerate = true)
    val alimentoId: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String = "",
    @ColumnInfo(name = "categoria") val categoria: String = "",
    @ColumnInfo(name = "medida") val medida: String = "",
    @ColumnInfo(name = "kcal_totales") val kcal: Double = 0.0,
    @ColumnInfo(name = "carbohidratos") val carbohidratos: Double = 0.0,
    @ColumnInfo(name = "proteinas") val proteinas: Double = 0.0,
    @ColumnInfo(name = "grasas") val grasas: Double = 0.0,
    @ColumnInfo(name = "alcohol") val alcohol: Double = 0.0,
    @ColumnInfo(name = "colesterol") val colesterol: Double = 0.0,
    @ColumnInfo(name = "fibra") val fibra: Double = 0.0
)