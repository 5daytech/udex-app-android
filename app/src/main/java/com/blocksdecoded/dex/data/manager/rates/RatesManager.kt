package com.blocksdecoded.dex.data.manager.rates

import android.content.Context
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.data.manager.ICoinManager
import io.horizontalsystems.xrateskit.XRatesKit
import io.horizontalsystems.xrateskit.entities.ChartInfo
import io.horizontalsystems.xrateskit.entities.ChartType
import io.horizontalsystems.xrateskit.entities.MarketInfo
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.math.BigDecimal

class RatesManager(
    context: Context,
    private val coinManager: ICoinManager
) : IRatesManager {
    private val currencyCode = "USD"
    private val disposables = CompositeDisposable()
    private val kit: XRatesKit = XRatesKit.create(context, currencyCode, 60 * 10)

    init {
        coinManager.coinsUpdatedSubject
            .subscribe {
                onCoinsUpdated(coinManager.coins)
            }.let { disposables.add(it) }
    }

    //region Private

    private fun cleanCode(coinCode: String): String =
        coinManager.cleanCoinCode(coinCode)

    private fun onCoinsUpdated(coins: List<Coin>) {
        kit.set(coins.map { cleanCode(it.code) })
    }

    //endregion

    //region Public

    //region Rates

    override fun getHistoricalRate(coinCode: String, timeStamp: Long): Single<BigDecimal> {
        return kit.historicalRate(cleanCode(coinCode), currencyCode, timeStamp)
    }

    override fun getLatestRateSingle(coinCode: String): Single<BigDecimal> {
        val rate = getLatestRate(coinCode)

        return if (rate != null) {
            Single.just(rate)
        } else {
            Single.error(NullPointerException())
        }
    }

    override fun getLatestRate(coinCode: String): BigDecimal? {
        val marketInfo = getMarketInfo(coinCode)

        return when {
            marketInfo == null -> null
            marketInfo.isExpired() -> null
            else -> marketInfo.rate
        }
    }

    //endregion

    //region Markets

    override fun getMarketsObservable(): Observable<Map<String, MarketInfo>> =
        kit.marketInfoMapObservable(currencyCode)

    override fun chartInfoObservable(
        coinCode: String,
        chartType: ChartType
    ): Observable<ChartInfo> {
        return kit.chartInfoObservable(cleanCode(coinCode), currencyCode, chartType)
    }

    override fun getMarketInfo(coinCode: String): MarketInfo? {
        return kit.getMarketInfo(cleanCode(coinCode), currencyCode)
    }

    override fun getMarkets(coinCodes: List<String>): List<MarketInfo?> =
        coinCodes.map { getMarketInfo(it) }

    //endregion

    override fun chartInfo(coinCode: String, chartType: ChartType): ChartInfo? {
        return kit.getChartInfo(cleanCode(coinCode), currencyCode, chartType)
    }

    override fun refresh() {
        kit.refresh()
    }

    //endregion
}
