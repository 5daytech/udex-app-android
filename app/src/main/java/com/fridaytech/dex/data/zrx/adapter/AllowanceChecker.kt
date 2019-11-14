package com.fridaytech.dex.data.zrx.adapter

import com.fridaytech.dex.data.zrx.IAllowanceChecker
import com.fridaytech.zrxkit.ZrxKit
import com.fridaytech.zrxkit.model.AssetItem
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.Flowable
import java.math.BigInteger

class AllowanceChecker(
    private val ethereumKit: EthereumKit,
    private val zrxKit: ZrxKit
) : IAllowanceChecker {
    override fun enableAllowance(address: String): Flowable<Boolean> {
        val coinWrapper = zrxKit.getErc20ProxyInstance(address)

        return coinWrapper.proxyAllowance(ethereumKit.receiveAddress)
            .flatMap {
                if (it > BigInteger.ZERO) {
                    Flowable.just(true)
                } else {
                    coinWrapper.setUnlimitedProxyAllowance().map {
                        true
                    }
                }
            }
    }

    override fun enablePairAllowance(pair: Pair<String, String>): Flowable<Boolean> {
        return enableAllowance(pair.first)
            .flatMap { enableAllowance(pair.second) }
    }

    override fun enableAssetPairAllowance(assetPair: Pair<AssetItem, AssetItem>): Flowable<Boolean> {
        val base = assetPair.first
        val quote = assetPair.second

        return enablePairAllowance(base.address to quote.address)
    }
}
