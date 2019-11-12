package com.blocksdecoded.dex.presentation.transactions.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.transactions.model.TransactionViewItem
import com.blocksdecoded.dex.utils.inflate
import com.blocksdecoded.dex.utils.isValidIndex

class TransactionsAdapter(
    private val listener: TransactionViewHolder.OnClickListener,
    private val loadNextListener: ILoadNextListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mTransactions = ArrayList<TransactionViewItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        TransactionViewHolder(parent.inflate(R.layout.item_transaction), listener)

    override fun getItemCount(): Int = mTransactions.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TransactionViewHolder -> holder.onBind(mTransactions[position])
        }

        if (position > itemCount - 3) {
            loadNextListener.loadNext()
        }
    }

    fun setTransactions(transactions: List<TransactionViewItem>) {
        val diffUtil = DiffUtil.calculateDiff(TransactionsDiffCallback(this.mTransactions, transactions))

        mTransactions.clear()
        mTransactions.addAll(transactions)

        diffUtil.dispatchUpdatesTo(this)
    }

    fun syncTransactions(indexes: List<Int>?) {
        indexes?.forEach {
            if (mTransactions.isValidIndex(it)) {
                notifyItemChanged(it)
            }
        }
    }

    interface ILoadNextListener {
        fun loadNext()
    }
}
