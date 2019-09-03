package com.blocksdecoded.dex.core.tradehistory

import com.blocksdecoded.dex.core.model.TransactionRecord

data class TradeRecord(
    val hash: String,
    val fromCoins: List<TradeRecordItem>,
    val toCoins: List<TradeRecordItem>
)

data class TradeRecordItem(
    val coinCode: String,
    val transactionRecord: TransactionRecord
)