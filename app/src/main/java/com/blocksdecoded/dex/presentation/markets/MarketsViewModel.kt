package com.blocksdecoded.dex.presentation.markets

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.manager.rates.RatesSyncState.*
import com.blocksdecoded.dex.core.manager.rates.model.StatsData
import com.blocksdecoded.dex.core.model.ChartType
import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.utils.rx.uiSubscribe
import java.math.BigDecimal

class MarketsViewModel : CoreViewModel() {
    val markets = MutableLiveData<List<MarketViewItem>>()
    val loading = MutableLiveData<Boolean>()

    private val coinManager = App.coinManager
    private val ratesManager = App.ratesManager
    private val ratesStatsManager = App.ratesStatsManager

    private var mRates = listOf<Rate?>()
    private var mRateStats = arrayListOf<StatsData?>()
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

                mRates = coinManager.coins.mapIndexed { index, coin ->
                    val rate = ratesManager.getLatestRate(coin.code)
                    if (!mRateStats.isValidIndex(index)) {
                        mRateStats.add(null)
                    }
                    rate
                }

                coins.forEach {
                    ratesStatsManager.syncStats(it.code)
                }

                updateMarkets()
            }
            .let { disposables.add(it) }

        ratesStatsManager.statsFlowable
            .uiSubscribe(disposables, { rateStats ->
                when(rateStats) {
                    is StatsData -> {
                        val index = mRates.indexOfFirst {
                            it?.coinCode == rateStats.coinCode
                        }

                        if (index >= 0) {
                            mRateStats[index] = rateStats
                        }

                        updateMarkets()
                    }
                }
            }, { Logger.e(it) })
    }

    private fun updateMarkets() {
        markets.postValue(mRates.mapIndexed { index, rate ->
            val price = rate?.price ?: BigDecimal.ZERO
            val stats = if (mRateStats.isValidIndex(index)) {
                mRateStats[index]
            } else {
                ratesStatsManager.getStats(rate?.coinCode ?: "")
            }
            var change = BigDecimal.ZERO
            var marketCap = BigDecimal.ZERO

            if (stats is StatsData) {
                change = stats.diff[ChartType.DAILY.name] ?: BigDecimal.ZERO
                marketCap = stats.marketCap
            }

            MarketViewItem(
                coinManager.getCoin(rate?.coinCode ?: ""),
                price,
                change,
                marketCap
            )
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
