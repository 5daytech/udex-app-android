package com.blocksdecoded.dex.core.manager.rates.remote

import com.blocksdecoded.dex.core.manager.rates.model.RateStatData
import com.blocksdecoded.dex.core.manager.rates.remote.config.IRatesClientConfig
import com.blocksdecoded.dex.core.manager.rates.remote.model.RatesResponse
import com.blocksdecoded.dex.core.network.CoreApiClient
import com.blocksdecoded.dex.utils.TimeUtils
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url
import java.math.BigDecimal
import java.util.concurrent.TimeoutException

class RatesApiClient: CoreApiClient(), IRatesApiClient {
    private var mConfig: IRatesClientConfig? = null
    private var mCurrencyRateClient: CurrencyNetworkClient? = null
    private var mHistoricalRateMainClient: HistoricalRateNetworkClient? = null

    private fun historicalRateApiClient(hostType: HostType): HistoricalRateNetworkClient? =
        mHistoricalRateMainClient

    private fun <T> Single<T>.timeoutRetry(): Single<T> = this.retry { _, t2 ->
        when (t2) {
            is TimeoutException -> true
            else -> false
        }
    }

    //region Public

    override fun init(rateClientConfig: IRatesClientConfig) {
        mConfig = rateClientConfig

        mCurrencyRateClient = getRetrofitClient(
            mConfig?.ipfsUrl ?: "",
            CurrencyNetworkClient::class.java
        )

        mHistoricalRateMainClient = getRetrofitClient(
            rateClientConfig.historicalIpfsConfig,
            HistoricalRateNetworkClient::class.java
        )
    }

    override fun getHistoricalRate(coinCode: String, timestamp: Long): Single<BigDecimal> =
        historicalRateApiClient(HostType.MAIN)
            ?.getRateByHour(coinCode, "USD", TimeUtils.dateInUTC(timestamp, "yyyy/MM/dd/HH"))
            ?.flatMap { minuteRates ->
                Single.just(minuteRates.getValue(TimeUtils.dateInUTC(timestamp, "mm")).toBigDecimal())
            } ?: Single.error(Exception())

    override fun getRates(): Single<RatesResponse> =
        mCurrencyRateClient?.getCoins("${mConfig?.ipnsPath}index.json")
            ?.timeoutRetry() ?: Single.error(Exception("Market api client not initialized"))

    override fun getRateStats(coinCode: String): Single<RateStatData> =
        historicalRateApiClient(HostType.MAIN)?.getRateStats("USD", coinCode)
            ?: Single.error(Exception("Market api client not initialized"))

    //endregion

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

        @GET("xrates/stats/{fiat}/{coin}/index.json")
        fun getRateStats(
            @Path("fiat") currency: String,
            @Path("coin") coinCode: String
        ): Single<RateStatData>
    }

    private interface CurrencyNetworkClient {
        @GET
        fun getCoins(@Url url: String): Single<RatesResponse>
    }

    enum class HostType {
        MAIN, FALLBACK
    }
}