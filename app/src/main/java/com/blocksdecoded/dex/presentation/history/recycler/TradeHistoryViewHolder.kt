package com.blocksdecoded.dex.presentation.history.recycler

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.tradehistory.TradeRecord
import com.blocksdecoded.dex.core.tradehistory.TradeRecordItem
import com.blocksdecoded.dex.presentation.widgets.CoinIconImage
import com.blocksdecoded.dex.presentation.widgets.HashView
import com.blocksdecoded.dex.utils.inflate

class TradeHistoryViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val hashView: HashView = itemView.findViewById(R.id.trade_record_hash)
    private val txRecycler: RecyclerView = itemView.findViewById(R.id.trade_record_tx_recycler)
    private val adapter = TradeRecordItemsAdapter()

    init {
        txRecycler.layoutManager = LinearLayoutManager(itemView.context)
        txRecycler.adapter = adapter
    }

    fun onBind(tradeRecord: TradeRecord) {
        hashView.update(tradeRecord.hash)
        adapter.setRecords(tradeRecord.fromCoins)
    }

    private class TradeRecordItemsAdapter: RecyclerView.Adapter<TradeRecordViewHolder>() {
        private var records = listOf<TradeRecordItem>()

        fun setRecords(records: List<TradeRecordItem>) {
            this.records = records
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeRecordViewHolder =
            TradeRecordViewHolder(parent.inflate(R.layout.item_trade_tx_record))

        override fun onBindViewHolder(holder: TradeRecordViewHolder, position: Int) =
            holder.onBind(records[position])

        override fun getItemCount(): Int = records.size
    }

    private class TradeRecordViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val coinIcon: CoinIconImage = itemView.findViewById(R.id.item_trade_tx_coin_icon)
        private val amount: TextView = itemView.findViewById(R.id.item_trade_tx_amount)

        fun onBind(tradeRecord: TradeRecordItem) {
            coinIcon.bind(tradeRecord.coinCode)
            amount.text = tradeRecord.transactionRecord.amount.stripTrailingZeros().toPlainString()
        }
    }
}