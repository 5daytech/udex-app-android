package com.fridaytech.dex.data.zrx.adapter

import com.fridaytech.dex.data.zrx.IAllowanceChecker
import com.fridaytech.dex.presentation.orders.model.EOrderSide
import com.fridaytech.zrxkit.ZrxKit
import com.fridaytech.zrxkit.model.AssetItem
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.Flowable
import java.math.BigInteger

class AllowanceChecker(
    private val ethereumKit: EthereumKit,
    private val zrxKit: ZrxKit
) : IAllowanceChecker {
    private fun checkAndUnlockTokenAddress(address: String): Flowable<Boolean> {
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

    // Give allowance on fill
    override fun checkAndUnlockPairForFill(
        pair: Pair<String, String>,
        side: EOrderSide
    ): Flowable<Boolean> {
        val addressToUnlock = when (side) {
            EOrderSide.SELL -> pair.second
            else -> pair.first
        }

        return checkAndUnlockTokenAddress(addressToUnlock)
    }

    // Give allowance on order create
    override fun checkAndUnlockAssetPairForPost(
        assetPair: Pair<AssetItem, AssetItem>,
        side: EOrderSide
    ): Flowable<Boolean> {
        val addressToUnlock = when (side) {
            EOrderSide.SELL -> assetPair.first.address
            else -> assetPair.second.address
        }

        return checkAndUnlockTokenAddress(addressToUnlock)
    }
}
