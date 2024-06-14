package unpsjb.ing.tntpm2024.encuesta

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import unpsjb.ing.tntpm2024.R

class MapsFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private val polygons = mutableListOf<PolygonInfo>()
    private var colorAnimator: ValueAnimator? = null
    private var latLngAnimator: ValueAnimator? = null
    private var selectedPolygonInfo: PolygonInfo? = null

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        val madryn = LatLng(-42.7692, -65.03851)
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madryn, 14f))

       // mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))

        // Agregar polígonos al mapa
        addPolygonToMap(listOf(
            LatLng(-42.788635, -65.013477),
            LatLng(-42.769431, -65.030652),
            LatLng(-42.772774, -65.040430),
            LatLng(-42.790150, -65.029579)
        ), "Zona Sur")

        addPolygonToMap(listOf(
            LatLng(-42.772774, -65.040430),
            LatLng(-42.775725, -65.049178),
            LatLng(-42.774003, -65.061113),
            LatLng(-42.785017, -65.083316),
            LatLng(-42.795431, -65.077114),
            LatLng(-42.786510, -65.048428),
            LatLng(-42.790121, -65.046007),
            LatLng(-42.785589, -65.032413)
        ), "Zona Oeste")

        addPolygonToMap(listOf(
            LatLng(-42.772774, -65.040430),
            LatLng(-42.775725, -65.049178),
            LatLng(-42.774003, -65.061113),
            LatLng(-42.766533, -65.072836),
            LatLng(-42.753236, -65.065282),
            LatLng(-42.749797, -65.044398),
            LatLng(-42.750066, -65.037039),
            LatLng(-42.760067, -65.036811),
            LatLng(-42.769521, -65.031191)
        ), "Zona Norte")

//        mMap.setOnPolygonClickListener { clickedPolygone ->
//            Toast.makeText(context, "click adentro", Toast.LENGTH_SHORT).show()
//        }

        mMap.setOnMapClickListener {
            selectedPolygonInfo?.let {
                resetPolygon(it)
                selectedPolygonInfo = null
            }
        }

        mMap.setOnPolygonClickListener { clickedPolygon ->
            val newlySelectedPolygonInfo = polygons.firstOrNull { it.polygon == clickedPolygon }
            if (newlySelectedPolygonInfo != null && newlySelectedPolygonInfo != selectedPolygonInfo) {
                selectedPolygonInfo?.let {
                    resetPolygon(it)
                }
                newlySelectedPolygonInfo.isSelected = true
                selectedPolygonInfo = newlySelectedPolygonInfo
                animatePolygon(newlySelectedPolygonInfo)
                animateLatLng(newlySelectedPolygonInfo.polygon.points[0], LatLng(newlySelectedPolygonInfo.polygon.points[0].latitude + 0.01, newlySelectedPolygonInfo.polygon.points[0].longitude))
                showInfoWindow(newlySelectedPolygonInfo)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun addPolygonToMap(points: List<LatLng>, name: String) {
        val polygonOptions = PolygonOptions()
            .addAll(points)
            .strokeWidth(10f)
            .strokeColor(Color.BLACK)
            .fillColor(Color.argb(128, 128, 128, 128)) // Color gris translúcido

        val polygon = mMap.addPolygon(polygonOptions)
        polygon.isClickable = true

        val polygonInfo = PolygonInfo(polygon, name)
        polygons.add(polygonInfo)
    }

    private fun animatePolygon(polygonInfo: PolygonInfo) {
        val startColor = Color.argb(128, 128, 128, 128) // Gris translúcido
        val endColor = Color.argb(50, 128, 0, 0) // Rojo translúcido

        colorAnimator?.cancel() // Cancelar cualquier animación previa
        colorAnimator = ValueAnimator.ofArgb(startColor, endColor).apply {
            duration = 1000 // Duración de la animación en milisegundos

            addUpdateListener { animator ->
                val animatedColor = animator.animatedValue as Int
                polygonInfo.polygon.fillColor = animatedColor
            }

            start()
        }

        polygonInfo.polygon.strokeColor = Color.RED // Cambiar el color del borde
    }

    private fun resetPolygon(polygonInfo: PolygonInfo) {
        polygonInfo.isSelected = false
        val startColor = polygonInfo.polygon.fillColor
        val endColor = Color.argb(128, 128, 128, 128) // Gris translúcido

        val reverseColorAnimator = ValueAnimator.ofArgb(startColor, endColor).apply {
            duration = 500 // Duración de la animación inversa

            addUpdateListener { animator ->
                val animatedColor = animator.animatedValue as Int
                polygonInfo.polygon.fillColor = animatedColor
            }

            start()
        }

        reverseColorAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Restaurar el color del borde después de que termine la animación
                polygonInfo.polygon.strokeColor = Color.BLACK
            }
        })

        polygonInfo.marker?.remove() // Eliminar el marcador asociado si existe

        // Revertir la animación de latitud/longitud si está en curso
        latLngAnimator?.cancel()
        animateLatLng(polygonInfo.polygon.points[0], LatLng(polygonInfo.polygon.points[0].latitude, polygonInfo.polygon.points[0].longitude))
    }
    private fun animateLatLng(start: LatLng, end: LatLng) {
        latLngAnimator?.cancel() // Cancelar cualquier animación previa
        latLngAnimator = ValueAnimator.ofObject(LatLngEvaluator(), start, end).apply {
            duration = 1000 // Duración de la animación en milisegundos

            addUpdateListener { animator ->
                val animatedLatLng = animator.animatedValue as LatLng
                // Aquí puedes mover algún objeto en el mapa usando animatedLatLng
                // Ejemplo: mover un marcador o una cámara
            }

            start()
        }
    }

    private fun showInfoWindow(polygonInfo: PolygonInfo) {
        val bounds = LatLngBounds.Builder()
        for (point in polygonInfo.polygon.points) {
            bounds.include(point)
        }
        val center = bounds.build().center

        val marker = mMap.addMarker(
            MarkerOptions()
                .position(center)
                .title(polygonInfo.zona) // Usar el nombre del polígono

        )
        marker?.showInfoWindow()
        polygonInfo.marker = marker
    }
}

class LatLngEvaluator : TypeEvaluator<LatLng> {
    override fun evaluate(fraction: Float, startValue: LatLng, endValue: LatLng): LatLng {
        val lat = startValue.latitude + (endValue.latitude - startValue.latitude) * fraction
        val lng = startValue.longitude + (endValue.longitude - startValue.longitude) * fraction
        return LatLng(lat, lng)
    }
}




// version que anda mal  con dos poligonos
 /*
    private lateinit var mMap: GoogleMap
    private lateinit var primarySurPolygon: Polygon
    private lateinit var shadowPolygonSur: Polygon
    private var isSurPolygonClicked = false

    private lateinit var primaryOestePolygon: Polygon
    private lateinit var shadowPolygonOeste: Polygon
    private var isOestePolygonClicked = false


    val startColor = Color.LTGRAY
    val endColor = Color.argb(128, 255, 0, 0)
    val colorAnimator = ValueAnimator.ofArgb(startColor, endColor)
    val latLngAnimator = ValueAnimator.ofFloat(0f, 1f)
    private lateinit var marker: Marker

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        val madryn = LatLng(-42.7692, -65.03851)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madryn, 13f))
        addPolylineToMap()
        mMap.setOnMapClickListener { latLng ->

            if (isSurPolygonClicked) {
                resetSurPolygon()
                marker.remove()
            }
            if (isOestePolygonClicked) {
                resetOestePolygon()
               marker.remove()
            }
        }
    }

    private fun resetSurPolygon() {
        isSurPolygonClicked = false
        // Detener la animación, si está en progreso
        colorAnimator?.cancel()
        // Volver atras la animacion
        colorAnimator.reverse()
        latLngAnimator.reverse()

    }

    private fun resetOestePolygon() {
        isOestePolygonClicked = false
        // Detener la animación, si está en progreso
        colorAnimator?.cancel()
        // Volver atras la animacion
        colorAnimator.reverse()
        latLngAnimator.reverse()
    }

    private fun addPolylineToMap() {

        // polygon zona sur
            val primaryPolygonSurOptions = PolygonOptions()
                .add(LatLng(-42.788635, -65.013477),
                    LatLng(-42.769431, -65.030652),
                    LatLng(-42.772774, -65.040430),
                    LatLng(-42.790150, -65.029579))
                .strokeWidth(5f)
                .strokeColor(Color.LTGRAY)

            val shadowPolygonSurOptions = PolygonOptions()
                .add(LatLng(-42.7886351, -65.0134771),
                    LatLng(-42.7694311, -65.0306521),
                    LatLng(-42.7727741, -65.0404301),
                    LatLng(-42.7901501, -65.0295791))
                .strokeWidth(5f)
                .strokeColor(Color.LTGRAY)

        // polygon zona oeste
        val primaryPolygonOesteOptions = PolygonOptions()
            .add(LatLng(-42.772774, -65.040430),
                LatLng(-42.775725, -65.049178),
                LatLng(-42.774003, -65.061113),
                LatLng(-42.785017, -65.083316),
                LatLng(-42.795431, -65.077114),
                LatLng(-42.786510, -65.048428),
                LatLng(-42.790121, -65.046007),
                LatLng(-42.785589, -65.032413)
                )
            .strokeWidth(5f)
            .strokeColor(Color.LTGRAY)

        val shadowPolygonOesteOptions = PolygonOptions()
            .add(LatLng(-42.7886351, -65.0134771),
                LatLng(-42.7694311, -65.0306521),
                LatLng(-42.7727741, -65.0404301),
                LatLng(-42.7901501, -65.0295791))
            .strokeWidth(5f)
            .strokeColor(Color.LTGRAY)


        shadowPolygonSur = mMap.addPolygon(shadowPolygonSurOptions)
        primarySurPolygon = mMap.addPolygon(primaryPolygonSurOptions)
        primarySurPolygon.isClickable = true

        primaryOestePolygon = mMap.addPolygon(primaryPolygonOesteOptions)
        primaryOestePolygon.isClickable = true

            mMap.setOnPolygonClickListener { clickedPolygone ->
                if (clickedPolygone == primarySurPolygon && !isSurPolygonClicked) {
                    isSurPolygonClicked = true
                    animatePolylineElevation(clickedPolygone)
                    showInfoWindow(clickedPolygone, "Zona Sur")
                }
                if(clickedPolygone == primaryOestePolygon && !isOestePolygonClicked) {
                    isOestePolygonClicked = true
                    animatePolylineElevation(clickedPolygone)
                    showInfoWindow(clickedPolygone, "Zona Oeste")
                }
            }
    }

    private fun animatePolylineElevation(polygonSelected : Polygon) {

        colorAnimator?.cancel()
        colorAnimator.duration = 300 // Duración de la animación en milisegundos

        colorAnimator.addUpdateListener { animator ->
            val animatedColor = animator.animatedValue as Int
            polygonSelected.strokeColor = animatedColor
        }

        val startLatLng = polygonSelected.points
        val endLatLng = startLatLng.map { LatLng(it.latitude + 0.0002, it.longitude + 0.0002) }

        latLngAnimator.duration = 300 // Duración de la animación en milisegundos

        latLngAnimator.addUpdateListener { animator ->
            val fraction = animator.animatedFraction
            val animatedLatLng = startLatLng.zip(endLatLng) { start, end ->
                LatLng(
                    start.latitude + (end.latitude - start.latitude) * fraction,
                    start.longitude + (end.longitude - start.longitude) * fraction
                )
            }
            polygonSelected.points = animatedLatLng
        }

        colorAnimator.start()
        latLngAnimator.start()

    }

    private fun showInfoWindow(polygon: Polygon, title: String) {
        val bounds = LatLngBounds.Builder()
        for (point in polygon.points) {
            bounds.include(point)
        }
        val center = bounds.build().center

        marker = mMap.addMarker(
            MarkerOptions()
                .position(center)
                .title(title)
        )!!
        marker.showInfoWindow()
    }

    private fun showConfirmationDialog(tag: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar selección")
            .setMessage("¿Está seguro que desea seleccionar la zona $tag?")
            .setPositiveButton("Sí") { dialog, which ->
                Toast.makeText(requireContext(), "Zona $tag seleccionada", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    */
//}

/*
// version lucas
package unpsjb.ing.tntpm2024.encuesta

import android.graphics.Color
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions
import unpsjb.ing.tntpm2024.R

class MapsFragment : Fragment() {

    private lateinit var nMap: GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->
        nMap = googleMap
        val madryn = LatLng(-42.7692, -65.03851)
        nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madryn, 13f))

        // Calcula los límites de la ciudad
        val delta = 0.05 // Cambio de coordenadas para definir los cuadrantes

       // val northWest = LatLng(madryn.latitude + delta, madryn.longitude - delta)
       // val northEast = LatLng(madryn.latitude + delta, madryn.longitude + delta)
       // val southWest = LatLng(madryn.latitude - delta, madryn.longitude - delta)
        // val southEast = LatLng(madryn.latitude - delta, madryn.longitude + delta)



        // coordenaadas polyline zona sur
        //-42.788635, -65.013477
        //-42.769431, -65.030652
        //-42.773921, -65.039690
        //-42.790150, -65.029579


        val polygon = nMap.addPolygon(
            PolygonOptions()
                .add(LatLng(-42.788635, -65.013477),
                    LatLng(-42.769431, -65.030652),
                    LatLng(-42.773921, -65.039690),
                    LatLng(-42.790150, -65.029579))
                .clickable(true)
                //.strokeColor(0xFFFF0000.toInt())
                .strokeColor(Color.GRAY)
                .strokeWidth(2f) // Grosor del contorno
        )
        polygon.tag = tag


        // Crea los cuadrantes con contornos de diferentes colores
        //createQuadrant(nMap, northWest, LatLng(madryn.latitude, madryn.longitude), "Zona Sur", 0xFF0000FF.toInt()) // Azul
        //createQuadrant(nMap, LatLng(madryn.latitude, madryn.longitude), southEast, "Sureste", 0xFF00FF00.toInt()) // Verde
        //createQuadrant(nMap, northEast, LatLng(madryn.latitude, madryn.longitude), "Noreste", 0xFFFF0000.toInt()) // Rojo
       // createQuadrant(nMap, LatLng(madryn.latitude, madryn.longitude), southWest, "Suroeste", 0xFFFFFF00.toInt()) // Amarillo

        nMap.setOnPolygonClickListener { polyline ->
            val tag = polyline.tag as String
            // Muestra un cuadro de diálogo de confirmación
            polyline.strokeColor = Color.RED
            //polyline.fillColor = Color.LTGRAY
            polyline.zIndex = 2.0f
           // showConfirmationDialog(tag)
        }
    }


    private fun createQuadrant(map: GoogleMap, corner1: LatLng, corner2: LatLng, tag: String, color: Int) {
        val polygon = map.addPolygon(
            PolygonOptions()
                .add(corner1, LatLng(corner1.latitude, corner2.longitude), corner2, LatLng(corner2.latitude, corner1.longitude))
                .clickable(true)
                .strokeColor(color)
                .strokeWidth(5f) // Grosor del contorno
        )
        polygon.tag = tag
    }

    private fun showConfirmationDialog(tag: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar selección")
            .setMessage("¿Está seguro que desea seleccionar la zona $tag?")
            .setPositiveButton("Sí") { dialog, which ->
                Toast.makeText(requireContext(), "Zona $tag seleccionada", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}*/
