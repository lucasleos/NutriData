package unpsjb.ing.tntpm2024.estadistica

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import unpsjb.ing.tntpm2024.listado.EncuestaListAdapter
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import unpsjb.ing.tntpm2024.databinding.FragmentEstadisticaBinding
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModel
import unpsjb.ing.tntpm2024.encuesta.EncuestaViewModelFactory


class EstadisticaFragment : Fragment() {

    val TAG = "EstadisticaFragment"
    private lateinit var binding: FragmentEstadisticaBinding
    private val viewModel: EstadisticaViewModel by viewModels()

    private lateinit var encuestaViewModel: EncuestaViewModel
    private val adapterList : EncuestaListAdapter by lazy { EncuestaListAdapter(requireContext()) }

    private var listDiarias: List<Encuesta> = listOf()
    private var listSemanales: List<Encuesta> = listOf()
    private var listMensuales: List<Encuesta> = listOf()

    private lateinit var pieChart: PieChart
    private var sumaDiaria: Float = 0f
    private var sumaSemanal: Float = 0f
    private var sumaMensual: Float = 0f

    private val DENSIDAD_GRASA = 0.11
    private lateinit var consumoDiario: String
    private lateinit var consumoSemanal: String
    private lateinit var consumoMensual: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_estadistica, container, false
        )

        binding.estadisticaViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        consumoDiario = binding.consumoDiarioText.toString()
        consumoSemanal = binding.consumoSemanalText.toString()
        consumoMensual = binding.consumoMensualText.toString()

        val database = EncuestasDatabase.getInstance(requireContext())
        val factory = EncuestaViewModelFactory(database)

        pieChart = binding.pieChart

//        encuestaViewModel = ViewModelProvider(this)[EncuestaViewModel::class.java]
        encuestaViewModel = ViewModelProvider(this, factory)[EncuestaViewModel::class.java]

        encuestaViewModel.todasLasEncuestas
            .observe(
                viewLifecycleOwner
            ) { encuestas ->
                encuestas?.let {
                    Log.d(TAG, "EncuestasDiarias: $encuestas")
                    filtrarEncuestasPorDia(encuestas)
                    filtrarEncuestasPorSemana(encuestas)
                    filtrarEncuestasPorMes(encuestas)
                    adapterList.setEncuestas(it)
                }
                setupPieChart()
                loadPieChartData()
            }


        return binding.root
    }

    private fun filtrarEncuestasPorDia(encuestas: List<Encuesta>) {
//        listDiarias = encuestas.filter { it.frecuencia == "Dia" }
//        Log.d(TAG, "FiltradoEncuestasDiarias: $listDiarias")
        calculoEncuestasDiarias()
    }

    private fun filtrarEncuestasPorSemana(encuestas: List<Encuesta>) {
//        listSemanales = encuestas.filter { it.frecuencia == "Semana" }
//        Log.d(TAG, "FiltradoEncuestasSemanales: $listSemanales")
        calculoEncuestasSemanales()
    }

    private fun filtrarEncuestasPorMes(encuestas: List<Encuesta>) {
//        listMensuales = encuestas.filter { it.frecuencia == "Mes" }
//        Log.d(TAG, "FiltradoEncuestasMensuales: $listMensuales")
        calculoEncuestasMensuales()
    }

    private fun calculoEncuestasDiarias() {

        Log.d(TAG, "ListaEncuestasDiariasPrevia: $listDiarias")
        sumaDiaria = listDiarias.sumOf { encuesta ->
//            val veces = encuesta.veces.toIntOrNull() ?:
//            val porcion = extractNum(encuesta.porcion)
            val veces = 1
            val porcion = 250
            veces * porcion
        }.toFloat()
        Log.d(TAG, "CalculoEncuestasDiarias: $sumaDiaria")
        calculoConsumoDiario()

    }

    private fun calculoConsumoDiario() {
        consumoDiario = (sumaDiaria * DENSIDAD_GRASA).toString() + "gr"
        Log.d(TAG, "CalculoConsumoDiario: $consumoDiario")
    }

    private fun calculoEncuestasSemanales() {

//        sumaSemanal = listSemanales.sumOf { it.veces.toInt() * extractNum(it.porcion) }.toFloat()
        sumaSemanal = 20.0F
        Log.d(TAG, "CalculoEncuestasSemanales: $sumaSemanal")
        calculoConsumoSemanal()

    }

    private fun calculoConsumoSemanal() {
        consumoSemanal = (sumaSemanal * DENSIDAD_GRASA).toString() + "gr"
        Log.d(TAG, "CalculoConsumoSemanal: $consumoSemanal")
    }

    private fun calculoEncuestasMensuales() {

//        sumaMensual = listMensuales.sumOf { it.veces.toInt() * extractNum(it.porcion) }.toFloat()
        sumaMensual = 10.0F
        Log.d(TAG, "CalculoEncuestasMensuales: $sumaMensual")
        calculoConsumoMensual()

    }

    private fun calculoConsumoMensual() {
        consumoMensual = (sumaMensual * DENSIDAD_GRASA).toString() + "gr"
        Log.d(TAG, "CalculoConsumoMensual: $consumoMensual")
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

        Log.d(TAG, "sumaDiariaGrafico: $sumaDiaria")
        Log.d(TAG, "sumaSemanalGrafico: $sumaSemanal")
        Log.d(TAG, "sumaMensualGrafico: $sumaMensual")

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(sumaDiaria, "% Diario"))
        entries.add(PieEntry(sumaSemanal, "% Semanal"))
        entries.add(PieEntry(sumaMensual, "% Mensual"))

        val colors = ArrayList<Int>()
        colors.add(Color.Red.toArgb())
        colors.add(Color.Yellow.toArgb())
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