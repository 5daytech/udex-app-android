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
	val totalSupply: Float = 0f,
	val circulatingSupply: Float = 0f,
	val volume: Float = 0f,
	val marketCap: Float = 0f,
	var price: BigDecimal = BigDecimal.ZERO,
	@Expose @SerializedName("change") val priceChange: Float = 0f,
	val history: List<Float> = listOf()
)