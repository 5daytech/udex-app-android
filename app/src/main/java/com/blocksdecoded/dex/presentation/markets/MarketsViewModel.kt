package com.blocksdecoded.dex.presentation.markets

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.manager.rates.RatesSyncState.*
import com.blocksdecoded.dex.core.model.Market
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.utils.uiSubscribe

class MarketsViewModel : CoreViewModel() {
    val markets = MutableLiveData<List<MarketViewItem>>()
    val loading = MutableLiveData<Boolean>()
    
    private val ratesManager = App.ratesManager
    private val ratesStatsManager = App.ratesStatsManager

    val openMarketInfoEvent = SingleLiveEvent<String>()

    init {
        ratesManager.ratesStateSubject
            .subscribe {
                loading.postValue(when(it) {
                    SYNCING -> true
                    SYNCED -> false
                    FAILED -> false
                })
            }
            .let { disposables.add(it) }

        ratesManager.ratesUpdateSubject
            .subscribe {
                val coins = App.coinManager.coins

                coins.forEach {
                    ratesStatsManager.syncStats(App.coinManager.cleanCoinCode(it.code))
                }

                val rates = ratesManager.getMarkets(coins.map { it.code })

                markets.postValue(coins.mapIndexed { index, marketCoin ->
                    MarketViewItem(
                        marketCoin,
                        rates.firstOrNull { it.coinCode == marketCoin.code || marketCoin.code.contains(it.coinCode)}
                            ?: Market(marketCoin.code))
                })
            }
            .let { disposables.add(it) }

        ratesStatsManager.statsFlowable
            .uiSubscribe(disposables, {

            })
    }

    fun refresh() {
        ratesManager.refresh()
    }

    fun onMarketClick(position: Int) {
        markets.value?.get(position)?.let {
            openMarketInfoEvent.value = it.coin.code
        }
    }
}
