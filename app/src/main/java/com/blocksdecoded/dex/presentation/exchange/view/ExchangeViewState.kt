package com.blocksdecoded.dex.presentation.exchange.view

import java.math.BigDecimal

data class ExchangeViewState(
	var sendAmount: BigDecimal,
	var receiveAmount: BigDecimal,
	var sendPair: ExchangePairItem,
	var receivePair: ExchangePairItem,
	var sendError: Int = 0,
	var receiveError: Int = 0
)