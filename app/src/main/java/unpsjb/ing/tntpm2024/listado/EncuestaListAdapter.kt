package unpsjb.ing.tnt.ligadeportiva.listado.listado

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.Encuesta


class EncuestaListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<EncuestaListAdapter.EncuestaViewHolder>() {


    var onItemClick: ((Encuesta) -> Unit)? = null

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var encuestas = emptyList<Encuesta>() // Copia cache de los encuestas

    inner class EncuestaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val encuestaItemView: TextView = itemView.findViewById(R.id.tvTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EncuestaViewHolder {
        val itemView = inflater.inflate(R.layout.item_layout, parent, false)
        return EncuestaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EncuestaViewHolder, position: Int) {
        val encuesta = encuestas[position]
        val text = "${encuesta.alimento} ${encuesta.porcion} ${encuesta.frecuencia} ${encuesta.veces}"
        holder.encuestaItemView.text = text

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(encuesta)
        }
    }

    internal fun setEncuestas(encuestas: List<Encuesta>) {
        this.encuestas = encuestas
        notifyDataSetChanged()
    }

    override fun getItemCount() = encuestas.size
}

