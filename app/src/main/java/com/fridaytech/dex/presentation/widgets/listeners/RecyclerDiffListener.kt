package com.fridaytech.dex.presentation.widgets.listeners

import androidx.recyclerview.widget.RecyclerView

class RecyclerDiffListener(
    private val recyclerView: RecyclerView
) : RecyclerView.AdapterDataObserver() {
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        if (itemCount > 0) {
            recyclerView.smoothScrollToPosition(0)
        }
    }
}
