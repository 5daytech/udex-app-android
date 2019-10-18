package com.blocksdecoded.dex.core.manager.rates.stats

import com.blocksdecoded.dex.core.manager.rates.IRatesManager
import com.blocksdecoded.dex.core.manager.rates.model.*
import com.blocksdecoded.dex.core.manager.rates.remote.IRatesApiClient
import com.blocksdecoded.dex.core.model.ChartType
import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.utils.Logger
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.math.BigDecimal
import java.util.*

class RatesStatsManager(
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
            ratesApiClient.getRateStats(coinCode)
                .onErrorResumeNext { ratesApiClient.getRateStats(coinCode) }
        }

        val rateLocal = rateStorage.getLatestRateSingle(coinCode)

        Single.zip(rateLocal, rateStats, BiFunction<Rate, RateStatData, Pair<Rate, RateStatData>> { a, b -> Pair(a, b) })
            .map { (rate, data) ->
                val lastDailyTimestamp = data.stats[ChartType.DAILY.name]?.timestamp
                cache[statsKey] = Pair(lastDailyTimestamp, data)

                val stats = mutableMapOf<String, List<ChartPoint>>()
                val diffs = mutableMapOf<String, BigDecimal>()

                for (type in data.stats.keys) {
                    val statsData = data.stats[type] ?: continue
                    val chartType = ChartType.fromString(type) ?: continue
                    val chartData = convert(statsData, rate, chartType)

                    stats[type] = chartData
                    diffs[type] = growthDiff(chartData)
                }

                StatsData(coinCode, data.marketCap, stats, diffs)
            }
            .subscribeOn(Schedulers.io())
            .subscribe({
                statsSubject.onNext(it)
            }, {
                Logger.e(it)
                statsSubject.onNext(StatsError(coinCode))
            })
            .let { disposables.add(it) }
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


    fun convert(points: List<Float>, scaleMinutes: Int, lastTimestamp: Long): List<ChartPoint> {
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