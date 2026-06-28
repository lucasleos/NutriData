package unpsjb.ing.tntpm2024.listado

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
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

    // Lista original sin alterar (Cache)
    private var encuestasOriginales = mutableListOf<Encuesta>()

    // Lista que realmente se muestra y se manda al DiffUtil
    private var encuestasVisibles = mutableListOf<Encuesta>()

    private var filtroCompletada: Boolean? = null
    private var filtroZona: String? = null

    inner class EncuestaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val imageUpload: ImageView = itemView.findViewById(R.id.imageUpload)
        val encuestaIdTextView: TextView = itemView.findViewById(R.id.tvEncuestaId)
        val fechaTextView: TextView = itemView.findViewById(R.id.tvFecha)
        val encuestaCompletadaTextView: TextView = itemView.findViewById(R.id.tvEncuestaCompletada)
        val zonaTextView: TextView = itemView.findViewById(R.id.tvzona)
        val imageCheck: ImageView = itemView.findViewById(R.id.ivCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EncuestaViewHolder {
        val itemView = inflater.inflate(R.layout.item_layout, parent, false)
        return EncuestaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EncuestaViewHolder, position: Int) {
        val encuesta = encuestasVisibles[position]
        val fechaLocalDateTime: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(encuesta.fecha), ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        holder.encuestaIdTextView.text = "Encuesta N° ${encuesta.encuestaId}"
        holder.fechaTextView.text = fechaLocalDateTime.format(formatter)
        holder.encuestaCompletadaTextView.text = if (encuesta.encuestaCompletada) "Completa" else "Incompleta"
        holder.zonaTextView.text = encuesta.zona

        if (encuesta.encuestaCompletada) {
            holder.imageView.setImageResource(R.drawable.view_ico)
            holder.imageUpload.setImageResource(R.drawable.cloud_upload_icon)
            holder.imageCheck.setImageResource(R.drawable.check)
        } else {
            holder.imageView.setImageResource(R.drawable.edit_ico)
            holder.imageCheck.setImageResource(R.drawable.wrong)
        }

        holder.imageView.setOnClickListener {
            if (encuesta.encuestaCompletada) {
                onItemClick?.invoke(encuesta)
            } else {
                onItemClickEditEncuesta?.invoke(encuesta)
            }
        }

        holder.imageUpload.setOnClickListener {
            onItemClickUploadInCloud?.invoke(encuesta)
        }
    }

    override fun getItemCount(): Int = encuestasVisibles.size

    internal fun setEncuestas(nuevasEncuestas: List<Encuesta>) {
        this.encuestasOriginales = nuevasEncuestas.toMutableList()
        aplicarFiltros()
    }

    fun filterEncuestas(completada: Boolean?, zona: String?) {
        filtroCompletada = completada
        filtroZona = zona
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val listaFiltrada = encuestasOriginales.filter {
            (filtroCompletada == null || it.encuestaCompletada == filtroCompletada) &&
                    (filtroZona == null || it.zona == filtroZona)
        }

        // Uso de DiffUtil para calcular las animaciones
        val diffCallback = EncuestaDiffCallback(encuestasVisibles, listaFiltrada)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        encuestasVisibles.clear()
        encuestasVisibles.addAll(listaFiltrada)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getEncuestaAt(position: Int): Encuesta {
        return encuestasVisibles[position]
    }

    fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(context).apply {
            setTitle("Confirmar eliminación")
            setMessage("¿Está seguro que desea borrar la encuesta?")
            setPositiveButton("Confirmar") { dialog, _ ->
                removeAt(position)
                dialog.dismiss()
            }
            setNegativeButton("Cancelar") { dialog, _ ->
                notifyItemChanged(position) // Revierte el swipe visualmente si cancela
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun removeAt(position: Int) {
        val encuestaAEliminar = encuestasVisibles[position]
        // Se notifica al Fragment para borrarla de la Base de Datos
        onSwipToDeleteCallback?.invoke(encuestaAEliminar)
    }
}

class EncuestaDiffCallback(
    private val oldList: List<Encuesta>,
    private val newList: List<Encuesta>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    // Verifica si es el mismo elemento (Mismo ID)
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].encuestaId == newList[newItemPosition].encuestaId
    }

    // Verifica si el contenido cambió (Por ejemplo, si se completó)
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}