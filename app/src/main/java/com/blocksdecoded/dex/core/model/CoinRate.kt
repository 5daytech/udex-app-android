package com.blocksdecoded.dex.core.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CoinRate(
	val symbol: String,
	val totalSupply: Float = 0f,
	val circulatingSupply: Float = 0f,
	val volume: Float = 0f,
	val marketCap: Float = 0f,
	val price: Double = 0.0,
	@Expose @SerializedName("change") val priceChange: Float = 0f,
	val history: List<Float> = listOf()
)