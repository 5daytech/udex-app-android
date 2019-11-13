package com.fridaytech.dex.data.manager

import com.fridaytech.dex.core.model.AuthData
import com.fridaytech.dex.core.model.Coin
import com.fridaytech.dex.data.adapter.IAdapter
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface ICoinManager {
    val coinsUpdatedSubject: PublishSubject<Unit>
    val coins: List<Coin>
    val allCoins: List<Coin>

    fun enableDefaultCoins()
    fun clear()
    fun cleanCoinCode(coinCode: String): String
    fun getCoin(code: String): Coin
    fun getErcCoinForAddress(address: String): Coin?
}

interface IEthereumKitManager {
    val kit: EthereumKit?

    fun ethereumKit(authData: AuthData): EthereumKit
    fun refresh()
    fun unlink()
}

interface IAdapterManager {
    val adapters: List<IAdapter>
    val adaptersUpdatedSignal: BehaviorSubject<Unit>

    fun refresh()
    fun initAdapters()
    fun stopKits()
}

interface IWordsManager {
    var isBackedUp: Boolean
    var backedUpSignal: PublishSubject<Unit>

    fun validate(words: List<String>)
    fun generateWords(): List<String>
}

interface ICleanupManager {
    fun logout()
    fun cleanUserData()
    fun removeKey()
}
