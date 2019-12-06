package com.fridaytech.dex.presentation.orders.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fridaytech.dex.R
import com.fridaytech.dex.data.zrx.model.SimpleOrder
import com.fridaytech.dex.presentation.orders.model.EOrderSide
import com.fridaytech.dex.utils.ui.getAttr
import com.fridaytech.dex.utils.ui.toDisplayFormat
import com.fridaytech.dex.utils.ui.toFiatDisplayFormat
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_my_order.*

class OrderViewHolder(
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

        if (order.side == EOrderSide.BUY) {
            my_order_amount.text = order.remainingTakerAmount.toDisplayFormat()
            my_order_amount_coin.text = order.takerCoin.code
            my_order_amount_fiat.text = "~ $${order.takerFiatAmount.toFiatDisplayFormat()}"

            my_order_total.text = order.remainingMakerAmount.toDisplayFormat()
            my_order_total_coin.text = order.makerCoin.code
            my_order_total_fiat.text = "~ $${order.makerFiatAmount.toFiatDisplayFormat()}"
        } else {
            my_order_amount.text = order.remainingMakerAmount.toDisplayFormat()
            my_order_amount_coin.text = order.makerCoin.code
            my_order_amount_fiat.text = "~ $${order.makerFiatAmount.toFiatDisplayFormat()}"

            my_order_total.text = order.remainingTakerAmount.toDisplayFormat()
            my_order_total_coin.text = order.takerCoin.code
            my_order_total_fiat.text = "~ $${order.takerFiatAmount.toFiatDisplayFormat()}"
        }
    }

    interface Listener {
        fun onClick(position: Int)
    }
}
