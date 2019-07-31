package com.blocksdecoded.dex.presentation.transactions.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.TransactionRecord
import com.blocksdecoded.dex.presentation.widgets.CoinIconImage
import com.blocksdecoded.dex.utils.setTextColorRes
import com.blocksdecoded.dex.utils.ui.TimeUtils
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import java.math.BigDecimal

class TransactionViewHolder(
    view: View,
    private val listener: OnClickListener
) : RecyclerView.ViewHolder(view) {

    private val iconImage: CoinIconImage = itemView.findViewById(R.id.transaction_coin_icon)
    private val dateTxt: TextView = itemView.findViewById(R.id.transaction_date)
    private val amountTxt: TextView = itemView.findViewById(R.id.transaction_amount)
    private val actionTxt: TextView = itemView.findViewById(R.id.transaction_action)

    init {
        itemView.setOnClickListener { listener.onClick(adapterPosition) }
    }

    fun onBind(transaction: TransactionRecord, coinCode: String) {
        iconImage.bind(coinCode)
        val amountColor: Int

        actionTxt.text = if (transaction.amount > BigDecimal.ZERO) {
            amountColor = R.color.green
            "Receive"
        } else {
            amountColor = R.color.red
            "Sent"
        }

        amountTxt.text = "${transaction.amount.toDisplayFormat()} $coinCode"
        amountTxt.setTextColorRes(amountColor)

        dateTxt.text = TimeUtils.timestampToShort(transaction.timestamp)
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }
}