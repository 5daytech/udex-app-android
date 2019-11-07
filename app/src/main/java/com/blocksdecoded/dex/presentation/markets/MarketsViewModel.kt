package com.blocksdecoded.dex.presentation.markets

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.utils.rx.ioObserve
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
        ratesManager.getMarketsObservable().ioObserve()
            .subscribe({
                Log.d("ololo", "Markets update ${it.keys}")
                loading.value = false
                updateMarkets(it)
            }, {

            }).let { disposables.add(it) }

        preload()
        refresh()
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
                marketInfo?.marketCap?.toBigDecimal() ?: BigDecimal.ZERO
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
                marketInfo?.marketCap?.toBigDecimal() ?: BigDecimal.ZERO
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
