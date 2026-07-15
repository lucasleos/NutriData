package unpsjb.ing.tntpm2024.estadistica

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.databinding.FragmentEstadisticaBinding

class EstadisticaFragment : Fragment() {

    private lateinit var binding: FragmentEstadisticaBinding
    private val viewModel: EstadisticaViewModel by viewModels()

    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_estadistica, container, false)
        binding.estadisticaViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        barChart = binding.barChart
        pieChart = binding.pieChart

        setupSpinner()
        setupCharts()

        viewModel.alimentoEncuestaDetalles.observe(viewLifecycleOwner) {
            Log.d("Estadisticas", "Cantidad de detalles: ${it.size}")
        }

        viewModel.chartData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                updateBarChart(data)
            }
        }

        viewModel.macroData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                updatePieChart(data)
            }
        }

        return binding.root
    }

    private fun setupCharts() {
        // Bar Chart setup
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true)

        // Pie Chart setup
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.centerText = "Macros"
        pieChart.setCenterTextSize(18f)
        pieChart.animateY(1000)
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.opcionesFrecuencia,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTimeFrame.adapter = adapter

        binding.spinnerTimeFrame.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val timeFrame = parent.getItemAtPosition(position).toString()
                viewModel.setTimeFrame(timeFrame)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateBarChart(data: Map<String, Double>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        var index = 0f
        data.forEach { (label, value) ->
            entries.add(BarEntry(index, value.toFloat()))
            labels.add(label)
            index++
        }

        val dataSet = BarDataSet(entries, "Consumo Promedio")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.granularity = 1f
        barChart.xAxis.isGranularityEnabled = true
        barChart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.labelRotationAngle = -45f

        barChart.data = barData
        barChart.invalidate()
    }

    private fun updatePieChart(data: Map<String, Double>) {
        val entries = ArrayList<PieEntry>()
        data.forEach { (label, value) ->
            if (value > 0) {
                entries.add(PieEntry(value.toFloat(), label))
            }
        }

        if (entries.isEmpty()) {
            pieChart.clear()
            return
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f
        dataSet.sliceSpace = 3f

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(pieChart))

        pieChart.data = pieData
        pieChart.invalidate()
    }
}
