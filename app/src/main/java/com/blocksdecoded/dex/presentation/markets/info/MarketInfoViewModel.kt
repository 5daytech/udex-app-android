package com.blocksdecoded.dex.presentation.markets.info

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.Market
import com.blocksdecoded.dex.core.ui.CoreViewModel

class MarketInfoViewModel : CoreViewModel() {
    private val coinManager = App.coinManager
    private val ratesManager = App.ratesManager

    val coin = MutableLiveData<Coin>()
    val market = MutableLiveData<Market>()

    fun init(coinCode: String) {
        market.value = ratesManager.getMarket(coinCode)
        coin.value = coinManager.getCoin(coinCode)
    }

}