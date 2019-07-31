package com.blocksdecoded.dex.presentation.transactions.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.TransactionRecord

class TransactionsAdapter(
    private val listener: TransactionViewHolder.OnClickListener,
    private val coinCode: String
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mTransactions = ArrayList<TransactionRecord>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = TransactionViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false),
        listener
    )

    override fun getItemCount(): Int = mTransactions.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is TransactionViewHolder -> holder.onBind(mTransactions[position], coinCode)
        }
    }

    fun setTransactions(transactions: List<TransactionRecord>) {
        mTransactions.clear()
        mTransactions.addAll(transactions)
        notifyDataSetChanged()
    }

    fun addNextTransactions(transactions: List<TransactionRecord>) {

    }

    fun addBeforeTransactions(transactions: List<TransactionRecord>) {

    }
}