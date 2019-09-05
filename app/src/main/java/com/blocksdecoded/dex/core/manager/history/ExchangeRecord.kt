package com.blocksdecoded.dex.core.manager.history

import com.blocksdecoded.dex.core.model.TransactionRecord

data class ExchangeRecord(
    val hash: String,
    val timestamp: Long,
    val fromCoins: List<ExchangeRecordItem>,
    val toCoins: List<ExchangeRecordItem>
)

data class ExchangeRecordItem(
    val coinCode: String,
    val transactionRecord: TransactionRecord
)