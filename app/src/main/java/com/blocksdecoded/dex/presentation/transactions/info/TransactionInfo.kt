package com.blocksdecoded.dex.presentation.transactions.info

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.core.model.TransactionRecord
import java.math.BigDecimal
import java.util.*

data class TransactionViewData(
	val transactionHash: String,
	val coin: Coin,
	val coinValue: BigDecimal,
	val fiatValue: BigDecimal?,
	val from: String?,
	val to: String?,
	val incoming: Boolean,
	val date: Date?,
	val status: TransactionStatus,
	val rate: Rate?
)

sealed class TransactionStatus {
	object Pending : TransactionStatus()
	class Processing(val progress: Double) : TransactionStatus() //progress in 0..100%
	object Completed : TransactionStatus()
}

data class TransactionInfo(
	val coin: Coin,
	val transactionRecord: TransactionRecord
)