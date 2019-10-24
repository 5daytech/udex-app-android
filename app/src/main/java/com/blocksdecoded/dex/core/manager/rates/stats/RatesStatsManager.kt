package com.blocksdecoded.dex.core.manager.rates.stats

import com.blocksdecoded.dex.core.manager.ICoinManager
import com.blocksdecoded.dex.core.manager.rates.IRatesManager
import com.blocksdecoded.dex.core.manager.rates.model.*
import com.blocksdecoded.dex.core.manager.rates.remote.IRatesApiClient
import com.blocksdecoded.dex.core.model.ChartType
import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.rx.ioSubscribe
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import java.math.BigDecimal
import java.util.*

class RatesStatsManager(
    private val coinManager: ICoinManager,
    private val ratesApiClient: IRatesApiClient,
    private val rateStorage: IRatesManager
): IRatesStatsManager {
    private val cacheUpdateTimeInterval: Long = 30 * 60 * 60 // 30 minutes in seconds
    private val disposables = CompositeDisposable()
    private val cache = mutableMapOf<StatsKey, Pair<Long?, RateStatData>>()
    private val statsSubject = PublishSubject.create<StatsResponse>()

    override val statsFlowable: Flowable<StatsResponse>
        get() = statsSubject.toFlowable(BackpressureStrategy.BUFFER)

    override fun syncStats(coinCode: String) {
        val statsKey = StatsKey(coinCode, "USD")
        val currentTime = Date().time / 1000 // timestamp in seconds
        val cached = cache[statsKey]

        val rateStats = if (cached != null && cached.first ?: 0 > currentTime - cacheUpdateTimeInterval) {
            Single.just(cached.second)
        } else {
            ratesApiClient.getRateStats(coinManager.cleanCoinCode(coinCode))
                .onErrorResumeNext { ratesApiClient.getRateStats(coinManager.cleanCoinCode(coinCode)) }
        }

        val rateLocal = rateStorage.getLatestRateSingle(coinCode)

        Single.zip(
            rateLocal,
            rateStats,
            BiFunction<Rate, RateStatData, StatsData> { rate, stats ->
                prepareStatsData(stats, statsKey, rate)
            }).ioSubscribe(disposables, {
                statsSubject.onNext(it)
            }, {
                Logger.e(it)
                statsSubject.onNext(StatsError(coinCode))
            })
    }

    override fun getStats(coinCode: String): StatsResponse? {
        val key = StatsKey(coinCode, "USD")
        val data = cache[key]?.second
        val rate = rateStorage.getLatestRate(coinCode)

        return if (data != null && rate != null) {
            prepareStatsData(data, key, rate)
        } else {
            null
        }
    }

    private fun prepareStatsData(
        data: RateStatData,
        key: StatsKey,
        rate: Rate?
    ): StatsData {
        val lastDailyTimestamp = data.stats[ChartType.DAILY.name]?.timestamp
        cache[key] = Pair(lastDailyTimestamp, data)

        val stats = mutableMapOf<String, List<ChartPoint>>()
        val diffs = mutableMapOf<String, BigDecimal>()

        for (type in data.stats.keys) {
            val statsData = data.stats[type] ?: continue
            val chartType = ChartType.fromString(type)
            val chartData = convert(statsData, rate, chartType)

            stats[type] = chartData
            diffs[type] = growthDiff(chartData)
        }

        return StatsData(key.coinCode, data.marketCap, stats, diffs)
    }

    fun clear() {
        disposables.clear()
        cache.clear()
    }

    private fun convert(data: RateData, rate: Rate?, chartType: ChartType): List<ChartPoint> {
        val rates = when (chartType) {
            ChartType.MONTHLY18 -> data.rates.takeLast(ChartType.annualPoints) // for one year
            else -> data.rates
        }

        val points = convert(rates, data.scale, data.timestamp).toMutableList()
        if (rate != null) {
            points.add(ChartPoint(rate.price.toFloat(), rate.timestamp))
        }

        return points
    }


    private fun convert(points: List<Float>, scaleMinutes: Int, lastTimestamp: Long): List<ChartPoint> {
        val scaleSecs = scaleMinutes * 60
        var timestamp = lastTimestamp

        val chartPoints = mutableListOf<ChartPoint>()

        for (i in (points.size - 1) downTo 0) {
            chartPoints.add(0, ChartPoint(points[i], timestamp))
            timestamp -= scaleSecs
        }

        return chartPoints
    }

    private fun growthDiff(points: List<ChartPoint>): BigDecimal {
        val pointStart = points.first { it.value != 0f }
        val pointEnd = points.last()

        return ((pointEnd.value - pointStart.value) / pointStart.value * 100).toBigDecimal()
    }
}