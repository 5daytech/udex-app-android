package com.blocksdecoded.dex.core.rates

import android.annotation.SuppressLint
import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.core.rates.bootstrap.IBootstrapClient
import com.blocksdecoded.dex.core.model.Market
import com.blocksdecoded.dex.core.rates.remote.IRatesApiClient
import com.blocksdecoded.dex.core.rates.remote.IRatesClientConfig
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.ioSubscribe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class RatesManager(
    private val marketsStorage: IMarketsStorage,
    private val ratesStorage: IRatesStorage,
    private val bootstrapClient: IBootstrapClient,
    private val rateClient: IRatesApiClient,
    private val rateClientConfig: IRatesClientConfig
): IRatesManager {
    private val disposables = CompositeDisposable()

    override val marketsUpdateSubject: BehaviorSubject<Unit> = BehaviorSubject.create()
    override val marketsStateSubject: BehaviorSubject<MarketState> = BehaviorSubject.create()

    private var bootstrapSynced = false
    private var cachedRates = listOf<Market>()

    init {
        marketsStateSubject.onNext(MarketState.SYNCING)

        initialStorageFetch()

        initialBootstrapConfig()
    }

    //region Private

    private fun initialBootstrapConfig() {
        bootstrapClient.getConfigs().ioSubscribe(disposables, {
            bootstrapSynced = true
            rateClientConfig.ipfsUrl = it.servers.first()
            rateClient.init(rateClientConfig)
            fetchRates()
        }, {
            Logger.e(it)
            fetchRates()
        })
    }

    @SuppressLint("CheckResult")
    private fun initialStorageFetch() {
        marketsStorage.getAllMarkets().ioSubscribe(disposables, {
            cachedRates = it
            marketsUpdateSubject.onNext(Unit)
        })
    }

    private fun fetchRates() {
        rateClient.getRates().ioSubscribe(disposables, {
                cachedRates = it.data.markets
                marketsStorage.save(*cachedRates.toTypedArray())
                marketsStateSubject.onNext(MarketState.SYNCED)
                marketsUpdateSubject.onNext(Unit)
            }, {
                marketsStateSubject.onNext(MarketState.FAILED)
                Logger.e(it)
            })
    }

    //endregion

    //region Public

    override fun getRate(coinCode: String, timeStamp: Long): Rate? =
        ratesStorage.getRate(coinCode, timeStamp)

    override fun getRateSingle(coinCode: String, timeStamp: Long): Single<Rate> =
        ratesStorage.getRateSingle(coinCode, timeStamp)
            .onErrorResumeNext(
                rateClient.getHistoricalRate(coinCode, timeStamp).map {
                    val rate = Rate(coinCode, timeStamp, it)
                    ratesStorage.save(rate)
                    rate
                }
            )

    override fun getMarkets(coinCodes: List<String>): List<Market> =
        cachedRates.filter {
            coinCodes.contains(it.coinCode) ||
                 coinCodes.indexOfFirst { symbol -> symbol.contains(it.coinCode) } >= 0
        }

    override fun getMarket(coinCode: String): Market =
        cachedRates.firstOrNull {
            it.coinCode == coinCode || it.coinCode.contains(coinCode, true)
        } ?: Market(coinCode)

    override fun refresh() {
        marketsStateSubject.value?.let {
            if (it != MarketState.SYNCING) {
                marketsStateSubject.onNext(MarketState.SYNCING)

                if (bootstrapSynced) {
                    fetchRates()
                } else {
                    initialBootstrapConfig()
                }
            }
        }
    }

    override fun stop() {
        disposables.dispose()
    }

    override fun clear() {
        marketsStorage.deleteAll()
        stop()
    }

    //endregion
}