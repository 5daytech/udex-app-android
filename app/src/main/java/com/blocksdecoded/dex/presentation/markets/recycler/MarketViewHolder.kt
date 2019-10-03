package com.blocksdecoded.dex.presentation.markets.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.markets.MarketViewItem
import com.blocksdecoded.dex.utils.setTextColorRes
import com.blocksdecoded.dex.utils.ui.CurrencyUtils
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_market.*
import kotlin.math.absoluteValue

class MarketViewHolder(
	override val containerView: View,
	private val listener: Listener
): RecyclerView.ViewHolder(containerView), LayoutContainer {
	init {
//		itemView.setOnClickListener { listener.onClick(adapterPosition) }
	}
	
	fun onBind(market: MarketViewItem) {
		val color = if (market.market.priceChange >= 0) {
			market_change_img.setImageResource(R.drawable.ic_carret_up_green)
			R.color.green
		} else {
			market_change_img.setImageResource(R.drawable.ic_carret_down_red)
			R.color.red
		}
		
		market_name.text = market.coin.title
		market_code.text = market.coin.code
		market_coin_icon.bind(market.coin.code)
		market_price.text = "$${market.market.price.toFiatDisplayFormat()}"

		market_change_percent.text = "${market.market.priceChange.absoluteValue}%"
		market_volume.text = "Volume ${CurrencyUtils.withSuffix(market.market.volume)}"

		market_change_percent.setTextColorRes(color)
	}
	
	interface Listener {
		fun onClick(position: Int)
	}
}