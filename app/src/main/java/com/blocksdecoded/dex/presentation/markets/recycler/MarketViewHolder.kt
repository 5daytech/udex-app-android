package com.blocksdecoded.dex.presentation.markets.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.core.model.Market

class MarketViewHolder(
	view: View,
	private val listener: Listener
): RecyclerView.ViewHolder(view) {
	
	init {
		itemView.setOnClickListener { listener.onClick(adapterPosition) }
	}
	
	fun onBind(market: Market) {
	
	}
	
	interface Listener {
		fun onClick(position: Int)
	}
}