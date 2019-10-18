package com.blocksdecoded.dex.core.manager.rates.remote

import com.blocksdecoded.dex.core.IAppConfiguration
import com.blocksdecoded.dex.core.manager.rates.model.LatestRateData
import com.blocksdecoded.dex.core.manager.rates.model.RateStatData
import com.blocksdecoded.dex.core.network.CoreApiClient
import com.blocksdecoded.dex.utils.TimeUtils
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import java.math.BigDecimal
import java.util.concurrent.TimeoutException

class RatesApiClient(
    appConfiguration: IAppConfiguration
): CoreApiClient(), IRatesApiClient {
    private var mainClient: HistoricalRateNetworkClient? = null

    private fun historicalRateApiClient(hostType: HostType): HistoricalRateNetworkClient? =
        mainClient

    private fun <T> Single<T>.timeoutRetry(): Single<T> = this.retry { _, t2 ->
        when (t2) {
            is TimeoutException -> true
            else -> false
        }
    }

    //region Public

    init {
        mainClient = getRetrofitClient(
            "https://${appConfiguration.ipfsMainGateway}/ipns/${appConfiguration.ipfsId}/",
            HistoricalRateNetworkClient::class.java
        )
    }

    override fun getHistoricalRate(coinCode: String, timestamp: Long): Single<BigDecimal> =
        historicalRateApiClient(HostType.MAIN)
            ?.getRateByHour(coinCode, "USD", TimeUtils.dateInUTC(timestamp, "yyyy/MM/dd/HH"))
            ?.flatMap { minuteRates ->
                Single.just(minuteRates.getValue(TimeUtils.dateInUTC(timestamp, "mm")).toBigDecimal())
            } ?: Single.error(Exception())

    override fun getLatestRates(): Single<LatestRateData> =
        historicalRateApiClient(HostType.MAIN)?.getLatestRates("USD")
            ?.timeoutRetry() ?: Single.error(Exception("Market api client not initialized"))

    override fun getRateStats(coinCode: String): Single<RateStatData> =
        historicalRateApiClient(HostType.MAIN)?.getRateStats("USD", coinCode)
            ?: Single.error(Exception("Market api client not initialized"))

    //endregion

    enum class HostType {
        MAIN, FALLBACK
    }

    private interface HistoricalRateNetworkClient {
        @GET("xrates/historical/{coin}/{fiat}/{datePath}/index.json")
        fun getRateByDay(
            @Path("coin") coinCode: String,
            @Path("fiat") currency: String,
            @Path("datePath") datePath: String
        ): Single<String>

        @GET("xrates/historical/{coin}/{fiat}/{datePath}/index.json")
        fun getRateByHour(
            @Path("coin") coinCode: String,
            @Path("fiat") currency: String,
            @Path("datePath") datePath: String
        ): Single<Map<String, String>>

        @GET("xrates/latest/{fiat}/index.json")
        fun getLatestRates(
            @Path("fiat") currency: String
        ): Single<LatestRateData>

        @GET("xrates/stats/{fiat}/{coin}/index.json")
        fun getRateStats(
            @Path("fiat") currency: String,
            @Path("coin") coinCode: String
        ): Single<RateStatData>
    }
}