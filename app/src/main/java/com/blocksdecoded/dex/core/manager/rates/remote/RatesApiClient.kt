package com.blocksdecoded.dex.core.manager.rates.remote

import com.blocksdecoded.dex.core.network.CoreApiClient
import com.blocksdecoded.dex.core.manager.rates.remote.model.RatesResponse
import com.blocksdecoded.dex.utils.TimeUtils
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url
import java.math.BigDecimal
import java.util.concurrent.TimeoutException

class RatesApiClient: CoreApiClient(), IRatesApiClient {
    private var mConfig: IRatesClientConfig? = null
    private var mClient: CurrencyNetworkClient? = null
    private var historicalRateMainClient: HistoricalRateNetworkClient? = null

    private fun historicalRateApiClient(hostType: HostType): HistoricalRateNetworkClient? {
        return historicalRateMainClient
    }

    private fun <T> Single<T>.timeoutRetry(): Single<T> = this.retry { _, t2 ->
        when (t2) {
            is TimeoutException -> true
            else -> false
        }
    }

    //region Public

    override fun init(rateClientConfig: IRatesClientConfig) {
        mConfig = rateClientConfig

        mClient = getRetrofitClient(
            mConfig?.ipfsUrl ?: "",
            CurrencyNetworkClient::class.java
        )

        historicalRateMainClient = getRetrofitClient(
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
        mClient?.getCoins("${mConfig?.ipnsPath}index.json")
            ?.timeoutRetry() ?: Single.error(Exception("Market api client not initialized"))

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
    }

    private interface CurrencyNetworkClient {
        @GET
        fun getCoins(@Url url: String): Single<RatesResponse>
    }
}