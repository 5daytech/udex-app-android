package com.blocksdecoded.dex.core.manager

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.AppConfiguration
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.CoinType

object CoinManager {
    val coins: List<Coin> = if (App.appConfiguration.testMode) {
        App.appConfiguration.testCoins
    } else {
        //TODO: Add mainnet coins
        App.appConfiguration.testCoins
    }
    
    fun getCoin(code: String): Coin =
        coins.firstOrNull { it.code == code } ?: throw Exception("Coin $code not found")

    fun getErcCoinForAddress(address: String): Coin? = coins.firstOrNull {
        when(it.type) {
            is CoinType.Erc20 -> it.type.address.equals(address, true)
            else -> false
        }
    }

    //TODO: Refactor
    fun addressForSymbol(symbol: String): String = ((coins.firstOrNull {
        when(it.type) {
            is CoinType.Erc20 -> it.code.equals(symbol, true)
            else -> false
        }
    } ?: coins[1]).type as? CoinType.Erc20)?.address ?: ""
}