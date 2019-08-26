package com.blocksdecoded.dex.core.rates

import com.blocksdecoded.dex.core.rates.bootstrap.IBootstrapClient
import com.blocksdecoded.dex.core.model.CoinRate
import com.blocksdecoded.dex.core.rates.remote.IRatesApiClient
import com.blocksdecoded.dex.core.rates.remote.IRatesClientConfig
import com.blocksdecoded.dex.utils.Logger
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class RatesManager(
    private val bootstrapClient: IBootstrapClient,
    private val rateClient: IRatesApiClient,
    private val rateClientConfig: IRatesClientConfig
): IRatesManager {
    private val disposables = CompositeDisposable()

    override val ratesUpdateSubject: BehaviorSubject<Unit> = BehaviorSubject.create()
    override val ratesStateSubject: BehaviorSubject<RatesState> = BehaviorSubject.create()

    private var cachedRates = listOf<CoinRate>()

    init {
        ratesStateSubject.onNext(RatesState.SYNCING)

        bootstrapClient.getConfigs()
            .subscribeOn(Schedulers.io())
            .subscribe({
                rateClientConfig.ipfsUrl = it.servers.first()
                rateClient.init(rateClientConfig)
                fetchRates()
            }, {
                Logger.e(it)
            }).let { disposables.add(it) }
    }

    private fun fetchRates() {
        rateClient.getRates()
            .subscribeOn(Schedulers.io())
            .subscribe({
                cachedRates = it.data.rates
                ratesStateSubject.onNext(RatesState.SYNCED)
                ratesUpdateSubject.onNext(Unit)
            }, {
                ratesStateSubject.onNext(RatesState.FAILED)
                Logger.e(it)
            }).let { disposables.add(it) }
    }

    //region Public

    override fun getRates(codes: List<String>): List<CoinRate> =
        cachedRates.filter {
            codes.contains(it.symbol) ||
                 codes.indexOfFirst { symbol -> symbol.contains(it.symbol) } >= 0
        }

    override fun getRate(code: String): CoinRate =
        cachedRates.firstOrNull {
            it.symbol == code || it.symbol.contains(code, true)
        } ?: CoinRate(code)

    override fun refresh() {
        ratesStateSubject.onNext(RatesState.SYNCING)
        fetchRates()
    }

    override fun stop() {
        disposables.dispose()
    }

    override fun clear() {

    }

    //endregion
}