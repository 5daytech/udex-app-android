package com.blocksdecoded.dex.core.rates

import android.annotation.SuppressLint
import android.util.Log
import com.blocksdecoded.dex.core.rates.bootstrap.IBootstrapClient
import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.core.rates.remote.IRatesApiClient
import com.blocksdecoded.dex.core.rates.remote.IRatesClientConfig
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.ioSubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class RatesManager(
    private val ratesStorage: IRatesStorage,
    private val bootstrapClient: IBootstrapClient,
    private val rateClient: IRatesApiClient,
    private val rateClientConfig: IRatesClientConfig
): IRatesManager {
    private val disposables = CompositeDisposable()

    override val ratesUpdateSubject: BehaviorSubject<Unit> = BehaviorSubject.create()
    override val ratesStateSubject: BehaviorSubject<RatesState> = BehaviorSubject.create()

    private var bootstrapSynced = false
    private var cachedRates = listOf<Rate>()

    init {
        ratesStateSubject.onNext(RatesState.SYNCING)

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
        ratesStorage.allRates().ioSubscribe(disposables, {
            cachedRates = it
            ratesUpdateSubject.onNext(Unit)
        })
    }

    private fun fetchRates() {
        rateClient.getRates().ioSubscribe(disposables, {
                cachedRates = it.data.rates
                ratesStorage.save(*cachedRates.toTypedArray())
                ratesStateSubject.onNext(RatesState.SYNCED)
                ratesUpdateSubject.onNext(Unit)
            }, {
                ratesStateSubject.onNext(RatesState.FAILED)
                Logger.e(it)
            })
    }

    //endregion

    //region Public

    override fun getRates(codes: List<String>): List<Rate> =
        cachedRates.filter {
            codes.contains(it.symbol) ||
                 codes.indexOfFirst { symbol -> symbol.contains(it.symbol) } >= 0
        }

    override fun getRate(code: String): Rate =
        cachedRates.firstOrNull {
            it.symbol == code || it.symbol.contains(code, true)
        } ?: Rate(code)

    override fun refresh() {
        ratesStateSubject.value?.let {
            if (it != RatesState.SYNCING) {
                ratesStateSubject.onNext(RatesState.SYNCING)

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
        ratesStorage.deleteAll()
        stop()
    }

    //endregion
}