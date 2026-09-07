package unpsjb.ing.tntpm2024.estadistica

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.databinding.FragmentEstadisticaBinding

class EstadisticaFragment : Fragment() {

    private var _binding: FragmentEstadisticaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EstadisticaViewModel by viewModels()

    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEstadisticaBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        barChart = binding.barChart
        pieChart = binding.pieChart

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupSpinner()
        setupCharts()
        observeViewModel()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.opcionesFrecuencia,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
        }

        binding.spinnerTimeFrame.adapter = adapter

        binding.spinnerTimeFrame.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedTimeFrame = parent
                        ?.getItemAtPosition(position)
                        ?.toString()
                        ?: return

                    viewModel.setTimeFrame(selectedTimeFrame)
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) = Unit
            }
    }

    private fun setupCharts() {
        barChart.apply {
            description.isEnabled = false
            setFitBars(true)
            setTouchEnabled(true)
            setPinchZoom(true)
            animateY(700)
        }

        pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setUsePercentValues(true)
            setEntryLabelTextSize(12f)
            setEntryLabelColor(Color.BLACK)
            centerText = "Macros"
            setCenterTextSize(18f)
            animateY(700)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.alimentoEncuestaDetalles.observe(
                viewLifecycleOwner
            ) {
                binding.tvTotalItems.text = it.size.toString()
            }
        }

        viewModel.avgKcal.observe(viewLifecycleOwner) { average ->
            binding.tvAvgKcal.text = String.format(
                "%.0f",
                average
            )
        }

        viewModel.topFoods.observe(viewLifecycleOwner) { topFoods ->
            if (topFoods.size >= 3) {
                binding.tvTop1Food.text = topFoods[0].name
                binding.ivTop1Food.setImageResource(topFoods[0].drawableResId)

                binding.tvTop2Food.text = topFoods[1].name
                binding.ivTop2Food.setImageResource(topFoods[1].drawableResId)

                binding.tvTop3Food.text = topFoods[2].name
                binding.ivTop3Food.setImageResource(topFoods[2].drawableResId)
            }
        }

        viewModel.chartData.observe(viewLifecycleOwner) { data ->
            updateBarChart(data)
        }

        viewModel.macroData.observe(viewLifecycleOwner) { data ->
            updatePieChart(data)
        }

        viewModel.timeFrame.observe(viewLifecycleOwner) { _ ->
            runTransitionEffect()
        }
    }

    private fun runTransitionEffect() {
        binding.barChart.alpha = 0.2f
        binding.barChart.animate().alpha(1.0f).setDuration(400).start()
        barChart.animateY(600)

        binding.pieChart.alpha = 0.2f
        binding.pieChart.animate().alpha(1.0f).setDuration(400).start()
        pieChart.animateY(600)
    }

    private fun updateBarChart(data: Map<String, Double>) {
        val orderedData = linkedMapOf(
            "Calorías" to (data["Calorías"] ?: 0.0),
            "Carbohidr." to (data["Carbohidr."] ?: 0.0),
            "Proteínas" to (data["Proteínas"] ?: 0.0),
            "Colesterol" to (data["Colesterol"] ?: 0.0),
            "Fibras" to (data["Fibras"] ?: 0.0)
        )

        val entries = orderedData.values.mapIndexed { index, value ->
            BarEntry(
                index.toFloat(),
                value.toFloat()
            )
        }

        val currentTimeFrame = viewModel.timeFrame.value ?: "Diaria"
        val dataSet = BarDataSet(
            entries,
            "Consumo promedio ($currentTimeFrame)"
        ).apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 10f
        }

        barChart.apply {
            xAxis.valueFormatter = IndexAxisValueFormatter(
                orderedData.keys.toList()
            )
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true
            xAxis.position =
                com.github.mikephil.charting.components
                    .XAxis.XAxisPosition.BOTTOM
            xAxis.labelRotationAngle = -45f
            setData(BarData(dataSet))
            invalidate()
        }
    }

    private fun updatePieChart(data: Map<String, Double>) {
        val entries = data
            .filter { it.value > 0.0 }
            .map { (label, value) ->
                PieEntry(value.toFloat(), label)
            }

        if (entries.isEmpty()) {
            pieChart.clear()
            pieChart.invalidate()
            return
        }

        val dataSet = PieDataSet(
            entries,
            ""
        ).apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 14f
            sliceSpace = 3f
        }

        val pieData = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(pieChart))
        }

        pieChart.data = pieData
        pieChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
