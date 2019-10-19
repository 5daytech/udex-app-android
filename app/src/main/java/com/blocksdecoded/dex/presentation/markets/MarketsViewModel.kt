package com.blocksdecoded.dex.presentation.markets

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.manager.rates.RatesSyncState.*
import com.blocksdecoded.dex.core.manager.rates.model.StatsData
import com.blocksdecoded.dex.core.model.ChartType
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.utils.uiSubscribe
import java.math.BigDecimal

class MarketsViewModel : CoreViewModel() {
    val markets = MutableLiveData<List<MarketViewItem>>()
    val loading = MutableLiveData<Boolean>()

    private val coinManager = App.coinManager
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
                val coins = coinManager.coins

                coins.forEach {
                    ratesStatsManager.syncStats(coinManager.cleanCoinCode(it.code))
                }

                val rates = ratesManager.getMarkets(coins.map { it.code })

                markets.postValue(coins.mapIndexed { index, marketCoin ->
                    MarketViewItem(
                        marketCoin,
                        ratesManager.getLatestRate(marketCoin.code)?.price ?: BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO
                    )
                })
            }
            .let { disposables.add(it) }

        ratesStatsManager.statsFlowable
            .uiSubscribe(disposables, { rateStats ->
                when(rateStats) {
                    is StatsData -> {
                        val index = markets.value?.indexOfFirst {
                            rateStats.coinCode == coinManager.cleanCoinCode(it.coin.code)
                        }
                        if (index != null && index >= 0) {
                            markets.value?.get(index)?.let {
                                it.change = rateStats.diff[ChartType.DAILY.name] ?: BigDecimal.ZERO
                                it.marketCap = rateStats.marketCap
                                markets.value = markets.value
                            }
                        }
                    }
                }
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
