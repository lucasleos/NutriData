package unpsjb.ing.tntpm2024.estadistica

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.databinding.FragmentEstadisticaBinding
import unpsjb.ing.tntpm2024.detalle.AlimentoEncuestaDetalles
import unpsjb.ing.tntpm2024.encuesta.AlimentoEncuestaViewModel
import unpsjb.ing.tntpm2024.encuesta.AlimentoEncuestaViewModelFactory

class EstadisticaFragment : Fragment() {

    val TAG = "EstadisticaFragment"
    private lateinit var binding: FragmentEstadisticaBinding
    private val viewModel: EstadisticaViewModel by viewModels()

    private lateinit var consumosList: List<AlimentoEncuestaDetalles>
    private lateinit var alimentoEncuestaViewModel: AlimentoEncuestaViewModel

    private lateinit var barChart: BarChart
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_estadistica, container, false
        )
        binding.estadisticaViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val database = EncuestasDatabase.getInstance(requireContext())
        val factory = AlimentoEncuestaViewModelFactory(database)
        val viewModel: AlimentoEncuestaViewModel by viewModels { factory }
        alimentoEncuestaViewModel = viewModel

        viewModel.alimentoEncuestaDetalles.observe(viewLifecycleOwner) { detalles ->
            consumosList = detalles
            setupSpinner()
        }

        barChart = binding.barChart
        spinner = binding.spinnerTimeFrame



        return binding.root
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.opcionesFrecuencia,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val timeFrame = parent.getItemAtPosition(position).toString()
                getConsumoNutrientes(timeFrame)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed
            }
        }
    }

    private fun getConsumoNutrientes(timeFrame: String) {
        var consumoKcal = 0.0
        var consumoCarbohidratos = 0.0
        var consumoProteinas = 0.0
        var consumoColesterol = 0.0
        var consumoFibras = 0.0

        consumosList.forEach { item ->
            val porcionEnGramos = item.porcion.toDouble()

            val kcalPorPorcion = (porcionEnGramos / 100.0) * item.kcal_totales.toDouble()
            val carbosPorPorcion = (porcionEnGramos / 100.0) * item.carbohidratos.toDouble()
            val proteinasPorPorcion = (porcionEnGramos / 100.0) * item.proteinas.toDouble()
            val colesterolPorPorcion = (porcionEnGramos / 100.0) * item.coresterol.toDouble()
            val fibrasPorPorcion = (porcionEnGramos / 100.0) * item.fibra.toDouble()

            val ajusteFrecuencia = when (item.frecuencia) {
                "Diaria" -> 1.0
                "Semanal" -> 1.0 / 7.0
                "Mensual" -> 1.0 / 30.0
                "Anual" -> 1.0 / 365.0
                else -> 1.0
            }

            consumoKcal += kcalPorPorcion * item.veces.toDouble() * ajusteFrecuencia
            consumoCarbohidratos += carbosPorPorcion * item.veces.toDouble() * ajusteFrecuencia
            consumoProteinas += proteinasPorPorcion * item.veces.toDouble() * ajusteFrecuencia
            consumoColesterol += colesterolPorPorcion * item.veces.toDouble() * ajusteFrecuencia
            consumoFibras += fibrasPorPorcion * item.veces.toDouble() * ajusteFrecuencia
        }

        val totalItems = consumosList.size.toDouble()
        val promedioConsumoKcal = if (totalItems > 0) consumoKcal / totalItems else 0.0
        val promedioConsumoCarbohidratos =
            if (totalItems > 0) consumoCarbohidratos / totalItems else 0.0
        val promedioConsumoProteinas = if (totalItems > 0) consumoProteinas / totalItems else 0.0
        val promedioConsumoColesterol = if (totalItems > 0) consumoColesterol / totalItems else 0.0
        val promedioConsumoFibras = if (totalItems > 0) consumoFibras / totalItems else 0.0

        val adjustedConsumoKcal = adjustConsumo(promedioConsumoKcal, timeFrame)
        val adjustedConsumoCarbohidratos = adjustConsumo(promedioConsumoCarbohidratos, timeFrame)
        val adjustedConsumoProteinas = adjustConsumo(promedioConsumoProteinas, timeFrame)
        val adjustedConsumoColesterol = adjustConsumo(promedioConsumoColesterol, timeFrame)
        val adjustedConsumoFibras = adjustConsumo(promedioConsumoFibras, timeFrame)

        updateBarChart(
            adjustedConsumoKcal.toFloat(),
            adjustedConsumoCarbohidratos.toFloat(),
            adjustedConsumoProteinas.toFloat(),
            adjustedConsumoColesterol.toFloat(),
            adjustedConsumoFibras.toFloat()
        )
    }

    private fun adjustConsumo(consumo: Double, timeFrame: String): Double {
        return when (timeFrame) {
            "Diaria" -> consumo
            "Semanal" -> consumo * 7
            "Mensual" -> consumo * 30
            "Anual" -> consumo * 365
            else -> consumo
        }
    }

    private fun updateBarChart(
        kcal: Float,
        carbohidratos: Float,
        proteinas: Float,
        colesterol: Float,
        fibras: Float
    ) {
        val list: ArrayList<BarEntry> = ArrayList()

        list.add(BarEntry(0f, kcal))
        list.add(BarEntry(1f, carbohidratos))
        list.add(BarEntry(2f, proteinas))
        list.add(BarEntry(3f, colesterol))
        list.add(BarEntry(4f, fibras))

        val barDataSet = BarDataSet(list, "Promedio de Consumo Diario")
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)
        barDataSet.valueTextColor = Color.BLACK

        val barData = BarData(barDataSet)

        val labels = listOf("Calorías", "Carbohidr.", "Proteínas", "Colesterol", "Fibras")
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        //barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f
        barChart.xAxis.isGranularityEnabled = true

        // setting text size
        barDataSet.valueTextSize = 16f
        barChart.description.isEnabled = false

        barChart.setFitBars(true)
        barChart.data = barData
        //barChart.description.text = "Promedio de Consumo Diario"
        barChart.animateY(1500)
        barChart.invalidate() // refresh the chart
    }
}
