package com.blocksdecoded.dex.presentation.statschart

import io.horizontalsystems.xrateskit.entities.ChartInfo
import io.horizontalsystems.xrateskit.entities.ChartPoint
import io.horizontalsystems.xrateskit.entities.ChartType
import io.horizontalsystems.xrateskit.entities.MarketInfo
import java.math.BigDecimal

data class ChartViewItem(
    val type: ChartType,
    val rateValue: BigDecimal?,
    val marketCap: Double,
    val lowValue: BigDecimal,
    val highValue: BigDecimal,
    val diffValue: BigDecimal,
    val chartData: List<ChartPoint>,
    val lastUpdateTimestamp: Long? = null
)

class RateChartViewFactory {
    fun createViewItem(chartType: ChartType, chartInfo: ChartInfo?, marketInfo: MarketInfo?): ChartViewItem? {
        val chartPoints = chartInfo?.points ?: listOf()

        val minValue = chartPoints.minBy { it.value }?.value?.toDouble() ?: 0.0
        val maxValue = chartPoints.maxBy { it.value }?.value?.toDouble() ?: 0.0

        val startValue = chartPoints.firstOrNull()?.value?.toDouble() ?: 0.0
        val endValue = chartPoints.lastOrNull()?.value?.toDouble() ?: 0.0
        val diffValue = ((endValue - startValue) / startValue * 100).toBigDecimal()

        return ChartViewItem(
            chartType,
            marketInfo?.rate,
            marketInfo?.marketCap ?: 0.0,
            minValue.toString().toBigDecimal(),
            maxValue.toString().toBigDecimal(),
            if (chartType == ChartType.DAILY) marketInfo?.diff ?: BigDecimal.ZERO else diffValue,
            chartInfo?.points ?: listOf(),
            marketInfo?.timestamp?.times(1000)
        )
    }
}
