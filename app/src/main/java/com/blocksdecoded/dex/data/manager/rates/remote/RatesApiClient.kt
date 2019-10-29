package com.blocksdecoded.dex.data.manager.rates.remote

import com.blocksdecoded.dex.core.IAppConfiguration
import com.blocksdecoded.dex.core.network.CoreApiClient
import com.blocksdecoded.dex.data.manager.rates.model.LatestRateData
import com.blocksdecoded.dex.data.manager.rates.model.RateStatData
import com.blocksdecoded.dex.utils.TimeUtils
import io.reactivex.Single
import java.math.BigDecimal
import java.util.concurrent.TimeoutException
import retrofit2.http.GET
import retrofit2.http.Path

class RatesApiClient(
    appConfiguration: IAppConfiguration
) : CoreApiClient(), IRatesApiClient {
    private var mainClient = getRetrofitClient(
        "https://${appConfiguration.ipfsMainGateway}/ipns/${appConfiguration.ipfsId}/",
        HistoricalRateNetworkClient::class.java
    )

    private fun getApiClient(hostType: HostType): HistoricalRateNetworkClient =
        mainClient

    private fun <T> Single<T>.timeoutRetry(): Single<T> = this.retry { _, t2 ->
        when (t2) {
            is TimeoutException -> true
            else -> false
        }
    }

    //region Public

    override fun getHistoricalRate(coinCode: String, timestamp: Long): Single<BigDecimal> =
        getApiClient(HostType.MAIN)
            .getRateByHour(coinCode, "USD", TimeUtils.dateInUTC(timestamp, "yyyy/MM/dd/HH"))
            .map { minuteRates ->
                minuteRates.getValue(TimeUtils.dateInUTC(timestamp, "mm")).toBigDecimal()
            }.onErrorResumeNext(getDayRate(coinCode, timestamp))

    override fun getLatestRates(): Single<LatestRateData> =
        getApiClient(HostType.MAIN).getLatestRates("USD")
            .timeoutRetry()

    override fun getRateStats(coinCode: String): Single<RateStatData> =
        getApiClient(HostType.MAIN).getRateStats("USD", coinCode)

    //endregion

    private fun getDayRate(coinCode: String, timestamp: Long): Single<BigDecimal> =
        getApiClient(HostType.MAIN)
            .getRateByDay(coinCode, "USD", TimeUtils.dateInUTC(timestamp, "yyyy/MM/dd"))
            .map { it.toBigDecimal() }

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
