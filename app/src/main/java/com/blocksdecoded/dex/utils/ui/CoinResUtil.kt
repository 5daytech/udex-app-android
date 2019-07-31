package com.blocksdecoded.dex.utils.ui

import com.blocksdecoded.dex.App

object CoinResUtil {
    fun getResForCoinCode(coinCode: String): Int = getCoinDrawableResource(coinCode)

    private fun getCoinDrawableResource(coinCode: String): Int {
        val coinResourceName = "coin_${coinCode.toLowerCase()}"
        return App.instance.resources.getIdentifier(coinResourceName, "drawable", App.instance.packageName)
    }
}