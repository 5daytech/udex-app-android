package com.blocksdecoded.dex.presentation.dialogs.transaction

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.Rate

data class TransactionInfo(
	val coin: Coin,
	val hash: String,
	val rate: Rate
) {
}