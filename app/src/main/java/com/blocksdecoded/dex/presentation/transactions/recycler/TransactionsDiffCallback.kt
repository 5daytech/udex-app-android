package com.blocksdecoded.dex.presentation.transactions.recycler

import androidx.recyclerview.widget.DiffUtil
import com.blocksdecoded.dex.presentation.transactions.model.TransactionViewItem

class TransactionsDiffCallback(
    private val oldTransactions: List<TransactionViewItem>,
    private val newTransactions: List<TransactionViewItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldTransactions.size

    override fun getNewListSize(): Int = newTransactions.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldTransactions[oldItemPosition].transactionHash == newTransactions[newItemPosition].transactionHash &&
                oldTransactions[oldItemPosition].innerIndex == newTransactions[newItemPosition].innerIndex

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldTransactions[oldItemPosition]
        val new = newTransactions[newItemPosition]

        return old.coinValue == new.coinValue &&
                old.fiatValue == new.fiatValue &&
                old.date == new.date &&
                old.status == new.status
    }
}
