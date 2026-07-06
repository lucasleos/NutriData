package unpsjb.ing.tntpm2024.encuesta

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import unpsjb.ing.tntpm2024.databinding.FragmentMapsBinding

class MapsFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private val polygons = mutableListOf<PolygonInfo>()
    private var colorAnimator: ValueAnimator? = null
    private var selectedPolygonInfo: PolygonInfo? = null
    
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private val args: MapsFragmentArgs by navArgs()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            enableMyLocation()
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        setupMap()
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
            selectedPolygonInfo?.let { info ->
                val action = if (args.isEdit) {
                    MapsFragmentDirections.actionMapsFragmentToEditarEncuestaFragment(
                        aliemento = "",
                        encuestaCompletada = false,
                        encuestaId = args.encuestaId,
                        frecuencia = "",
                        porcion = "",
                        veces = "",
                        zona = info.zona
                    )
                } else {
                    MapsFragmentDirections.actionMapsFragmentToNuevaEncuestaFragment(
                        zona = info.zona,
                        args.encuestaId
                    )
                }
                findNavController().navigate(action)
            }
        }

        binding.fabMapType.setOnClickListener {
            toggleMapType()
        }
    }

    private fun setupMap() {
        val madryn = LatLng(-42.7692, -65.03851)
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madryn, 13f))
        
        checkLocationPermissions()

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

        mMap.setOnMapClickListener {
            selectedPolygonInfo?.let {
                resetPolygon(it)
                selectedPolygonInfo = null
                binding.tvSelectedZone.text = "Selecciona una zona en el mapa"
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
                showInfoWindow(newlySelectedPolygonInfo)
                
                binding.tvSelectedZone.text = "Zona Seleccionada: ${newlySelectedPolygonInfo.zona}"
                binding.btnSeleccionZona.isEnabled = true
            }
        }
    }

    private fun checkLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                enableMyLocation()
            }
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun enableMyLocation() {
        try {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
        } catch (e: SecurityException) {
            Log.e("MapsFragment", "Error enabling location", e)
        }
    }

    private fun toggleMapType() {
        mMap.mapType = if (mMap.mapType == GoogleMap.MAP_TYPE_NORMAL) {
            GoogleMap.MAP_TYPE_SATELLITE
        } else {
            GoogleMap.MAP_TYPE_NORMAL
        }
    }

    private fun addPolygonToMap(points: List<LatLng>, name: String) {
        val polygonOptions = PolygonOptions()
            .addAll(points)
            .strokeWidth(5f)
            .strokeColor(Color.DKGRAY)
            .fillColor(Color.argb(70, 46, 125, 50)) // Verde semi-translúcido (basado en green_700)

        val polygon = mMap.addPolygon(polygonOptions)
        polygon.isClickable = true

        val polygonInfo = PolygonInfo(polygon, name)
        polygons.add(polygonInfo)
    }

    private fun animatePolygon(polygonInfo: PolygonInfo) {
        val startColor = Color.argb(70, 46, 125, 50)
        val endColor = Color.argb(120, 121, 85, 72) // Marrón translúcido (basado en brown_500)

        colorAnimator?.cancel()
        colorAnimator = ValueAnimator.ofArgb(startColor, endColor).apply {
            duration = 500
            addUpdateListener { animator ->
                polygonInfo.polygon.fillColor = animator.animatedValue as Int
            }
            start()
        }
        polygonInfo.polygon.strokeColor = Color.parseColor("#795548")
        polygonInfo.polygon.strokeWidth = 8f
    }

    private fun resetPolygon(polygonInfo: PolygonInfo) {
        binding.btnSeleccionZona.isEnabled = false
        polygonInfo.isSelected = false
        val startColor = polygonInfo.polygon.fillColor
        val endColor = Color.argb(70, 46, 125, 50)

        val reverseColorAnimator = ValueAnimator.ofArgb(startColor, endColor).apply {
            duration = 300
            addUpdateListener { animator ->
                polygonInfo.polygon.fillColor = animator.animatedValue as Int
            }
            start()
        }

        reverseColorAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                polygonInfo.polygon.strokeColor = Color.DKGRAY
                polygonInfo.polygon.strokeWidth = 5f
            }
        })

        polygonInfo.marker?.remove()
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
                .title(polygonInfo.zona)
        )
        marker?.showInfoWindow()
        polygonInfo.marker = marker
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
