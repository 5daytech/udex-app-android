package com.blocksdecoded.dex.presentation.transactions.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.TransactionRecord
import com.blocksdecoded.dex.presentation.transactions.TransactionViewItem
import com.blocksdecoded.dex.utils.isValidIndex
import kotlin.collections.ArrayList

class TransactionsAdapter(
    private val listener: TransactionViewHolder.OnClickListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mTransactions = ArrayList<TransactionViewItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = TransactionViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false),
        listener
    )

    override fun getItemCount(): Int = mTransactions.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is TransactionViewHolder -> holder.onBind(mTransactions[position])
        }
    }

    fun setTransactions(transactions: List<TransactionViewItem>) {
        val diffUtil = DiffUtil.calculateDiff(TransactionsDiffCallback(this.mTransactions, transactions))

        mTransactions.clear()
        mTransactions.addAll(transactions)

        diffUtil.dispatchUpdatesTo(this)
    }

    fun addNextTransactions(transactions: List<TransactionRecord>) {

    }

    fun addBeforeTransactions(transactions: List<TransactionRecord>) {

    }

    fun syncTransaction(it: Int?) {
        if (it != null && mTransactions.isValidIndex(it)) {
            notifyItemChanged(it)
        }
    }
}