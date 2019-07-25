package com.blocksdecoded.dex.core.manager

import com.blocksdecoded.dex.core.model.AuthData
import com.blocksdecoded.zrxkit.ZrxKit
import io.horizontalsystems.ethereumkit.core.EthereumKit
import org.web3j.tx.gas.ContractGasProvider

interface IEthereumKitManager {
    val gasProvider: ContractGasProvider
    fun ethereumKit(authData: AuthData): EthereumKit
    fun defaultKit(): EthereumKit
    fun unlink()
}

interface IZrxKitManager {
    fun zrxKit(): ZrxKit
}