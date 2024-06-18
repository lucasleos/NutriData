package unpsjb.ing.tntpm2024.listado

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tntpm2024.R

import android.animation.ValueAnimator
class SwipeToDeleteCallback(
    private val adapter: EncuestaListAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private val deleteIcon: Drawable? = ContextCompat.getDrawable(adapter.context, R.drawable.baseline_delete_black_24dp)
    private val intrinsicWidth = deleteIcon?.intrinsicWidth ?: 0
    private val intrinsicHeight = deleteIcon?.intrinsicHeight ?: 0
    private val background = ColorDrawable(Color.RED)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        adapter.showDeleteConfirmationDialog(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        // Dibujar el fondo rojo
        background.setBounds(
            itemView.left, itemView.top,
            itemView.left + dX.toInt(), itemView.bottom
        )
        background.draw(c)

        // Calcular la posición del ícono de eliminación
        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft = itemView.left + deleteIconMargin
        val deleteIconRight = deleteIconLeft + intrinsicWidth
        val deleteIconBottom = deleteIconTop + intrinsicHeight

        // Dibujar el ícono de eliminación
        deleteIcon?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteIcon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
