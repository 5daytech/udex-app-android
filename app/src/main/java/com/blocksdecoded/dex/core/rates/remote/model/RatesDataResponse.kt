package com.blocksdecoded.dex.core.rates.remote.model

import com.blocksdecoded.dex.core.model.CoinRate
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class RatesDataResponse(
    @Expose @SerializedName("coins") var rates: List<CoinRate>,
    @Expose @SerializedName("updated_at") var updatedAt: Date? = Date()
)