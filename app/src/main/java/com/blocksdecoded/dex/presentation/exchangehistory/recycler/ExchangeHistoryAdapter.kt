package com.blocksdecoded.dex.presentation.exchangehistory.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.data.manager.history.ExchangeRecord
import com.blocksdecoded.dex.utils.inflate

class ExchangeHistoryAdapter(
    private var exchangeHistory: List<ExchangeRecord>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ExchangeHistoryViewHolder(parent.inflate(R.layout.item_trade_record), onClick)

    override fun getItemCount(): Int = exchangeHistory.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExchangeHistoryViewHolder -> holder.onBind(exchangeHistory[position])
        }
    }

    fun setTrades(exchangeHistory: List<ExchangeRecord>) {
        this.exchangeHistory = exchangeHistory
        notifyDataSetChanged()
    }
}
