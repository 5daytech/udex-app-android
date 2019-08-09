package com.blocksdecoded.dex.presentation.exchange.view.market

import com.blocksdecoded.dex.presentation.exchange.view.ExchangePairItem
import java.math.BigDecimal

data class MarketOrderViewState(
	var sendAmount: BigDecimal,
	var receiveAmount: BigDecimal,
	var sendPair: ExchangePairItem?,
	var receivePair: ExchangePairItem?,
	var sendError: Int = 0,
	var receiveError: Int = 0
)