package com.blocksdecoded.dex.presentation.transactions.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.transactions.model.TransactionViewItem
import com.blocksdecoded.dex.utils.setTextColorRes
import com.blocksdecoded.dex.utils.TimeUtils
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_transaction.*
import java.math.BigDecimal

class TransactionViewHolder(
    override val containerView: View,
    private val listener: OnClickListener
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    init {
        itemView.setOnClickListener { listener.onClick(adapterPosition) }
    }

    fun onBind(transaction: TransactionViewItem) {
        val isPositive = transaction.coinValue > BigDecimal.ZERO

        transaction_amount?.text = "${if (isPositive) "+" else "-"}${transaction.coinValue.abs().toDisplayFormat()} ${transaction.coin.code}"
        transaction_amount?.setTextColorRes(if (isPositive) R.color.green else R.color.red)

        transaction_fiat_amount?.text = "$${transaction.fiatValue?.abs()?.toFiatDisplayFormat()}"
        transaction_type_icon?.setImageResource(if (isPositive) R.drawable.ic_received else R.drawable.ic_sent)

        transaction.date?.let {
            transaction_date?.text = TimeUtils.dateToShort(it)
            transaction_time?.text = TimeUtils.dateToHour(it)
        }
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }
}