package com.blocksdecoded.dex.data.manager.rates

import io.horizontalsystems.xrateskit.entities.ChartInfo
import io.horizontalsystems.xrateskit.entities.ChartType
import io.horizontalsystems.xrateskit.entities.MarketInfo
import io.reactivex.Observable
import io.reactivex.Single
import java.math.BigDecimal

interface IRatesManager {
    fun getMarketsObservable(): Observable<Map<String, MarketInfo>>

    fun getMarkets(coinCodes: List<String>): List<MarketInfo?>

    fun getMarketInfo(coinCode: String): MarketInfo?

    fun chartInfo(coinCode: String, chartType: ChartType): ChartInfo?

    fun chartInfoObservable(coinCode: String, chartType: ChartType): Observable<ChartInfo>

    fun getHistoricalRate(coinCode: String, timeStamp: Long): Single<BigDecimal>

    fun getLatestRateSingle(coinCode: String): Single<BigDecimal>

    fun getLatestRate(coinCode: String): BigDecimal?

    fun refresh()
}
