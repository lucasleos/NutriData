package unpsjb.ing.tntpm2024.estadistica

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import unpsjb.ing.tntpm2024.AndroidApp
import unpsjb.ing.tntpm2024.basededatos.AlimentoEncuestaRepository
import unpsjb.ing.tntpm2024.detalle.AlimentoEncuestaDetalles
import java.util.Locale

data class TopFood(
    val rank: Int,
    val name: String,
    val count: Int,
    val drawableResId: Int
)

class EstadisticaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database =
        (application as AndroidApp).database

    private val repository =
        AlimentoEncuestaRepository(
            database.alimentoEncuestaDao()
        )

    private val encuestaRepository =
        unpsjb.ing.tntpm2024.basededatos.Repository(
            database.encuestaDAO
        )

    val zoneCounts: LiveData<Map<String, Int>> =
        encuestaRepository.allEncuestas.map { encuestas ->
            encuestas
                .asSequence()
                .map { it.zona.trim() }
                .filter { it.isNotEmpty() }
                .groupingBy { it }
                .eachCount()
        }

    val alimentoEncuestaDetalles:
            LiveData<List<AlimentoEncuestaDetalles>> =
        repository.alimentoEncuestaDetallesLiveData

    private val _timeFrame =
        MutableLiveData("Diaria")

    val timeFrame: LiveData<String> = _timeFrame

    val chartData =
        MediatorLiveData<Map<String, Double>>().apply {
            addSource(alimentoEncuestaDetalles) {
                value = calculateStats(
                    it,
                    _timeFrame.value ?: "Diaria"
                )
            }

            addSource(_timeFrame) {
                value = calculateStats(
                    alimentoEncuestaDetalles.value.orEmpty(),
                    it ?: "Diaria"
                )
            }
        }

    val macroData =
        alimentoEncuestaDetalles.map { detalles ->
            calculateMacros(detalles)
        }

    val totalItems: LiveData<Int> =
        alimentoEncuestaDetalles.map { detalles ->
            detalles.size
        }

    val avgKcal: LiveData<Double> =
        MediatorLiveData<Double>().apply {
            fun update() {
                val detalles =
                    alimentoEncuestaDetalles.value.orEmpty()

                val periodo =
                    _timeFrame.value ?: "Diaria"

                value = calculateStats(
                    detalles,
                    periodo
                )["Calorías"] ?: 0.0
            }

            addSource(alimentoEncuestaDetalles) {
                update()
            }

            addSource(_timeFrame) {
                update()
            }
        }

    val topFood: LiveData<String> =
        alimentoEncuestaDetalles.map { detalles ->
            detalles
                .asSequence()
                .map { it.nombre.trim() }
                .filter { it.isNotEmpty() }
                .groupingBy { it }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key
                ?: "N/A"
        }

    val topFoods: LiveData<List<TopFood>> =
        alimentoEncuestaDetalles.map { detalles ->
            val counts = detalles
                .asSequence()
                .map { it.nombre.trim() }
                .filter { it.isNotEmpty() }
                .groupingBy { it }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .take(3)

            val context = getApplication<Application>()

            val result = mutableListOf<TopFood>()
            for (i in 0 until 3) {
                val entry = counts.getOrNull(i)
                if (entry != null) {
                    val foodName = entry.key
                    val count = entry.value
                    val drawableId = unpsjb.ing.tntpm2024.util.FoodImageUtils.getFoodDrawableResId(context, foodName)
                    result.add(TopFood(i + 1, foodName, count, drawableId))
                } else {
                    result.add(TopFood(i + 1, "N/A", 0, unpsjb.ing.tntpm2024.R.drawable.ic_food_logo))
                }
            }
            result
        }

    fun setTimeFrame(timeFrame: String) {
        if (_timeFrame.value != timeFrame) {
            _timeFrame.value = timeFrame
        }
    }

    private fun calculateStats(
        detalles: List<AlimentoEncuestaDetalles>,
        timeFrame: String
    ): Map<String, Double> {
        if (detalles.isEmpty()) {
            return linkedMapOf(
                "Calorías" to 0.0,
                "Carbohidr." to 0.0,
                "Proteínas" to 0.0,
                "Colesterol" to 0.0,
                "Fibras" to 0.0
            )
        }

        var totalKcal = 0.0
        var totalCarbs = 0.0
        var totalProteins = 0.0
        var totalCholesterol = 0.0
        var totalFiber = 0.0

        detalles.forEach { item ->
            val factor = calculateFactor(item)

            totalKcal += parseNumber(item.kcal_totales) * factor
            totalCarbs += parseNumber(item.carbohidratos) * factor
            totalProteins += parseNumber(item.proteinas) * factor
            totalCholesterol += parseNumber(item.colesterol) * factor
            totalFiber += parseNumber(item.fibra) * factor
        }

        val count = detalles.size.toDouble()

        val averageKcal = totalKcal / count
        val averageCarbs = totalCarbs / count
        val averageProteins = totalProteins / count
        val averageCholesterol = totalCholesterol / count
        val averageFiber = totalFiber / count

        val multiplier = when (
            timeFrame.trim().lowercase(Locale.ROOT)
        ) {
            "diaria", "diariamente" -> 1.0
            "semanal", "semanalmente" -> 7.0
            "mensual", "mensualmente" -> 30.0
            "anual", "anualmente" -> 365.0
            else -> 1.0
        }

        return linkedMapOf(
            "Calorías" to averageKcal * multiplier,
            "Carbohidr." to averageCarbs * multiplier,
            "Proteínas" to averageProteins * multiplier,
            "Colesterol" to averageCholesterol * multiplier,
            "Fibras" to averageFiber * multiplier
        )
    }

    private fun calculateMacros(
        detalles: List<AlimentoEncuestaDetalles>
    ): Map<String, Double> {
        var totalCarbs = 0.0
        var totalProteins = 0.0
        var totalFats = 0.0

        detalles.forEach { item ->
            val factor = calculateFactor(item)

            totalCarbs += parseNumber(item.carbohidratos) * factor
            totalProteins += parseNumber(item.proteinas) * factor
            totalFats += parseNumber(item.grasas) * factor
        }

        return linkedMapOf(
            "Carbohidratos" to totalCarbs,
            "Proteínas" to totalProteins,
            "Grasas" to totalFats
        )
    }

    private fun calculateFactor(
        item: AlimentoEncuestaDetalles
    ): Double {
        val portionInGrams =
            parseNumber(item.porcion, 100.0)

        val timesConsumed =
            parseNumber(item.veces, 1.0)

        return (portionInGrams / 100.0) *
                timesConsumed *
                frequencyAdjustment(item.frecuencia)
    }

    private fun frequencyAdjustment(
        frequency: String
    ): Double {
        return when (
            frequency.trim().lowercase(Locale.ROOT)
        ) {
            "diaria", "diariamente" -> 1.0
            "semanal", "semanalmente" -> 1.0 / 7.0
            "mensual", "mensualmente" -> 1.0 / 30.0
            "anual", "anualmente" -> 1.0 / 365.0
            else -> 1.0
        }
    }

    private fun parseNumber(
        text: String?,
        defaultValue: Double = 0.0
    ): Double {
        if (text.isNullOrBlank()) {
            return defaultValue
        }

        val normalizedText = text
            .trim()
            .replace(",", ".")
            .replace(
                Regex("[^0-9.\\-]"),
                ""
            )

        return normalizedText.toDoubleOrNull()
            ?: defaultValue
    }
}
