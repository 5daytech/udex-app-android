package com.blocksdecoded.dex.core.manager

import com.blocksdecoded.dex.core.IAppConfiguration
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.CoinType
import io.reactivex.subjects.PublishSubject

class CoinManager(
    val appConfiguration: IAppConfiguration
) : ICoinManager {
    private val baseCoins = listOf("BTC", "ETH")

    override val coinsUpdatedSubject: PublishSubject<Unit> = PublishSubject.create()
    override val allCoins: List<Coin>
        get() = appConfiguration.allCoins

    override var coins: List<Coin> = appConfiguration.allCoins
        set(value) {
            field = value
            coinsUpdatedSubject.onNext(Unit)
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

    override fun enableDefaultCoins() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clear() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}