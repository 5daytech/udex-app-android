package com.fridaytech.dex.presentation.orders.orderbook

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fridaytech.dex.R
import com.fridaytech.dex.data.zrx.model.SimpleOrder
import com.fridaytech.dex.presentation.orders.model.EOrderSide
import com.fridaytech.dex.utils.setTextColorRes
import com.fridaytech.dex.utils.ui.getAttr
import com.fridaytech.dex.utils.ui.toDisplayFormat
import com.fridaytech.dex.utils.ui.toFiatDisplayFormat
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_order_book.*

class OrderBookViewHolder(
    override val containerView: View,
    private val listener: Listener
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    init {
        itemView.setOnClickListener { listener.onClick(adapterPosition) }
    }

    fun onBind(order: SimpleOrder) {
        itemView.setBackgroundColor(
            getAttr(if (adapterPosition % 2 == 0) {
                    R.attr.OddOrderBackground
                } else {
                    R.attr.EvenOrderBackground
                })
        )

        market_order_amount.setTextColorRes(if (order.side == EOrderSide.SELL) R.color.red else R.color.green)

        if (order.side == EOrderSide.BUY) {
            market_order_amount.text = order.takerAmount.toDisplayFormat()
            market_order_amount_fiat.text = "$${order.takerFiatAmount.toFiatDisplayFormat()}"

            market_order_total.text = order.makerAmount.toDisplayFormat()
            market_order_total_fiat.text = "$${order.makerFiatAmount.toFiatDisplayFormat()}"
        } else {
            market_order_amount.text = order.makerAmount.toDisplayFormat()
            market_order_amount_fiat.text = "$${order.makerFiatAmount.toFiatDisplayFormat()}"

            market_order_total.text = order.takerAmount.toDisplayFormat()
            market_order_total_fiat.text = "$${order.takerFiatAmount.toFiatDisplayFormat()}"
        }
    }

    interface Listener {
        fun onClick(position: Int)
    }
}
