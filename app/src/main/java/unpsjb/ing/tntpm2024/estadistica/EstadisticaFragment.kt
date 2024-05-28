package unpsjb.ing.tntpm2024.estadistica

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.encuestas.Encuesta
import unpsjb.ing.tntpm2024.databinding.FragmentEstadisticaBinding


class EstadisticaFragment : Fragment() {

    private lateinit var binding: FragmentEstadisticaBinding
    private val viewModel: EstadisticaViewModel by viewModels()

    private lateinit var pieChart: PieChart
    private var sumaDiaria: Float = 0f;
    private var sumaSemanal: Float = 0f;
    private var sumaMensual: Float = 0f;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_estadistica, container, false
        )

        binding.estadisticaViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        calculoEncuestasDiarias()
        calculoEncuestasSemanales()
        calculoEncuestasMensuales()

        pieChart = binding.pieChart
        setupPieChart()
        loadPieChartData()

        return binding.root
    }

    private fun calculoEncuestasDiarias() {

        val encuestas = listOf(
            Encuesta(1, "Yogur bebible", "250ml", "Dia", "2", 12, true),
            Encuesta(2, "Yogur bebible", "150ml", "Dia", "5", 12, true),
            Encuesta(3, "Yogur bebible", "100ml", "Dia", "1", 12, true)
        )
        sumaDiaria = encuestas.sumOf { it.veces.toInt() * extractNum(it.porcion) }.toFloat()

    }

    private fun calculoEncuestasSemanales() {

        val encuestas = listOf(
            Encuesta(1, "Yogur bebible", "250ml", "Semana", "5", 12, true),
            Encuesta(2, "Yogur bebible", "250ml", "Semana", "3", 12, true),
            Encuesta(3, "Yogur bebible", "150ml", "Semana", "3", 12, true)
        )
        sumaSemanal = encuestas.sumOf { it.veces.toInt() * extractNum(it.porcion) }.toFloat()

    }

    private fun calculoEncuestasMensuales() {

        val encuestas = listOf(
            Encuesta(1, "Yogur bebible", "250ml", "Mes", "15", 12, true),
            Encuesta(2, "Yogur bebible", "250ml", "Mes", "8", 12, true),
            Encuesta(3, "Yogur bebible", "150ml", "Mes", "24", 12, true)
        )
        sumaMensual = encuestas.sumOf { it.veces.toInt() * extractNum(it.porcion) }.toFloat()

    }

    private fun extractNum(cadena: String): Int {
        return cadena.substringBefore("ml").trim().toInt()
    }

    private fun setupPieChart() {
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.White.toArgb())
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.Black.toArgb())
        pieChart.centerText = "Consumo de Grasas"
        pieChart.setCenterTextSize(24f)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = true
    }

    private fun loadPieChartData() {
        // Ejemplo de datos de consumo de grasas (en gramos)
        val consumoDiario = 20f
        val consumoSemanal = 140f
        val consumoMensual = 600f

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(sumaDiaria, "% Diario"))
        entries.add(PieEntry(sumaSemanal, "% Semanal"))
        entries.add(PieEntry(sumaMensual, "% Mensual"))

        val colors = ArrayList<Int>()
        colors.add(Color.Red.toArgb())
        colors.add(Color.Blue.toArgb())
        colors.add(Color.Green.toArgb())

        val dataSet = PieDataSet(entries, "Consumo de Grasas")
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.Black.toArgb())

        pieChart.data = data
        pieChart.invalidate()
    }

}