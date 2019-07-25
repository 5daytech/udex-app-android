package com.blocksdecoded.dex.core.manager

import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.model.AuthData
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.zrxkit.ZrxKit
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.Observable
import org.web3j.tx.gas.ContractGasProvider

interface IEthereumKitManager {
    val gasProvider: ContractGasProvider
    val ethereumKit: EthereumKit?

    fun ethereumKit(authData: AuthData): EthereumKit
    fun defaultKit(): EthereumKit
    fun unlink()
}

interface IZrxKitManager {
    fun zrxKit(): ZrxKit
}

interface IAdapterManager {
    val adapters: List<IAdapter>
    val adaptersUpdatedSignal: Observable<Unit>

    fun refresh()
    fun initAdapters(coins: List<Coin>)
    fun stopKits()
}