package com.blocksdecoded.dex.core.model

data class CoinRate(
    val symbol: String,
    val totalSupply: Float = 0f,
    val circulatingSupply: Float = 0f,
    val volume: Float = 0f,
    val marketCap: Float = 0f,
    val price: Double = 0.0,
    val priceChange: Float = 0f
)