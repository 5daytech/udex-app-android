package com.blocksdecoded.dex.presentation.markets.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.markets.MarketViewItem
import com.blocksdecoded.dex.utils.inflate

class MarketsAdapter(
	private val listener: MarketViewHolder.Listener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val markets = ArrayList<MarketViewItem>()
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
		MarketViewHolder(parent.inflate(R.layout.item_market), listener)
	
	override fun getItemCount(): Int = markets.size
	
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		when(holder) {
			is MarketViewHolder -> holder.onBind(markets[position])
		}
	}
	
	fun setMarkets(markets: List<MarketViewItem>) {
		val diffUtil = DiffUtil.calculateDiff(MarketsDiffCallback(this.markets, markets))
		this.markets.clear()
		this.markets.addAll(markets)
		diffUtil.dispatchUpdatesTo(this)
	}
}