package unpsjb.ing.tntpm2024.estadistica

import android.app.Application
import androidx.lifecycle.*
import unpsjb.ing.tntpm2024.AndroidApp
import unpsjb.ing.tntpm2024.basededatos.AlimentoEncuestaRepository
import unpsjb.ing.tntpm2024.basededatos.EncuestasDatabase
import unpsjb.ing.tntpm2024.detalle.AlimentoEncuestaDetalles
import android.util.Log

class EstadisticaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AlimentoEncuestaRepository
    private val database: EncuestasDatabase = (application as AndroidApp).database

    private val _timeFrame = MutableLiveData("Diaria")
    val timeFrame: LiveData<String> = _timeFrame

    val alimentoEncuestaDetalles: LiveData<List<AlimentoEncuestaDetalles>>

    init {
        val dao = database.alimentoEncuestaDao()
        repository = AlimentoEncuestaRepository(dao)
        alimentoEncuestaDetalles = repository.alimentoEncuestaDetallesLiveData
    }

    fun setTimeFrame(timeFrame: String) {
        _timeFrame.value = timeFrame
    }

    // Processed data for Bar Chart
    val chartData = MediatorLiveData<Map<String, Double>>().apply {
        addSource(alimentoEncuestaDetalles) { detalles ->
            value = calculateStats(detalles, _timeFrame.value ?: "Diaria")
        }
        addSource(_timeFrame) { tf ->
            value = calculateStats(alimentoEncuestaDetalles.value ?: emptyList(), tf)
        }
    }

    // Processed data for Pie Chart (Macronutrients)
    val macroData = MediatorLiveData<Map<String, Double>>().apply {
        addSource(alimentoEncuestaDetalles) { detalles ->
            value = calculateMacros(detalles)
        }
    }

    // Quick Stats
    val totalItems: LiveData<Int> = alimentoEncuestaDetalles.map { it.size }
    
    val avgKcalDaily: LiveData<Double> = alimentoEncuestaDetalles.map { detalles ->
        calculateStats(detalles, "Diaria")["Calorías"] ?: 0.0
    }
    
    val topFood: LiveData<String> = alimentoEncuestaDetalles.map { detalles ->
        if (detalles.isEmpty()) "N/A"
        else detalles.groupBy { it.nombre }
            .maxByOrNull { it.value.size }?.key ?: "N/A"
    }

    private fun calculateStats(detalles: List<AlimentoEncuestaDetalles>, tf: String): Map<String, Double> {
        var totalKcal = 0.0
        var totalCarbs = 0.0
        var totalProts = 0.0
        var totalCols = 0.0
        var totalFibs = 0.0

        detalles.forEach { item ->
            val porcionEnGramos = extraerNumeroSeguro(item.porcion, 100.0)
            val vecesConsumidas = extraerNumeroSeguro(item.veces, 1.0)
            val factor = (porcionEnGramos / 100.0) * vecesConsumidas * getAjusteFrecuencia(item.frecuencia)

            totalKcal += (item.kcal_totales.toDoubleOrNull() ?: 0.0) * factor
            totalCarbs += (item.carbohidratos.toDoubleOrNull() ?: 0.0) * factor
            totalProts += (item.proteinas.toDoubleOrNull() ?: 0.0) * factor
            totalCols += (item.colesterol.toDoubleOrNull() ?: 0.0) * factor
            totalFibs += (item.fibra.toDoubleOrNull() ?: 0.0) * factor
        }

        val totalItemsCount = detalles.size.toDouble()
        val promKcal = if (totalItemsCount > 0) totalKcal / totalItemsCount else 0.0
        val promCarbs = if (totalItemsCount > 0) totalCarbs / totalItemsCount else 0.0
        val promProts = if (totalItemsCount > 0) totalProts / totalItemsCount else 0.0
        val promCols = if (totalItemsCount > 0) totalCols / totalItemsCount else 0.0
        val promFibs = if (totalItemsCount > 0) totalFibs / totalItemsCount else 0.0

        val multiplier = when (tf.lowercase()) {
            "diaria", "diariamente" -> 1.0
            "semanal", "semanalmente" -> 7.0
            "mensual", "mensualmente" -> 30.0
            "anual", "anualmente" -> 365.0
            else -> 1.0
        }

        return mapOf(
            "Calorías" to promKcal * multiplier,
            "Carbohidr." to promCarbs * multiplier,
            "Proteínas" to promProts * multiplier,
            "Colesterol" to promCols * multiplier,
            "Fibras" to promFibs * multiplier
        )
    }

    private fun calculateMacros(detalles: List<AlimentoEncuestaDetalles>): Map<String, Double> {
        var totalCarbs = 0.0
        var totalProts = 0.0
        var totalFats = 0.0

        detalles.forEach { item ->
            val porcionEnGramos = extraerNumeroSeguro(item.porcion, 100.0)
            val vecesConsumidas = extraerNumeroSeguro(item.veces, 1.0)
            val factor = (porcionEnGramos / 100.0) * vecesConsumidas * getAjusteFrecuencia(item.frecuencia)

            totalCarbs += (item.carbohidratos.toDoubleOrNull() ?: 0.0) * factor
            totalProts += (item.proteinas.toDoubleOrNull() ?: 0.0) * factor
            totalFats += (item.grasas.toDoubleOrNull() ?: 0.0) * factor
        }

        return mapOf(
            "Carbohidratos" to totalCarbs,
            "Proteínas" to totalProts,
            "Grasas" to totalFats
        )
    }

    private fun getAjusteFrecuencia(frecuencia: String): Double {
        return when (frecuencia.lowercase()) {
            "diaria", "diariamente" -> 1.0
            "semanal", "semanalmente" -> 1.0 / 7.0
            "mensual", "mensualmente" -> 1.0 / 30.0
            "anual", "anualmente" -> 1.0 / 365.0
            else -> 1.0
        }
    }

    private fun extraerNumeroSeguro(texto: String, valorPorDefecto: Double = 1.0): Double {
        return try {
            val numeroLimpio = texto.replace(Regex("[^0-9.]"), " ").trim().split(" ")[0]
            if (numeroLimpio.isEmpty()) valorPorDefecto else numeroLimpio.toDouble()
        } catch (e: Exception) {
            valorPorDefecto
        }
    }
}
