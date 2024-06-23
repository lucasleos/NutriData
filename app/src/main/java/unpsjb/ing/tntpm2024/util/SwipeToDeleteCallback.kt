package unpsjb.ing.tntpm2024.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tntpm2024.R

import unpsjb.ing.tntpm2024.listado.EncuestaListAdapter


class SwipeToDeleteCallback(
    val context: Context,
    private val adapter: EncuestaListAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

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
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val background = Paint().apply { color = Color.RED }
        val icon = ContextCompat.getDrawable(context, R.drawable.baseline_delete_black_24dp)!!

        val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
        val iconTop = itemView.top + iconMargin
        val iconBottom = iconTop + icon.intrinsicHeight
        val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
        val iconRight = itemView.right - iconMargin

        if (dX < 0) { // Swipe to the left
            val left = itemView.right + dX
            val right = itemView.right.toFloat()
            val top = itemView.top.toFloat()
            val bottom = itemView.bottom.toFloat()

            c.drawRect(left, top, right, bottom, background)
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            icon.draw(c)
        }
    }
}
