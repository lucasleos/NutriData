package unpsjb.ing.tntpm2024.util

import android.content.Context
import unpsjb.ing.tntpm2024.R
import java.util.Locale

object FoodImageUtils {
    fun getFoodDrawableResId(context: Context, nombre: String, imagenNombre: String? = null): Int {
        val resources = context.resources
        val packageName = context.packageName

        if (!imagenNombre.isNullOrEmpty()) {
            val resId = resources.getIdentifier(imagenNombre, "drawable", packageName)
            if (resId != 0) return resId
        }

        val nombreLower = nombre.lowercase(Locale.getDefault())

        val drawableDirecto = when {
            nombreLower.contains("polvo") -> R.drawable.leche_polvo
            nombreLower.contains("fluida") || (nombreLower.contains("leche") && !nombreLower.contains("polvo")) -> R.drawable.leche_fluida
            nombreLower.contains("azul") || nombreLower.contains("semidura") -> R.drawable.queso_azul
            nombreLower.contains("duro") || nombreLower.contains("dura") -> R.drawable.queso_duro
            nombreLower.contains("empanada") -> R.drawable.empanada_carne
            nombreLower.contains("carne") && nombreLower.contains("vacuna") -> R.drawable.carne_vacuna
            nombreLower.contains("frito") -> R.drawable.huevo_frito
            nombreLower.contains("huevo") -> R.drawable.huevo
            nombreLower.contains("manteca") -> R.drawable.manteca
            nombreLower.contains("banana") -> R.drawable.banana
            else -> 0
        }
        if (drawableDirecto != 0) return drawableDirecto

        if (nombre.isNotEmpty()) {
            val nombreNormalizado = nombreLower
                .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u").replace("ñ", "n")
                .replace(" ", "_")
                .replace("[^a-z0-9_]".toRegex(), "")

            val resIdNormalizado = resources.getIdentifier(nombreNormalizado, "drawable", packageName)
            if (resIdNormalizado != 0) return resIdNormalizado
        }

        return R.drawable.ic_food_logo
    }
}
