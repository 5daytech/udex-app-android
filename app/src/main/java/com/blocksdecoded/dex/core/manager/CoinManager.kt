package com.blocksdecoded.dex.core.manager

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.CoinType

object CoinManager {
    val coins: List<Coin> = listOf(
        Coin("Ethereum", "ETH", CoinType.Ethereum),
        Coin("Wrapped ETH", "WETH", CoinType.Erc20("0xd0A1E359811322d97991E03f863a0C30C2cF029C", 18)),
        Coin("0x", "ZRX", CoinType.Erc20("0x2002d3812f58e35f0ea1ffbf80a75a38c32175fa", 18)),
        Coin("Tameki V2", "TMKV2", CoinType.Erc20("0x4fda2207fab83964bde2a9b75e24cc83af4c21c7", 18)),
        Coin("Papka", "PPA", CoinType.Erc20("0x2d756d19eb0a79b1c98e0fc433425095584878b2", 3)),
        Coin("Tameki", "TMK", CoinType.Erc20("0x0a5b9a7fc0474390101c075ad158c71779cb400f", 2))
    )

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

    val ercTokensNames : List<String> = coins
            .subList(1, coins.size)
            .map { it.code }

    val ercCoins: List<Coin> = coins
            .subList(1, coins.size)
}