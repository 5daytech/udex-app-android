package com.blocksdecoded.dex.data.manager.rates

import android.annotation.SuppressLint
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.Market
import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.data.manager.ICoinManager
import com.blocksdecoded.dex.data.manager.rates.remote.IRatesApiClient
import com.blocksdecoded.dex.data.storage.IMarketsStorage
import com.blocksdecoded.dex.data.storage.IRatesStorage
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.rx.ioSubscribe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal

class RatesManager(
    private val coinManager: ICoinManager,
    private val marketsStorage: IMarketsStorage,
    private val ratesStorage: IRatesStorage,
    private val rateClient: IRatesApiClient
) : IRatesManager {
    private val disposables = CompositeDisposable()

    override val ratesUpdateSubject: BehaviorSubject<Unit> = BehaviorSubject.create()
    override val ratesStateSubject: BehaviorSubject<RatesSyncState> = BehaviorSubject.create()

    private var availableCoins = listOf<Coin>()
    private var cachedMarkets = listOf<Market>()
    private var latestRates = listOf<Rate>()
        set(value) {
            field = value
            ratesStorage.saveLatest(value)
        }

    init {
        ratesStateSubject.onNext(RatesSyncState.SYNCING)

        coinManager.coinsUpdatedSubject
            .subscribe {
                availableCoins = coinManager.coins
                syncRates()
            }.let { disposables.add(it) }

        initialStorageFetch()
    }

    //region Private

    @SuppressLint("CheckResult")
    private fun initialStorageFetch() {
        ratesStorage.getLatestRates().ioSubscribe(disposables, {
            latestRates = it
            ratesUpdateSubject.onNext(Unit)
        })
    }

    private fun syncRates() {
        rateClient.getLatestRates().ioSubscribe(disposables, { ratesData ->
            val rates = ArrayList<Rate>()
            availableCoins.forEach {
                val cleanCoinCode = coinManager.cleanCoinCode(it.code)

                rates.add(Rate(
                    it.code,
                    ratesData.timestamp / 1000,
                    ratesData.rates[cleanCoinCode]?.toBigDecimal() ?: BigDecimal.ZERO,
                    isLatest = true
                ))
            }
            latestRates = rates
            ratesStateSubject.onNext(RatesSyncState.SYNCED)
            ratesUpdateSubject.onNext(Unit)
        }, {
            ratesStateSubject.onNext(RatesSyncState.FAILED)
            Logger.e(it)
        })
    }

    //endregion

    //region Public

    //region Rates

    override fun getRate(coinCode: String, timeStamp: Long): Rate? =
        ratesStorage.getRate(coinManager.cleanCoinCode(coinCode), timeStamp)

    override fun getRateSingle(coinCode: String, timeStamp: Long): Single<Rate> {
        val cleanCoinCode = coinManager.cleanCoinCode(coinCode)

        return ratesStorage.getRateSingle(cleanCoinCode, timeStamp)
            .onErrorResumeNext(
                rateClient.getHistoricalRate(cleanCoinCode, timeStamp).map {
                    val rate = Rate(cleanCoinCode, timeStamp, it, isLatest = false)
                    ratesStorage.save(rate)
                    rate
                }
            )
    }

    override fun getLatestRateSingle(coinCode: String): Single<Rate> {
        val rate = getLatestRate(coinCode)
        return if (rate != null) {
            Single.just(rate)
        } else {
            Single.error(NullPointerException())
        }
    }

    override fun getLatestRate(coinCode: String): Rate? {
        return latestRates.firstOrNull { it.coinCode == coinCode }
    }

    //endregion

    //region Markets

    override fun getMarkets(coinCodes: List<String>): List<Market> =
        cachedMarkets.filter {
            coinCodes.contains(it.coinCode) ||
                 coinCodes.indexOfFirst { symbol -> symbol.contains(it.coinCode) } >= 0
        }

    override fun refresh() {
        ratesStateSubject.value?.let {
            if (it != RatesSyncState.SYNCING) {
                ratesStateSubject.onNext(RatesSyncState.SYNCING)

                syncRates()
            }
        }
    }

    //endregion

    override fun stop() {
        disposables.dispose()
    }

    override fun clear() {
        marketsStorage.deleteAll()
        stop()
    }

    //endregion
}
