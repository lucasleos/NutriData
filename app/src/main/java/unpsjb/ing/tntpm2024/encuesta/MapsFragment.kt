package unpsjb.ing.tntpm2024.encuesta

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
        nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madryn, 12f))

        // Calcula los límites de la ciudad
        val delta = 0.05 // Cambio de coordenadas para definir los cuadrantes

        val northWest = LatLng(madryn.latitude + delta, madryn.longitude - delta)
        val northEast = LatLng(madryn.latitude + delta, madryn.longitude + delta)
        val southWest = LatLng(madryn.latitude - delta, madryn.longitude - delta)
        val southEast = LatLng(madryn.latitude - delta, madryn.longitude + delta)

        // Crea los cuadrantes con contornos de diferentes colores
        createQuadrant(nMap, northWest, LatLng(madryn.latitude, madryn.longitude), "Noroeste", 0xFF0000FF.toInt()) // Azul
        createQuadrant(nMap, LatLng(madryn.latitude, madryn.longitude), southEast, "Sureste", 0xFF00FF00.toInt()) // Verde
        createQuadrant(nMap, northEast, LatLng(madryn.latitude, madryn.longitude), "Noreste", 0xFFFF0000.toInt()) // Rojo
        createQuadrant(nMap, LatLng(madryn.latitude, madryn.longitude), southWest, "Suroeste", 0xFFFFFF00.toInt()) // Amarillo

        nMap.setOnPolygonClickListener { polygon ->
            val tag = polygon.tag as String
            // Muestra un cuadro de diálogo de confirmación
            showConfirmationDialog(tag)
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
}
