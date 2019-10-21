package com.blocksdecoded.dex.core.manager.rates.stats

import com.blocksdecoded.dex.core.manager.rates.model.StatsResponse
import io.reactivex.Flowable

interface IRatesStatsManager {
    val statsFlowable: Flowable<StatsResponse>

    fun syncStats(coinCode: String)

    fun getStats(coinCode: String): StatsResponse?
}