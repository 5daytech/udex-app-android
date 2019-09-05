package com.blocksdecoded.dex.presentation.markets

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.model.Market
import com.blocksdecoded.dex.core.manager.rates.MarketState.*
import com.blocksdecoded.dex.core.ui.CoreViewModel

class MarketsViewModel : CoreViewModel() {
    val markets = MutableLiveData<List<MarketViewItem>>()
    val loading = MutableLiveData<Boolean>()
    
    private val ratesManager = App.ratesManager
    private val availableMarkets = App.coinManager.coins

    init {
        ratesManager.marketsStateSubject
            .subscribe {
                loading.postValue(when(it) {
                    SYNCING -> true
                    SYNCED -> false
                    FAILED -> false
                })
            }
            .let { disposables.add(it) }

        ratesManager.marketsUpdateSubject
            .subscribe {
                val rates = ratesManager.getMarkets(availableMarkets.map { it.code })

                markets.postValue(availableMarkets.mapIndexed { index, marketCoin ->
                    MarketViewItem(
                        marketCoin,
                        rates.firstOrNull { it.coinCode == marketCoin.code || marketCoin.code.contains(it.coinCode)}
                            ?: Market(marketCoin.code))
                })
            }
            .let { disposables.add(it) }
    }

    fun refresh() {
        ratesManager.refresh()
    }
}
