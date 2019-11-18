package com.fridaytech.dex.presentation.deprecated.markets

import androidx.lifecycle.MutableLiveData
import com.fridaytech.dex.App
import com.fridaytech.dex.core.ui.CoreViewModel
import com.fridaytech.dex.core.ui.SingleLiveEvent
import io.horizontalsystems.xrateskit.entities.MarketInfo
import io.reactivex.disposables.Disposable
import java.math.BigDecimal

class MarketsViewModel : CoreViewModel() {
    val markets = MutableLiveData<List<MarketViewItem>>()
    val loading = MutableLiveData<Boolean>()

    private var marketsDisposable: Disposable? = null

    private val coinManager = App.coinManager
    private val ratesManager = App.ratesManager

    val openMarketInfoEvent = SingleLiveEvent<String>()

    init {
        loading.value = true
        refresh()

        observeMarketsChange()

        preload()

        coinManager.coinsUpdatedSubject
            .subscribe { observeMarketsChange() }
            .let { disposables.add(it) }
    }

    override fun onNetworkConnectionAvailable() {
        super.onNetworkConnectionAvailable()
        refresh()
    }

    private fun observeMarketsChange() {
        marketsDisposable?.dispose()
        ratesManager.getMarketsObservable()
            .subscribe {
                loading.postValue(false)
                updateMarkets(it)
            }.let { marketsDisposable = it }
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
        loading.postValue(true)
        ratesManager.refresh()
    }

    fun onMarketClick(position: Int) {
        markets.value?.get(position)?.let {
            openMarketInfoEvent.value = it.coin.code
        }
    }

    override fun onCleared() {
        super.onCleared()
        marketsDisposable?.dispose()
    }
}
