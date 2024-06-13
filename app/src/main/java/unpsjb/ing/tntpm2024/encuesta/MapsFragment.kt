package unpsjb.ing.tntpm2024.encuesta

import android.animation.ValueAnimator
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
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import unpsjb.ing.tntpm2024.R

class MapsFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var primaryPolygone: Polygon
    private lateinit var shadowPolygone: Polygon
    private var isPolygonClicked = false
    val startColor = Color.LTGRAY
    val endColor = Color.argb(128, 255, 0, 0)
    val colorAnimator = ValueAnimator.ofArgb(startColor, endColor)
    val latLngAnimator = ValueAnimator.ofFloat(0f, 1f)

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        val madryn = LatLng(-42.7692, -65.03851)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madryn, 13f))
        addPolylineToMap()
        mMap.setOnMapClickListener { latLng ->
            if (isPolygonClicked) {
                resetPolygon()
            }
        }
    }

    private fun resetPolygon() {
        isPolygonClicked = false
        // Detener la animación, si está en progreso
        colorAnimator?.cancel()
        // Volver atras la animacion
        colorAnimator.reverse()
        latLngAnimator.reverse()
    }

    private fun addPolylineToMap() {
            val primaryPolygoneOptions = PolygonOptions()
                .add(LatLng(-42.788635, -65.013477),
                    LatLng(-42.769431, -65.030652),
                    LatLng(-42.772774, -65.040430),
                    LatLng(-42.790150, -65.029579))
                .strokeWidth(5f)
                .strokeColor(Color.LTGRAY)

            val shadowPolygoneOptions = PolygonOptions()
                .add(LatLng(-42.7886351, -65.0134771),
                    LatLng(-42.7694311, -65.0306521),
                    LatLng(-42.7727741, -65.0404301),
                    LatLng(-42.7901501, -65.0295791))
                .strokeWidth(5f)
                .strokeColor(Color.LTGRAY)

            shadowPolygone = mMap.addPolygon(shadowPolygoneOptions)
            primaryPolygone = mMap.addPolygon(primaryPolygoneOptions)
            primaryPolygone.isClickable = true

            mMap.setOnPolygonClickListener { clickedPolygone ->
                if (clickedPolygone == primaryPolygone && !isPolygonClicked) {
                    isPolygonClicked = true
                    animatePolylineElevation()
                }
            }
    }

    private fun animatePolylineElevation() {

        colorAnimator?.cancel()
        colorAnimator.duration = 400 // Duración de la animación en milisegundos

        colorAnimator.addUpdateListener { animator ->
            val animatedColor = animator.animatedValue as Int
            primaryPolygone.strokeColor = animatedColor
        }

        val startLatLng = primaryPolygone.points
        val endLatLng = startLatLng.map { LatLng(it.latitude + 0.0002, it.longitude + 0.0002) }

        latLngAnimator.duration = 400 // Duración de la animación en milisegundos

        latLngAnimator.addUpdateListener { animator ->
            val fraction = animator.animatedFraction
            val animatedLatLng = startLatLng.zip(endLatLng) { start, end ->
                LatLng(
                    start.latitude + (end.latitude - start.latitude) * fraction,
                    start.longitude + (end.longitude - start.longitude) * fraction
                )
            }
            primaryPolygone.points = animatedLatLng
        }

        colorAnimator.start()
        latLngAnimator.start()
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
}





/*package unpsjb.ing.tntpm2024.encuesta

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
