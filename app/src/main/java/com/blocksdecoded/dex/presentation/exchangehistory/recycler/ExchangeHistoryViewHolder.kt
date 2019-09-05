package com.blocksdecoded.dex.presentation.exchangehistory.recycler

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.history.ExchangeRecord
import com.blocksdecoded.dex.core.history.ExchangeRecordItem
import com.blocksdecoded.dex.presentation.widgets.CoinIconImage
import com.blocksdecoded.dex.presentation.widgets.HashView
import com.blocksdecoded.dex.utils.TimeUtils
import com.blocksdecoded.dex.utils.inflate
import com.blocksdecoded.dex.utils.setTextColorRes
import java.math.BigDecimal

class ExchangeHistoryViewHolder(
    view: View,
    val onClick: (Int) -> Unit
): RecyclerView.ViewHolder(view) {

    private val hashView: HashView = itemView.findViewById(R.id.trade_record_hash)
    private val dateTxt: TextView = itemView.findViewById(R.id.trade_record_date)
    private val txRecycler: RecyclerView = itemView.findViewById(R.id.trade_record_tx_recycler)
    private val adapter = ExchangeRecordsAdapter()

    init {
        txRecycler.layoutManager = LinearLayoutManager(itemView.context)
        txRecycler.adapter = adapter
        hashView.bind { onClick.invoke(adapterPosition) }
    }

    fun onBind(exchangeRecord: ExchangeRecord) {
        hashView.update(exchangeRecord.hash)
        adapter.setRecords(exchangeRecord.fromCoins)
        dateTxt.text = TimeUtils.timestampToDisplayFormat(exchangeRecord.timestamp)
    }

    private class ExchangeRecordsAdapter: RecyclerView.Adapter<ExchangeRecordViewHolder>() {
        private var records = listOf<ExchangeRecordItem>()

        fun setRecords(records: List<ExchangeRecordItem>) {
            this.records = records
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRecordViewHolder =
            ExchangeRecordViewHolder(parent.inflate(R.layout.item_trade_tx_record))

        override fun onBindViewHolder(holder: ExchangeRecordViewHolder, position: Int) =
            holder.onBind(records[position])

        override fun getItemCount(): Int = records.size
    }

    private class ExchangeRecordViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val coinIcon: CoinIconImage = itemView.findViewById(R.id.item_trade_tx_coin_icon)
        private val amount: TextView = itemView.findViewById(R.id.item_trade_tx_amount)

        fun onBind(exchangeRecordItem: ExchangeRecordItem) {
            val isPositive = exchangeRecordItem.transactionRecord.amount >= BigDecimal.ZERO
            coinIcon.bind(exchangeRecordItem.coinCode)
            amount.setTextColorRes(if (isPositive) R.color.green else R.color.red)
            amount.text = "${if (isPositive) "+" else "-"} ${exchangeRecordItem.transactionRecord.amount.abs().stripTrailingZeros().toPlainString()} ${exchangeRecordItem.coinCode}"
        }
    }
}