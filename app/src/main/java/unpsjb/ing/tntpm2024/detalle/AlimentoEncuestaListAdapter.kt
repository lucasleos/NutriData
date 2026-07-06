package unpsjb.ing.tntpm2024.detalle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tntpm2024.databinding.ItemAlimentoEncuestaBinding

class AlimentoEncuestaListAdapter :
    ListAdapter<AlimentoEncuestaDetalles, AlimentoEncuestaListAdapter.AlimentoViewHolder>(AlimentoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlimentoViewHolder {
        return AlimentoViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AlimentoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class AlimentoViewHolder private constructor(private val binding: ItemAlimentoEncuestaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AlimentoEncuestaDetalles) {
            binding.alimento = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AlimentoViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAlimentoEncuestaBinding.inflate(layoutInflater, parent, false)
                return AlimentoViewHolder(binding)
            }
        }
    }
}

class AlimentoDiffCallback : DiffUtil.ItemCallback<AlimentoEncuestaDetalles>() {
    override fun areItemsTheSame(oldItem: AlimentoEncuestaDetalles, newItem: AlimentoEncuestaDetalles): Boolean {
        return oldItem.alimentoId == newItem.alimentoId && oldItem.encuestaId == newItem.encuestaId
    }

    override fun areContentsTheSame(oldItem: AlimentoEncuestaDetalles, newItem: AlimentoEncuestaDetalles): Boolean {
        return oldItem == newItem
    }
}
