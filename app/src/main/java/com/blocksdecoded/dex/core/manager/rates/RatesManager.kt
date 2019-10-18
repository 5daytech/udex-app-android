package com.blocksdecoded.dex.core.manager.rates

import android.annotation.SuppressLint
import com.blocksdecoded.dex.core.manager.ICoinManager
import com.blocksdecoded.dex.core.manager.rates.remote.IRatesApiClient
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.Market
import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.core.storage.IMarketsStorage
import com.blocksdecoded.dex.core.storage.IRatesStorage
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.ioSubscribe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal

class RatesManager(
    private val coinManager: ICoinManager,
    private val marketsStorage: IMarketsStorage,
    private val ratesStorage: IRatesStorage,
    private val rateClient: IRatesApiClient
): IRatesManager {
    private val disposables = CompositeDisposable()

    override val ratesUpdateSubject: BehaviorSubject<Unit> = BehaviorSubject.create()
    override val ratesStateSubject: BehaviorSubject<RatesSyncState> = BehaviorSubject.create()

    private var availableCoins = listOf<Coin>()
    private var cachedRates = listOf<Market>()
    private var latestRates = listOf<Rate>()

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
        marketsStorage.getAllMarkets().ioSubscribe(disposables, {
            cachedRates = it
            ratesUpdateSubject.onNext(Unit)
        })
    }

    private fun syncRates() {
        rateClient.getLatestRates().ioSubscribe(disposables, { ratesData ->
            val rates = ArrayList<Rate>()
            availableCoins.forEach {
                val cleanCoinCode = coinManager.cleanCoinCode(it.code)

                rates.add(Rate(
                    cleanCoinCode,
                    ratesData.timestamp / 1000,
                    ratesData.rates[cleanCoinCode]?.toBigDecimal() ?: BigDecimal.ZERO
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
                    val rate = Rate(cleanCoinCode, timeStamp, it)
                    ratesStorage.save(rate)
                    rate
                }
            )
    }

    override fun getLatestRateSingle(coinCode: String): Single<Rate> {
        val rate = getLatestRate(coinManager.cleanCoinCode(coinCode))
        return if (rate != null) {
            Single.just(rate)
        } else {
            Single.error(NullPointerException())
        }
    }

    override fun getLatestRate(coinCode: String): Rate? {
        return latestRates.firstOrNull { it.coinCode == coinManager.cleanCoinCode(coinCode) }
    }

    //endregion

    //region Markets

    override fun getMarkets(coinCodes: List<String>): List<Market> =
        cachedRates.filter {
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