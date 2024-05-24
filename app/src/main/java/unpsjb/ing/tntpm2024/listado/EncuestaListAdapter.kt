package unpsjb.ing.tnt.listado.listado

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.encuestas.Encuesta


class EncuestaListAdapter internal constructor(
    val context: Context
) : RecyclerView.Adapter<EncuestaListAdapter.EncuestaViewHolder>() {

    var onItemClick: ((Encuesta) -> Unit)? = null
    var onItemClickEditEncuesta: ((Encuesta) -> Unit)? = null
    var onSwipToDeleteCallback: ((Encuesta) -> Unit)? = null

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    //private var encuestas = emptyList<Encuesta>() // Copia cache de los encuestas
    private var encuestas = mutableListOf<Encuesta>() // Copia cache de los encuestas

    inner class EncuestaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView : ImageView
        val encuestaItemView: TextView = itemView.findViewById(R.id.tvTitle)

        init {
            imageView = itemView.findViewById(R.id.imageView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EncuestaViewHolder {
        val itemView = inflater.inflate(R.layout.item_layout, parent, false)
        return EncuestaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EncuestaViewHolder, position: Int) {

        val encuesta = encuestas[position]
        val text = "${encuesta.alimento} ${encuesta.porcion} ${encuesta.frecuencia} ${encuesta.veces} ${encuesta.encuestaId}"
        holder.encuestaItemView.text = text

        if(encuesta.encuestaCompletada) {
            holder.imageView.setImageResource(R.drawable.view_ico)
        }else{
            holder.imageView.setImageResource(R.drawable.edit_ico)
        }

      //  holder.itemView.setOnClickListener{
       //     onItemClick?.invoke(encuesta)
       // }

        holder.imageView.setOnClickListener {
            if(encuesta.encuestaCompletada) {
                // navegar a detail
                onItemClick?.invoke(encuesta)
            }else{
               // navegar a editar, pasarle parametros de como estaba la encuesta
                onItemClickEditEncuesta?.invoke(encuesta)
            }
        }
    }


    internal fun setEncuestas(encuestas: List<Encuesta>) {
        //this.encuestas = encuestas
        this.encuestas = encuestas.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount() = encuestas.size

    fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(context).apply {
            setTitle("Confirmar eliminacion")
            setMessage("Esta seguro que desea borrar la encuesta?")
            setPositiveButton("Confirmar"){
                dialog, wich -> removeAt(position)
            }
            setNegativeButton("Cancelar"){
                dialog, wich -> dialog.dismiss()
            }
            create()
            show()
        }
        notifyItemRangeChanged(position, itemCount) // actualiza las posiciones de los elementos restantes
    }

    private fun removeAt(position: Int) {
        val encuesta = encuestas[position]
        encuestas.removeAt(position)
        onSwipToDeleteCallback?.invoke(encuesta)
        notifyItemRemoved(position)
    }

}

