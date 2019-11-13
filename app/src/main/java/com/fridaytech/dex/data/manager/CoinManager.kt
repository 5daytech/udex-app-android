package com.fridaytech.dex.data.manager

import com.fridaytech.dex.core.IAppConfiguration
import com.fridaytech.dex.core.model.Coin
import com.fridaytech.dex.core.model.CoinType
import com.fridaytech.dex.core.model.EnabledCoin
import com.fridaytech.dex.data.storage.IEnabledCoinsStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class CoinManager(
    private val appConfiguration: IAppConfiguration,
    private val enabledCoinsStorage: IEnabledCoinsStorage
) : ICoinManager {
    private val baseCoins = listOf("BTC", "ETH")
    override val coinsUpdatedSubject: PublishSubject<Unit> = PublishSubject.create()

    init {
        val disposable = enabledCoinsStorage.enabledCoinsObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { enabledCoinsFromStorage ->
                if (enabledCoinsFromStorage.isEmpty()) {
                    enableDefaultCoins()
                    return@subscribe
                }

                val enabledCoins = mutableListOf<Coin>()
                enabledCoinsFromStorage.forEach { enabledCoin ->
                    allCoins.firstOrNull { coin -> coin.code == enabledCoin.coinCode }
                        ?.let { enabledCoins.add(it) }
                }
                coins = enabledCoins
            }
    }

    override val allCoins: List<Coin>
        get() = appConfiguration.allCoins

    override var coins: List<Coin> = listOf()
        set(value) {
            field = value
            coinsUpdatedSubject.onNext(Unit)
        }

    override fun cleanCoinCode(coinCode: String): String {
        val baseIndex = baseCoins.indexOfFirst {
            coinCode.startsWith("W") &&
                    it == coinCode.substring(1)
        }

        return if (baseIndex >= 0) baseCoins[baseIndex] else coinCode
    }

    override fun getCoin(code: String): Coin =
        allCoins.firstOrNull { it.code == code } ?: throw Exception("Coin $code not found")

    override fun getErcCoinForAddress(address: String): Coin? = coins.firstOrNull {
        when (it.type) {
            is CoinType.Erc20 -> it.type.address.equals(address, true)
            else -> false
        }
    }

    override fun enableDefaultCoins() {
        val enabledCoins = mutableListOf<EnabledCoin>()
        appConfiguration.defaultCoinCodes.forEachIndexed { order, coinCode ->
            enabledCoins.add(EnabledCoin(coinCode, order))
        }
        enabledCoinsStorage.save(enabledCoins)
    }

    override fun clear() {
        coins = listOf()
        enabledCoinsStorage.deleteAll()
    }
}
