package com.blocksdecoded.dex.core.model

enum class ChartType {
    DAILY,
    WEEKLY,
    MONTHLY,
    MONTHLY6,
    MONTHLY18;

    companion object {
        val annualPoints = 53
        val map = values().associateBy(ChartType::name)
        fun fromString(type: String?): ChartType? = map[type]
    }
}