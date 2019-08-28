package com.blocksdecoded.dex.presentation.transactions.info

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.core.model.TransactionRecord

data class TransactionInfo(
	val coin: Coin,
	val transactionRecord: TransactionRecord
)