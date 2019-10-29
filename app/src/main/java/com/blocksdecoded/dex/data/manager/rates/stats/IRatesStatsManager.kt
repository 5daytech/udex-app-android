package com.blocksdecoded.dex.data.manager.rates.stats

import com.blocksdecoded.dex.data.manager.rates.model.StatsResponse
import io.reactivex.Flowable

interface IRatesStatsManager {
    val statsFlowable: Flowable<StatsResponse>

    fun syncStats(coinCode: String)

    fun getStats(coinCode: String): StatsResponse?
}
