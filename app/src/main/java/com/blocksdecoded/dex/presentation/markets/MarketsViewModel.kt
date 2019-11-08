package com.blocksdecoded.dex.presentation.markets

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import io.horizontalsystems.xrateskit.entities.MarketInfo
import java.math.BigDecimal

class MarketsViewModel : CoreViewModel() {
    val markets = MutableLiveData<List<MarketViewItem>>()
    val loading = MutableLiveData<Boolean>()

    private val coinManager = App.coinManager
    private val ratesManager = App.ratesManager

    val openMarketInfoEvent = SingleLiveEvent<String>()

    init {
        loading.value = true
        refresh()

        ratesManager.getMarketsObservable()
            .subscribe {
                loading.postValue(false)
                updateMarkets(it)
            }.let { disposables.add(it) }

        preload()
    }

    override fun onNetworkConnectionAvailable() {
        super.onNetworkConnectionAvailable()
        refresh()
    }

    private fun preload() {
        loading.value = false
        markets.value = coinManager.coins.map {
            val marketInfo = ratesManager.getMarketInfo(it.code)

            MarketViewItem(
                it,
                marketInfo?.rate ?: BigDecimal.ZERO,
                marketInfo?.diff ?: BigDecimal.ZERO,
                marketInfo?.marketCap ?: 0.0
            )
        }
    }

    private fun updateMarkets(markets: Map<String, MarketInfo>) {
        this.markets.postValue(coinManager.coins.map {
            val marketInfo = markets[coinManager.cleanCoinCode(it.code)]

            MarketViewItem(
                it,
                marketInfo?.rate ?: BigDecimal.ZERO,
                marketInfo?.diff ?: BigDecimal.ZERO,
                marketInfo?.marketCap ?: 0.0
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
