package unpsjb.ing.tntpm2024.listadoFireBase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentosEnEncuestas
import unpsjb.ing.tntpm2024.databinding.ItemChildAlimentoFirebaseBinding
import unpsjb.ing.tntpm2024.databinding.ItemGroupEncuestaAliementosFirebaseBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpandableRecyclerViewAdapter(
    private val context: Context,
    private val parentItemList: List<AlimentosEnEncuestas>
) : RecyclerView.Adapter<ExpandableRecyclerViewAdapter.ParentViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    inner class ParentViewHolder(val binding: ItemGroupEncuestaAliementosFirebaseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val binding = ItemGroupEncuestaAliementosFirebaseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ParentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parentItem = parentItemList[position]
        val binding = holder.binding

        // Title: Encuesta ID
        binding.groupTitleEncuesta.text = "Encuesta #${parentItem.encuesta.encuestaId}"

        // Alimentos count
        val count = parentItem.alimentos.size
        binding.tvAlimentosCount.text = if (count == 1) "1 alimento" else "$count alimentos"

        // Encuestador
        val userNickName = parentItem.encuesta.userEmail?.substringBefore("@") ?: "Anónimo"
        binding.tvEncuestador.text = "Encuestador: $userNickName"

        // Date
        val fechaText = if (parentItem.encuesta.fecha > 0) {
            dateFormat.format(Date(parentItem.encuesta.fecha))
        } else {
            "Sin fecha"
        }
        binding.tvFecha.text = "Fecha: $fechaText"

        // Zone
        val zonaText = parentItem.encuesta.zona.ifEmpty { "Sin especificar" }
        binding.tvZona.text = "Zona: $zonaText"

        // Child adapter setup
        val childAdapter = ChildAdapter(parentItem.alimentos)
        binding.childRecyclerView.adapter = childAdapter
        binding.childRecyclerView.layoutManager = LinearLayoutManager(context)

        // Expand / Collapse state
        val isExpanded = expandedPositions.contains(position)
        binding.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
        binding.expandButton.setImageResource(if (isExpanded) R.drawable.drop_up_ico else R.drawable.drop_down_ico)

        val toggleExpand = View.OnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (expandedPositions.contains(adapterPosition)) {
                    expandedPositions.remove(adapterPosition)
                } else {
                    expandedPositions.add(adapterPosition)
                }
                notifyItemChanged(adapterPosition)
            }
        }

        binding.parentHeaderLayout.setOnClickListener(toggleExpand)
        binding.expandButton.setOnClickListener(toggleExpand)
    }

    override fun getItemCount(): Int = parentItemList.size
}

class ChildAdapter(private val childItemList: List<Alimento>) :
    RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

    inner class ChildViewHolder(val binding: ItemChildAlimentoFirebaseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val binding = ItemChildAlimentoFirebaseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChildViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val childItem = childItemList[position]
        val binding = holder.binding

        binding.childNombre.text = childItem.nombre.ifEmpty { "Sin nombre" }
        
        val categoria = childItem.categoria.ifEmpty { "Sin categoría" }
        val medida = childItem.medida.ifEmpty { "Unidad" }
        binding.childCategoriaMedida.text = "$categoria • $medida"

        binding.childMacros.text = "Carb: ${childItem.carbohidratos}g | Prot: ${childItem.proteinas}g | Grasas: ${childItem.grasas}g"
        binding.childKcal.text = "${childItem.kcal.toInt()} kcal"

        // Food thumbnail image binding
        val resId = obtenerResIdImagen(holder.itemView.context, childItem)
        binding.ivAlimentoChild.setImageResource(resId)
    }

    private fun obtenerResIdImagen(context: Context, alimento: Alimento): Int {
        return unpsjb.ing.tntpm2024.util.FoodImageUtils.getFoodDrawableResId(context, alimento.nombre, alimento.imagenNombre)
    }

    override fun getItemCount(): Int = childItemList.size
}
