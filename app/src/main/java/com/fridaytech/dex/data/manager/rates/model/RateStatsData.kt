package com.fridaytech.dex.data.manager.rates.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class RateData(
    @SerializedName("rates") val rates: List<Float>,
    @SerializedName("scale_minutes") val scale: Int,
    @SerializedName("timestamp") val timestamp: Long
)

data class RateStatData(
    @SerializedName("market_cap") val marketCap: BigDecimal,
    @SerializedName("stats") val stats: Map<String, RateData>
)

sealed class StatsResponse

data class StatsKey(
    val coinCode: String,
    val currencyCode: String
)

data class StatsData(
    val coinCode: String,
    val marketCap: BigDecimal,
    val stats: Map<String, List<ChartPoint>>,
    val diff: Map<String, BigDecimal>
) : StatsResponse()

data class StatsError(val coinCode: String) : StatsResponse()

data class ChartPoint(
    val value: Float,
    val timestamp: Long
)

data class LatestRateData(
    val rates: Map<String, String>,
    val currency: String,
    @SerializedName("time") val timestamp: Long
)
