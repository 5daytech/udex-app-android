package com.blocksdecoded.dex.core.adapter

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.manager.AppConfiguration
import com.blocksdecoded.dex.core.manager.IEthereumKitManager
import com.blocksdecoded.dex.core.manager.IFeeRateProvider
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.CoinType
import java.math.BigDecimal

class AdapterFactory(
    private val appConfigProvider: AppConfiguration,
    private val ethereumKitManager: IEthereumKitManager,
    private val feeRateProvider: IFeeRateProvider
) {

    fun adapterForCoin(coin: Coin): IAdapter = when (coin.type) {
        is CoinType.Ethereum -> {
            EthereumAdapter(coin, ethereumKitManager.defaultKit(), feeRateProvider)
        }
        is CoinType.Erc20 -> {
            Erc20Adapter(coin, App.instance, ethereumKitManager.defaultKit(), feeRateProvider, coin.type.decimal, BigDecimal(0.0), coin.type.address, coin.code)
        }
    }

    fun unlinkAdapter(adapter: IAdapter) {
        when (adapter) {
            is EthereumBaseAdapter -> {
                ethereumKitManager.unlink()
            }
        }
    }
}
