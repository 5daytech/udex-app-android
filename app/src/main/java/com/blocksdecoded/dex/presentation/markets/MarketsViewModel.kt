package com.blocksdecoded.dex.presentation.markets

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.core.model.Market
import com.blocksdecoded.dex.core.rates.RatesState.*
import com.blocksdecoded.dex.core.ui.CoreViewModel
import io.reactivex.schedulers.Schedulers

class MarketsViewModel : CoreViewModel() {
    val markets = MutableLiveData<List<Market>>()
    val loading = MutableLiveData<Boolean>()
    
    private val ratesManager = App.ratesManager
    private val availableMarkets = CoinManager.coins

    init {
        ratesManager.ratesStateSubject
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe {
                loading.postValue(when(it) {
                    SYNCING -> true
                    SYNCED -> false
                    FAILED -> false
                })
            }
            .let { disposables.add(it) }

        ratesManager.ratesUpdateSubject
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe {
                val rates = ratesManager.getRates(availableMarkets.map { it.code })

                markets.postValue(availableMarkets.mapIndexed { index, marketCoin ->
                    Market(
                        marketCoin,
                        rates.firstOrNull { it.symbol == marketCoin.code || marketCoin.code.contains(it.symbol)}
                            ?: Rate(marketCoin.code))
                })
            }
            .let { disposables.add(it) }
    }

    fun refresh() {
        ratesManager.refresh()
    }
}
