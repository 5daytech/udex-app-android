package com.blocksdecoded.dex.core.model

import androidx.room.Entity
import java.math.BigDecimal

@Entity(primaryKeys = ["coinCode", "timestamp", "isLatest"])
data class Rate(
    val coinCode: String,
    val timestamp: Long,
    val price: BigDecimal,
    val isLatest: Boolean
)
