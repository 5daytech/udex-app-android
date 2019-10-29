package com.blocksdecoded.dex.presentation.exchangehistory.recycler

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.data.manager.history.ExchangeRecord
import com.blocksdecoded.dex.data.manager.history.ExchangeRecordItem
import com.blocksdecoded.dex.utils.TimeUtils
import com.blocksdecoded.dex.utils.inflate
import com.blocksdecoded.dex.utils.setTextColorRes
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_trade_record.*
import kotlinx.android.synthetic.main.item_trade_tx_record.*
import java.math.BigDecimal

class ExchangeHistoryViewHolder(
    override val containerView: View,
    val onClick: (Int) -> Unit
): RecyclerView.ViewHolder(containerView), LayoutContainer {
    private val adapter = ExchangeRecordsAdapter()

    init {
        trade_record_tx_recycler.layoutManager = LinearLayoutManager(itemView.context)
        trade_record_tx_recycler.adapter = adapter
        trade_record_hash.bind { onClick.invoke(adapterPosition) }
    }

    fun onBind(exchangeRecord: ExchangeRecord) {
        trade_record_hash.update(exchangeRecord.hash)
        trade_record_date.text = TimeUtils.timestampToDisplayFormat(exchangeRecord.timestamp)
        adapter.setRecords(exchangeRecord.fromCoins)
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

    private class ExchangeRecordViewHolder(
        override val containerView: View
    ): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun onBind(exchangeRecordItem: ExchangeRecordItem) {
            val isPositive = exchangeRecordItem.transactionRecord.amount >= BigDecimal.ZERO
            item_trade_tx_coin_icon.bind(exchangeRecordItem.coinCode)
            item_trade_tx_amount.setTextColorRes(if (isPositive) R.color.green else R.color.red)
            item_trade_tx_amount.text = "${if (isPositive) "+" else "-"} ${exchangeRecordItem.transactionRecord.amount.abs().stripTrailingZeros().toPlainString()} ${exchangeRecordItem.coinCode}"
        }
    }
}