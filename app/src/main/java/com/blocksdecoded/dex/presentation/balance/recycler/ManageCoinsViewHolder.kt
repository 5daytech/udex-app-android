package com.blocksdecoded.dex.presentation.balance.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ManageCoinsViewHolder(
    view: View,
    private val listener: Listener
): RecyclerView.ViewHolder(view) {
    init { itemView.setOnClickListener { listener.onClick() } }

    interface Listener {
        fun onClick()
    }
}