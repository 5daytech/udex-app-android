package com.blocksdecoded.dex.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "rate")
data class Rate(
	@PrimaryKey
	val symbol: String,
	val totalSupply: Float = 0f,
	val circulatingSupply: Float = 0f,
	val volume: Float = 0f,
	val marketCap: Float = 0f,
	val price: Double = 0.0,
	@Expose @SerializedName("change") val priceChange: Float = 0f,
	val history: List<Float> = listOf()
)