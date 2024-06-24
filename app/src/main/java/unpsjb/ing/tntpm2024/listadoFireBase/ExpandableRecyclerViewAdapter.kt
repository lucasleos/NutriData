
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.entidades.Alimento
import unpsjb.ing.tntpm2024.basededatos.entidades.AlimentosEnEncuestas

class ExpandableRecyclerViewAdapter(
    private val context: Context,
    private val parentItemList: List<AlimentosEnEncuestas>
) : RecyclerView.Adapter<ExpandableRecyclerViewAdapter.ParentViewHolder>() {

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitleEncuesta: TextView = itemView.findViewById(R.id.groupTitleEncuesta)
        val recyclerViewChild: RecyclerView = itemView.findViewById(R.id.childRecyclerView)
        val expandButton: ImageButton = itemView.findViewById(R.id.expandButton)
    }

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewChildNombre: TextView = itemView.findViewById(R.id.childNombre)
        val textViewChildMedida: TextView = itemView.findViewById(R.id.childMedida)
        val textViewChildCategoria: TextView = itemView.findViewById(R.id.childCategoria)
        val textViewChildPorcentajeGraso: TextView = itemView.findViewById(R.id.childPorcentajeGraso)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group_encuesta_aliementos_firebase, parent, false)
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
            val parentItem = parentItemList[position]

            holder.tvTitleEncuesta.text =
                "Encuesta ID: ${parentItem.encuesta.encuestaId}, Zona: ${parentItem.encuesta.zona}"

            val childAdapter = ChildAdapter(parentItem.alimentos)
            holder.recyclerViewChild.adapter = childAdapter
            holder.recyclerViewChild.layoutManager = LinearLayoutManager(context)

            holder.expandButton.setOnClickListener {
                if (holder.recyclerViewChild.visibility == View.VISIBLE) {
                    holder.recyclerViewChild.visibility = View.GONE
                    holder.expandButton.setImageResource(R.drawable.drop_down_ico)
                } else {
                    holder.recyclerViewChild.visibility = View.VISIBLE
                    holder.expandButton.setImageResource(R.drawable.drop_up_ico)
                }
            }
    }

    override fun getItemCount(): Int {
        return parentItemList.size
    }

}

class ChildAdapter(private val childItemList: List<Alimento>) : RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewChildNombre: TextView = itemView.findViewById(R.id.childNombre)
        val textViewChildMedida: TextView = itemView.findViewById(R.id.childMedida)
        val textViewChildCategoria: TextView = itemView.findViewById(R.id.childCategoria)
        val textViewChildPorcentajeGraso: TextView = itemView.findViewById(R.id.childPorcentajeGraso)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_child_alimento_firebase, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val childItem = childItemList[position]
        holder.textViewChildNombre.text =  "Alimento: ${childItem.nombre} "
        holder.textViewChildMedida.text = "Medida: ${ childItem.medida} "
        holder.textViewChildCategoria.text = "Categoria: ${ childItem.categoria} "
        holder.textViewChildPorcentajeGraso.text = "Porcentaje Graso: ${ childItem.porcentajeGraso} "
    }

    override fun getItemCount(): Int {
        return childItemList.size
    }
}