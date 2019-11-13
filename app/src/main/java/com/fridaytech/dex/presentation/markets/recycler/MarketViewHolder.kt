package com.fridaytech.dex.presentation.markets.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fridaytech.dex.R
import com.fridaytech.dex.presentation.markets.MarketViewItem
import com.fridaytech.dex.utils.bindChangePercent
import com.fridaytech.dex.utils.ui.NumberUtils
import com.fridaytech.dex.utils.ui.toFiatDisplayFormat
import java.math.BigDecimal
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_market.*

class MarketViewHolder(
    override val containerView: View,
    private val listener: Listener
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    init {
        itemView.setOnClickListener { listener.onClick(adapterPosition) }
    }

    fun onBind(market: MarketViewItem) {
        market_name.text = market.coin.title
        market_code.text = market.coin.code
        market_coin_icon.bind(market.coin.code)
        market_price.text = "$${market.price.toFiatDisplayFormat()}"

        market_change_img.setImageResource(
            if (market.change >= BigDecimal.ZERO)
                R.drawable.ic_carret_up_green
            else
                R.drawable.ic_carret_down_red
        )
        market_change_percent.bindChangePercent(market.change, withSign = false)
        market_volume.text = "Mrk. Cap ${NumberUtils.withSuffix(market.marketCap)}"
    }

    interface Listener {
        fun onClick(position: Int)
    }
}
