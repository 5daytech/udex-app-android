package com.fridaytech.dex.data.manager.history

import com.fridaytech.dex.core.model.TransactionRecord

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
