package unpsjb.ing.tntpm2024.turnos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tntpm2024.basededatos.entidades.Turno
import unpsjb.ing.tntpm2024.databinding.ItemTurnoSolicitadoBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TurnosAdapter(
    private var turnos: List<Turno>,
    private val onAsignarClick: (Turno) -> Unit
) : RecyclerView.Adapter<TurnosAdapter.TurnoViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    inner class TurnoViewHolder(val binding: ItemTurnoSolicitadoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurnoViewHolder {
        val binding = ItemTurnoSolicitadoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TurnoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TurnoViewHolder, position: Int) {
        val turno = turnos[position]
        holder.binding.tvVoluntarioId.text = "Voluntario: ${turno.voluntarioId.take(8)}..."
        holder.binding.tvFechaSolicitud.text = "Solicitado: ${dateFormat.format(Date(turno.fechaSolicitud))}"

        holder.binding.btnAsignarTurno.setOnClickListener {
            onAsignarClick(turno)
        }
    }

    override fun getItemCount(): Int = turnos.size

    fun actualizarLista(nuevaLista: List<Turno>) {
        turnos = nuevaLista
        notifyDataSetChanged()
    }
}