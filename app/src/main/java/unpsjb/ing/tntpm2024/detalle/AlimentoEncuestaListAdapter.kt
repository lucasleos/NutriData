package unpsjb.ing.tntpm2024.detalle

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tntpm2024.R

class AlimentoEncuestaListAdapter(private val context: Context) : RecyclerView.Adapter<AlimentoEncuestaListAdapter.AlimentoViewHolder>() {

    private var alimentosEncuestaDetalle = emptyList<AlimentoEncuestaDetalles>()

    class AlimentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdAlimento : TextView = itemView.findViewById(R.id.tvIdAlimento)
        val tvNombreAlimento : TextView = itemView.findViewById(R.id.tvNombreAlimento)
        val tvPorcion : TextView = itemView.findViewById(R.id.tvEncuestaCompletada)
        val tvFrecuencia : TextView = itemView.findViewById(R.id.tvFrecuencia)
        val tvVeces :  TextView = itemView.findViewById(R.id.tvVeces)
        val medida: TextView = itemView.findViewById(R.id.tvMedida)
        val categoria: TextView = itemView.findViewById(R.id.tvCategoria)
        val porcentajeGraso: TextView = itemView.findViewById(R.id.tvPorcentajeGraso)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlimentoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_alimento_encuesta, parent, false)
        return AlimentoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlimentoViewHolder, position: Int) {
        val alimentoEncuestaDetalle = alimentosEncuestaDetalle[position]
        holder.tvIdAlimento.text = "Alimento N° ${alimentoEncuestaDetalle.alimentoId.toString()}"
        holder.tvNombreAlimento.text = "Alimento: ${alimentoEncuestaDetalle.nombre}"
        holder.tvPorcion.text = "Porcion: ${alimentoEncuestaDetalle.porcion}"
        holder.tvFrecuencia.text = "Frecuencia: ${alimentoEncuestaDetalle.frecuencia}"
        holder.tvVeces.text = "Veces Consumido: ${alimentoEncuestaDetalle.veces}"
        holder.medida.text = "Medida: ${alimentoEncuestaDetalle.medida}"
        holder.categoria.text = "Categoria : ${alimentoEncuestaDetalle.categoria}"
        holder.porcentajeGraso.text = "Porcentaje Graso: ${alimentoEncuestaDetalle.porcentaje_graso}"
    }

    override fun getItemCount() = alimentosEncuestaDetalle.size

    fun setAlimentos(alimentosEncuesta: List<AlimentoEncuestaDetalles>) {
        this.alimentosEncuestaDetalle = alimentosEncuesta
        notifyDataSetChanged()
    }
}