package com.blocksdecoded.dex.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

@Entity
data class Market(
	@SerializedName("symbol") @PrimaryKey
	val coinCode: String,
	val marketCap: Float = 0f,
	var price: BigDecimal = BigDecimal.ZERO,
	@Expose @SerializedName("change") val priceChange: Float = 0f
)