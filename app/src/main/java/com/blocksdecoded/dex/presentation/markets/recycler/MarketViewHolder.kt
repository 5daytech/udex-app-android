package com.blocksdecoded.dex.presentation.markets.recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.markets.MarketViewItem
import com.blocksdecoded.dex.presentation.widgets.CoinIconView
import com.blocksdecoded.dex.presentation.widgets.MarketChart
import com.blocksdecoded.dex.utils.setTextColorRes
import com.blocksdecoded.dex.utils.ui.CurrencyUtils
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import kotlin.math.absoluteValue

class MarketViewHolder(
	view: View,
	private val listener: Listener
): RecyclerView.ViewHolder(view) {

	private val coinIcon: CoinIconView = itemView.findViewById(R.id.market_coin_icon)
	private val nameTxt: TextView = itemView.findViewById(R.id.market_name)
	private val priceTxt: TextView = itemView.findViewById(R.id.market_price)
	private val codeTxt: TextView = itemView.findViewById(R.id.market_code)
	private val changePercentTxt: TextView = itemView.findViewById(R.id.market_change_percent)
	private val coinVolume: TextView = itemView.findViewById(R.id.market_volume)
	private val changeImg: ImageView = itemView.findViewById(R.id.market_change_img)
	private val chart: MarketChart = itemView.findViewById(R.id.market_chart)
	
	init {
//		itemView.setOnClickListener { listener.onClick(adapterPosition) }
	}
	
	fun onBind(market: MarketViewItem) {
		val color = if (market.market.priceChange >= 0) {
			changeImg.setImageResource(R.drawable.ic_carret_up_green)
			R.color.green
		} else {
			changeImg.setImageResource(R.drawable.ic_carret_down_red)
			R.color.red
		}
		
		nameTxt.text = market.coin.title
		codeTxt.text = market.coin.code
		coinIcon.bind(market.coin.code)
		priceTxt.text = "$${market.market.price.toFiatDisplayFormat()}"

		changePercentTxt.text = "${market.market.priceChange.absoluteValue}%"
		coinVolume.text = "Volume ${CurrencyUtils.withSuffix(market.market.volume)}"

		changePercentTxt.setTextColorRes(color)
//		chart.displayData(market.market.history, color, R.color.transparent)
		chart.displayData(listOf(), color, R.color.transparent)
	}
	
	interface Listener {
		fun onClick(position: Int)
	}
}