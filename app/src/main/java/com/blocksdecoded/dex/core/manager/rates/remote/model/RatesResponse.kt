package com.blocksdecoded.dex.core.manager.rates.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RatesResponse(
    @Expose @SerializedName("status") val status: String,
    @Expose @SerializedName("data") val data: RatesDataResponse
)