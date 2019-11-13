package com.fridaytech.dex.presentation.transactions.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fridaytech.dex.R
import com.fridaytech.dex.presentation.transactions.model.TransactionViewItem
import com.fridaytech.dex.utils.TimeUtils
import com.fridaytech.dex.utils.setTextColorRes
import com.fridaytech.dex.utils.ui.toDisplayFormat
import com.fridaytech.dex.utils.ui.toFiatDisplayFormat
import java.math.BigDecimal
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_transaction.*

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
