package com.blocksdecoded.dex.data.zrx.model

import com.blocksdecoded.zrxkit.model.AssetItem

data class ExchangePair(
    val baseCoinCode: String,
    val quoteCoinCode: String,
    val baseAsset: AssetItem,
    val quoteAsset: AssetItem
)
