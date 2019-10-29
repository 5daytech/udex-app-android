package com.blocksdecoded.dex.presentation.statschart

import com.blocksdecoded.dex.core.model.ChartType
import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.data.manager.rates.model.ChartPoint
import com.blocksdecoded.dex.data.manager.rates.model.StatsData
import java.math.BigDecimal

data class ChartViewItem(
    val type: ChartType,
    val rateValue: BigDecimal?,
    val marketCap: BigDecimal,
    val lowValue: BigDecimal,
    val highValue: BigDecimal,
    val diffValue: BigDecimal,
    val chartData: List<ChartPoint>,
    val lastUpdateTimestamp: Long? = null
)

class RateChartViewFactory {
    fun createViewItem(chartType: ChartType, statData: StatsData, rate: Rate?): ChartViewItem? {
        val diff = statData.diff[chartType.name] ?: return null
        val points = statData.stats[chartType.name] ?: return null

        val minValue = points.minBy { it.value }?.value ?: 0f
        val maxValue = points.maxBy { it.value }?.value ?: 0f

        val lowValue = minValue.toBigDecimal()
        val highValue = maxValue.toBigDecimal()
        val marketCap = statData.marketCap
        val rateValue = rate?.price

        return ChartViewItem(
            chartType,
            rateValue,
            marketCap,
            lowValue,
            highValue,
            diff,
            points,
            rate?.timestamp?.times(1000)
        )
    }
}
