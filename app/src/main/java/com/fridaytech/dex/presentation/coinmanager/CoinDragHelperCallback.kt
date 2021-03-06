package com.fridaytech.dex.presentation.coinmanager

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class CoinDragHelperCallback(private var listener: Listener) : ItemTouchHelper.Callback() {

    private var dragFrom = -1
    private var dragTo = -1

    private val drawMovementFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN

    override fun isLongPressDragEnabled(): Boolean = false

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(drawMovementFlags, 0)
    }

    override fun canDropOver(recyclerView: RecyclerView, current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return current.itemViewType == target.itemViewType
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        if (dragFrom == -1) {
            dragFrom = fromPosition
        }
        dragTo = toPosition

        listener.onItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(recyclerView: RecyclerView.ViewHolder, position: Int) { }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
            listener.onItemMoveEnded(dragFrom, dragTo)
        }

        dragFrom = -1
        dragTo = -1
    }

    interface Listener {
        fun onItemMoved(from: Int, to: Int)
        fun onItemMoveEnded(from: Int, to: Int)
    }
}

interface IDragListener {
    fun requestDrag(viewHolder: RecyclerView.ViewHolder)
}
