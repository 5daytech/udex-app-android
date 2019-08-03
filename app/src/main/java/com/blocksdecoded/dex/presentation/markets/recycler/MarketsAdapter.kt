package com.blocksdecoded.dex.presentation.markets.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.Market

class MarketsAdapter(
	private val listener: MarketViewHolder.Listener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	
	private val markets = ArrayList<Market>()
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
		MarketViewHolder(LayoutInflater.from(parent.context)
			.inflate(R.layout.item_market, parent, false), listener)
	
	override fun getItemCount(): Int = markets.size
	
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		when(holder) {
			is MarketViewHolder -> holder.onBind(markets[position])
		}
	}
	
	fun setMarkets(markets: List<Market>) {
		this.markets.clear()
		this.markets.addAll(markets)
		notifyDataSetChanged()
	}
}