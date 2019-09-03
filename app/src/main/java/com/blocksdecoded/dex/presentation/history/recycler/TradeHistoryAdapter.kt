package com.blocksdecoded.dex.presentation.history.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.tradehistory.TradeRecord
import com.blocksdecoded.dex.utils.inflate

class TradeHistoryAdapter(
    private var tradesHistory: List<TradeRecord>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        TradeHistoryViewHolder(parent.inflate(R.layout.item_trade_record))

    override fun getItemCount(): Int = tradesHistory.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is TradeHistoryViewHolder -> holder.onBind(tradesHistory[position])
        }
    }

    fun setTrades(tradesHistory: List<TradeRecord>) {
        this.tradesHistory = tradesHistory
        notifyDataSetChanged()
    }
}