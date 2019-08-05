package com.blocksdecoded.dex.presentation.markets.recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.Market
import com.blocksdecoded.dex.presentation.widgets.CoinIconImage
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat

class MarketViewHolder(
	view: View,
	private val listener: Listener
): RecyclerView.ViewHolder(view) {

	private val coinIcon: CoinIconImage = itemView.findViewById(R.id.market_coin_icon)
	private val nameTxt: TextView = itemView.findViewById(R.id.market_name)
	private val priceTxt: TextView = itemView.findViewById(R.id.market_price)
	private val codeTxt: TextView = itemView.findViewById(R.id.market_code)
	private val changeImg: ImageView = itemView.findViewById(R.id.market_change_img)

	init {
		itemView.setOnClickListener { listener.onClick(adapterPosition) }
	}
	
	fun onBind(market: Market) {
		nameTxt.text = market.coin.title
		codeTxt.text = market.coin.code
		coinIcon.bind(market.coin.code)
		priceTxt.text = "$${market.rate.price.toFiatDisplayFormat()}"
	}
	
	interface Listener {
		fun onClick(position: Int)
	}
}