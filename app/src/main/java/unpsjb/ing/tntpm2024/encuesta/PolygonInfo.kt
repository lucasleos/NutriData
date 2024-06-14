package unpsjb.ing.tntpm2024.encuesta

import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import org.checkerframework.common.reflection.qual.GetMethod

data class PolygonInfo(
    val polygon: Polygon,
    var zona: String,
    var isSelected: Boolean = false,
    var marker: Marker? = null
)