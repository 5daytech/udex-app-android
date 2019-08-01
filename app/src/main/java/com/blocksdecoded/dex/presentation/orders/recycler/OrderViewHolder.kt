package com.blocksdecoded.dex.presentation.orders.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.presentation.orders.model.UiOrder

class OrderViewHolder(
        view: View,
        private val listener: Listener
): RecyclerView.ViewHolder(view) {

    init {
        itemView.setOnClickListener { listener.onClick(adapterPosition) }
    }

    fun onBind(order: UiOrder) {

    }

    interface Listener {
        fun onClick(position: Int)
    }
}