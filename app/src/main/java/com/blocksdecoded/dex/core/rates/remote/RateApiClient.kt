package com.blocksdecoded.dex.core.rates.remote

import com.blocksdecoded.dex.core.network.CoreApiClient
import com.blocksdecoded.dex.core.rates.remote.model.RatesResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeoutException

class RateApiClient: CoreApiClient(), IRateClient {
    private var mConfig: IRateClientConfig? = null
    private var mClient: CurrencyNetworkClient? = null

    private fun <T> Single<T>.timeoutRetry(): Single<T> = this.retry { _, t2 ->
        when (t2) {
            is TimeoutException -> true
            else -> false
        }
    }

    //region Public

    override fun init(rateClientConfig: IRateClientConfig) {
        mConfig = rateClientConfig
        mClient = getRetrofitClient(
            mConfig?.ipfsUrl ?: "",
            CurrencyNetworkClient::class.java
        )
    }

    override fun getRates(): Single<RatesResponse> =
        mClient?.getCoins("${mConfig?.ipnsPath}index.json")
            ?.timeoutRetry() ?: Single.error(Exception("Rate api client not initialized"))

    //endregion

    private interface CurrencyNetworkClient {
        @GET
        fun getCoins(@Url url: String): Single<RatesResponse>
    }
}