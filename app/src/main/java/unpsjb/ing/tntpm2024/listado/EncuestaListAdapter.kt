package unpsjb.ing.tntpm2024.listado

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tntpm2024.R
import unpsjb.ing.tntpm2024.basededatos.entidades.Encuesta
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class EncuestaListAdapter internal constructor(
    val context: Context
) : RecyclerView.Adapter<EncuestaListAdapter.EncuestaViewHolder>() {

    var onItemClick: ((Encuesta) -> Unit)? = null
    var onItemClickEditEncuesta: ((Encuesta) -> Unit)? = null
    var onSwipToDeleteCallback: ((Encuesta) -> Unit)? = null
    var onItemClickUploadInCloud: ((Encuesta) -> Unit)? = null

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    //private var encuestas = emptyList<Encuesta>() // Copia cache de los encuestas
    private var encuestas = mutableListOf<Encuesta>() // Copia cache de los encuestas

    inner class EncuestaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val imageUpload: ImageView = itemView.findViewById(R.id.imageUpload)
        val encuestaIdTextView: TextView = itemView.findViewById(R.id.tvEncuestaId)
        val fechaTextView: TextView = itemView.findViewById(R.id.tvFecha)
        val encuestaCompletadaTextView: TextView = itemView.findViewById(R.id.tvEncuestaCompletada)
        val zonaTextView: TextView = itemView.findViewById(R.id.tvzona)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EncuestaViewHolder {
        val itemView = inflater.inflate(R.layout.item_layout, parent, false)
        return EncuestaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EncuestaViewHolder, position: Int) {
        val encuesta = encuestas[position]

        val fechaLong: Long = encuesta.fecha

        val fechaLocalDateTime: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(fechaLong), ZoneId.systemDefault())

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val fechaFormateada: String = fechaLocalDateTime.format(formatter)


        holder.encuestaIdTextView.text = "Encuesta N° ${encuesta.encuestaId}"
        holder.fechaTextView.text = "Fecha: $fechaFormateada"
        holder.encuestaCompletadaTextView.text = if (encuesta.encuestaCompletada) "Encuesta completada" else "Encuesta incompleta"
        holder.zonaTextView.text = encuesta.zona

        if (encuesta.encuestaCompletada) {
            holder.imageView.setImageResource(R.drawable.view_ico)
            holder.imageUpload.setImageResource(R.drawable.cloud_upload_icon)
        } else {
            holder.imageView.setImageResource(R.drawable.edit_ico)
        }

        holder.imageView.setOnClickListener {
            if (encuesta.encuestaCompletada) {
                onItemClick?.invoke(encuesta)
            } else {
                onItemClickEditEncuesta?.invoke(encuesta)
            }
        }

    /*    if (encuesta.encuestaSubidaANube) {
            holder.imageView.setImageResource(R.drawable.view_ico) // icono subida ok
        } else {
            holder.imageView.setImageResource(R.drawable.edit_ico) // icono subir
        }*/

        holder.imageUpload.setOnClickListener{
            onItemClickUploadInCloud?.invoke(encuesta)
//            Toast.makeText(context, "Encuesta subida a la nube", Toast.LENGTH_SHORT).show()
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
            setPositiveButton("Confirmar") { dialog, wich ->
                removeAt(position)
            }
            setNegativeButton("Cancelar") { dialog, wich ->
                dialog.dismiss()
            }
            create()
            show()
        }
        notifyItemRangeChanged(
            position,
            itemCount
        ) // actualiza las posiciones de los elementos restantes
    }

    private fun removeAt(position: Int) {
        val encuesta = encuestas[position]
        encuestas.removeAt(position)
        onSwipToDeleteCallback?.invoke(encuesta)
        notifyItemRemoved(position)
    }

}

