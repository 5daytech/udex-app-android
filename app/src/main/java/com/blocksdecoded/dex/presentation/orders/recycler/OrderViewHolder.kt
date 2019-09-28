package com.blocksdecoded.dex.presentation.orders.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.utils.ui.getAttr
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat

class OrderViewHolder(
        view: View,
        private val listener: Listener
): RecyclerView.ViewHolder(view) {

    private val amountTxt: TextView = itemView.findViewById(R.id.order_amount)
    private val fiatAmountTxt: TextView = itemView.findViewById(R.id.order_amount_fiat)
    private val amountCoinTxt: TextView = itemView.findViewById(R.id.order_amount_coin)

    private val totalTxt: TextView = itemView.findViewById(R.id.order_total)
    private val fiatTotalTxt: TextView = itemView.findViewById(R.id.order_total_fiat)
    private val totalCoinTxt: TextView = itemView.findViewById(R.id.order_total_coin)

    init {
        itemView.setOnClickListener { listener.onClick(adapterPosition) }
    }

    fun onBind(order: UiOrder) {
        itemView.setBackgroundColor(
            getAttr(if (adapterPosition % 2 == 0) {
                    R.attr.OddOrderBackground
                } else {
                    R.attr.EvenOrderBackground
                })
        )

        if (order.side == EOrderSide.BUY) {
            amountTxt.text = order.takerAmount.toDisplayFormat()
            amountCoinTxt.text = order.takerCoin.code
            fiatAmountTxt.text = "~ $${order.takerFiatAmount.toFiatDisplayFormat()}"

            totalTxt.text = order.makerAmount.toDisplayFormat()
            totalCoinTxt.text = order.makerCoin.code
            fiatTotalTxt.text = "~ $${order.makerFiatAmount.toFiatDisplayFormat()}"
        } else {
            amountTxt.text = order.makerAmount.toDisplayFormat()
            amountCoinTxt.text = order.makerCoin.code
            fiatAmountTxt.text = "~ $${order.makerFiatAmount.toFiatDisplayFormat()}"

            totalTxt.text = order.takerAmount.toDisplayFormat()
            totalCoinTxt.text = order.takerCoin.code
            fiatTotalTxt.text = "~ $${order.takerFiatAmount.toFiatDisplayFormat()}"
        }
    }

    interface Listener {
        fun onClick(position: Int)
    }
}