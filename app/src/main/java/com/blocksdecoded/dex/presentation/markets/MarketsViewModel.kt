package com.blocksdecoded.dex.presentation.markets

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.core.model.CoinRate
import com.blocksdecoded.dex.core.model.Market
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.utils.Logger
import io.reactivex.android.schedulers.AndroidSchedulers

class MarketsViewModel : CoreViewModel() {
    val markets = MutableLiveData<List<Market>>()

    private val ratesManager = App.ratesManager
    private val availableMarkets = CoinManager.coins

    init {
        ratesManager.ratesStateSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { Logger.d("Rates viewState refresh $it") }
            .let { disposables.add(it) }

        ratesManager.ratesUpdateSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val rates = ratesManager.getRates(availableMarkets.map { it.code })

                markets.value = availableMarkets.mapIndexed { index, marketCoin ->
                    Market(
                        marketCoin,
                        rates.firstOrNull {
                            it.symbol == marketCoin.code
                        } ?: CoinRate(marketCoin.code))
                }
            }
            .let { disposables.add(it) }
    }

    fun refresh() {
        ratesManager.refresh()
    }
}
