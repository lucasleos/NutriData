package unpsjb.ing.tntpm2024.encuesta

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.databinding.FragmentMapsBinding

class MapsFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private val polygons = mutableListOf<PolygonInfo>()
    private var colorAnimator: ValueAnimator? = null
    private var latLngAnimator: ValueAnimator? = null
    private var selectedPolygonInfo: PolygonInfo? = null
    private lateinit var btnSeleccionZona: Button
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    val args: MapsFragmentArgs by navArgs()

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        val madryn = LatLng(-42.7692, -65.03851)
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madryn, 12.5f))

        // mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))

        // Agregar polígonos al mapa
        addPolygonToMap(
            listOf(
                LatLng(-42.788635, -65.013477),
                LatLng(-42.769431, -65.030652),
                LatLng(-42.772774, -65.040430),
                LatLng(-42.790150, -65.029579)
            ), "Zona Sur"
        )

        addPolygonToMap(
            listOf(
                LatLng(-42.772774, -65.040430),
                LatLng(-42.775725, -65.049178),
                LatLng(-42.774003, -65.061113),
                LatLng(-42.785017, -65.083316),
                LatLng(-42.795431, -65.077114),
                LatLng(-42.786510, -65.048428),
                LatLng(-42.790121, -65.046007),
                LatLng(-42.785589, -65.032413)
            ), "Zona Oeste"
        )

        addPolygonToMap(
            listOf(
                LatLng(-42.772774, -65.040430),
                LatLng(-42.775725, -65.049178),
                LatLng(-42.774003, -65.061113),
                LatLng(-42.766533, -65.072836),
                LatLng(-42.753236, -65.065282),
                LatLng(-42.749797, -65.044398),
                LatLng(-42.750066, -65.037039),
                LatLng(-42.760067, -65.036811),
                LatLng(-42.769521, -65.031191)
            ), "Zona Norte"
        )

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
                animateLatLng(
                    newlySelectedPolygonInfo.polygon.points[0],
                    LatLng(
                        newlySelectedPolygonInfo.polygon.points[0].latitude + 0.01,
                        newlySelectedPolygonInfo.polygon.points[0].longitude
                    )
                )
                showInfoWindow(newlySelectedPolygonInfo)
                binding.btnSeleccionZona.isEnabled = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        binding.btnSeleccionZona.setOnClickListener {
            Toast.makeText(context, "selecciono: ${selectedPolygonInfo?.zona}", Toast.LENGTH_SHORT)
                .show()

            val action = if (args.isEdit) {
                MapsFragmentDirections.actionMapsFragmentToEditarEncuestaFragment(
                    aliemento = "",
                    encuestaCompletada = false,
                    encuestaId = args.encuestaId,
                    frecuencia = "",
                    porcion = "",
                    veces = "",
                    zona = selectedPolygonInfo!!.zona
                )
            } else {
                MapsFragmentDirections.actionMapsFragmentToNuevaEncuestaFragment(
                    zona = selectedPolygonInfo!!.zona
                )
            }

            findNavController().navigate(action)
        }

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
        binding.btnSeleccionZona.isEnabled = false
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
        animateLatLng(
            polygonInfo.polygon.points[0],
            LatLng(polygonInfo.polygon.points[0].latitude, polygonInfo.polygon.points[0].longitude)
        )
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