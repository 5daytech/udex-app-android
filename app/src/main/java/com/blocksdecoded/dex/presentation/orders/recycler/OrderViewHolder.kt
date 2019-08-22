package com.blocksdecoded.dex.presentation.orders.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import com.blocksdecoded.dex.utils.ui.toLongDisplayFormat

class OrderViewHolder(
        view: View,
        private val listener: Listener
): RecyclerView.ViewHolder(view) {
    
    private val priceTxt: TextView = itemView.findViewById(R.id.order_price)
    private val amountTxt: TextView = itemView.findViewById(R.id.order_amount)
    private val totalTxt: TextView = itemView.findViewById(R.id.order_total)

    init {
        itemView.setOnClickListener { listener.onClick(adapterPosition) }
    }

    fun onBind(order: UiOrder) {
        itemView.setBackgroundResource(
            if (adapterPosition % 2 == 0) {
                R.color.main_dark
            } else {
                R.color.action_button_dark
            }
        )
        
        priceTxt.text = order.price.toLongDisplayFormat()
        amountTxt.text = if (order.side == EOrderSide.BUY) {
            order.takerAmount.toDisplayFormat()
        } else {
            order.makerAmount.toDisplayFormat()
        }
        totalTxt.text = if (order.side == EOrderSide.BUY) {
            order.makerAmount.toDisplayFormat()
        } else {
            order.takerAmount.toDisplayFormat()
        }
    }

    interface Listener {
        fun onClick(position: Int)
    }
}