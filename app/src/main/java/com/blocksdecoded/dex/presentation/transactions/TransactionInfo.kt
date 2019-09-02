package com.blocksdecoded.dex.presentation.transactions

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.Rate
import java.math.BigDecimal
import java.util.*

data class TransactionViewItem(
	val coin: Coin,
	val transactionHash: String,
	val coinValue: BigDecimal,
	var fiatValue: BigDecimal?,
	var historicalRate: BigDecimal?,
	val from: String?,
	val to: String?,
	val incoming: Boolean,
	val date: Date?,
	val status: TransactionStatus
)

sealed class TransactionStatus {
	object Pending : TransactionStatus()
	class Processing(val progress: Double) : TransactionStatus() //progress in 0..100%
	object Completed : TransactionStatus()
}