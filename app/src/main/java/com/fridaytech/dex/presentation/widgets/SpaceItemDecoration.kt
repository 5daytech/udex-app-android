package com.fridaytech.dex.presentation.widgets

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(
    val top: Int = 0,
    val bottom: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = 0
        outRect.bottom = 0

        when(parent.getChildAdapterPosition(view)) {
            0 -> outRect.top = top
            (parent.adapter?.itemCount ?: 0) - 1 -> outRect.bottom = bottom
        }
    }

}