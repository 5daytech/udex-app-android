package com.blocksdecoded.dex.presentation.orders.model

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.CoinType
import com.blocksdecoded.dex.data.manager.ICoinManager
import com.blocksdecoded.dex.data.manager.rates.RatesConverter
import com.blocksdecoded.dex.data.zrx.OrdersUtil
import com.blocksdecoded.dex.utils.TimeUtils
import com.blocksdecoded.zrxkit.model.EAssetProxyId
import com.blocksdecoded.zrxkit.model.OrderInfo
import com.blocksdecoded.zrxkit.model.SignedOrder
import java.math.BigDecimal

data class UiOrder(
    val makerCoin: Coin,
    val takerCoin: Coin,
    val price: BigDecimal,
    val makerAmount: BigDecimal,
    val takerAmount: BigDecimal,
    val makerFiatAmount: BigDecimal,
    val takerFiatAmount: BigDecimal,
    val expireDate: String,
    val side: EOrderSide,
    val isMine: Boolean,
    val status: String,
    val filledAmount: BigDecimal
) {
    companion object {
        fun fromOrder(
            coinManager: ICoinManager,
            ratesConverter: RatesConverter,
            order: SignedOrder,
            side: EOrderSide,
            orderInfo: OrderInfo? = null,
            isMine: Boolean = false
        ): UiOrder {
            val normalizedData = OrdersUtil.normalizeOrderData(order)

            val takerCoin = coinManager.getErcCoinForAddress(EAssetProxyId.ERC20.decode(order.takerAssetData))!!

            val filledAmount = orderInfo?.orderTakerAssetFilledAmount?.toBigDecimal()
                ?.movePointLeft((takerCoin.type as CoinType.Erc20).decimal)
                ?.stripTrailingZeros() ?: BigDecimal.ZERO

            return UiOrder(
                normalizedData.makerCoin,
                normalizedData.takerCoin,
                normalizedData.price,
                normalizedData.makerAmount,
                normalizedData.takerAmount,
                ratesConverter.getCoinsPrice(normalizedData.makerCoin.code, normalizedData.makerAmount),
                ratesConverter.getCoinsPrice(normalizedData.takerCoin.code, normalizedData.takerAmount),
                TimeUtils.timestampToDisplay(order.expirationTimeSeconds.toLong()),
                side,
                isMine,
                orderInfo?.orderStatus ?: "unknown",
                filledAmount
            )
        }
    }
}
