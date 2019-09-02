package com.blocksdecoded.dex.core.manager

import com.blocksdecoded.dex.core.AppConfiguration
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.CoinType

class CoinManager(
    appConfiguration: AppConfiguration
) : ICoinManager {
    private val baseCoins = listOf("BTC", "ETH")

    override val coins: List<Coin> = if (appConfiguration.testMode) {
        appConfiguration.testCoins
    } else {
        //TODO: Add mainnet coins
        appConfiguration.testCoins
    }

    override fun cleanCoinCode(coinCode: String): String {
        val baseIndex = baseCoins.indexOfFirst {
            coinCode.startsWith("W") &&
                    it == coinCode.substring(1)
        }

        return if (baseIndex >= 0) {
            baseCoins[baseIndex]
        } else {
            coinCode
        }
    }
    
    override fun getCoin(code: String): Coin =
        coins.firstOrNull { it.code == code } ?: throw Exception("Coin $code not found")

    override fun getErcCoinForAddress(address: String): Coin? = coins.firstOrNull {
        when(it.type) {
            is CoinType.Erc20 -> it.type.address.equals(address, true)
            else -> false
        }
    }
}