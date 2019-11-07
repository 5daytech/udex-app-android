package com.blocksdecoded.dex.presentation.statschart

import io.horizontalsystems.xrateskit.entities.ChartInfo
import io.horizontalsystems.xrateskit.entities.ChartPoint
import io.horizontalsystems.xrateskit.entities.ChartType
import io.horizontalsystems.xrateskit.entities.MarketInfo
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
    fun createViewItem(chartType: ChartType, chartInfo: ChartInfo?, marketInfo: MarketInfo?): ChartViewItem? {
        val minValue = chartInfo?.points?.minBy { it.value }?.value ?: 0f
        val maxValue = chartInfo?.points?.maxBy { it.value }?.value ?: 0f

        return ChartViewItem(
            chartType,
            marketInfo?.rate,
            marketInfo?.marketCap?.toBigDecimal() ?: BigDecimal.ZERO,
            minValue.toString().toBigDecimal(),
            maxValue.toString().toBigDecimal(),
            marketInfo?.diff ?: BigDecimal.ZERO,
            chartInfo?.points ?: listOf(),
            marketInfo?.timestamp?.times(1000)
        )
    }
}
