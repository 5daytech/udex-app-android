package com.fridaytech.dex.presentation.orders.model

import com.fridaytech.dex.core.model.Coin
import com.fridaytech.dex.core.model.CoinType
import com.fridaytech.dex.data.manager.ICoinManager
import com.fridaytech.dex.data.manager.rates.RatesConverter
import com.fridaytech.dex.data.zrx.OrdersUtil
import com.fridaytech.dex.utils.TimeUtils
import com.fridaytech.zrxkit.model.EAssetProxyId
import com.fridaytech.zrxkit.model.OrderInfo
import com.fridaytech.zrxkit.relayer.model.OrderRecord
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
            orderRecord: OrderRecord,
            side: EOrderSide,
            orderInfo: OrderInfo? = null,
            isMine: Boolean = false
        ): UiOrder {
            val normalizedData = OrdersUtil.normalizeOrderData(orderRecord)

            val takerCoin = coinManager.getErcCoinForAddress(EAssetProxyId.ERC20.decode(orderRecord.order.takerAssetData))!!

            val filledAmount = orderInfo?.orderTakerAssetFilledAmount?.toBigDecimal()
                ?.movePointLeft((takerCoin.type as CoinType.Erc20).decimal)
                ?.stripTrailingZeros() ?: BigDecimal.ZERO

            return UiOrder(
                normalizedData.makerCoin,
                normalizedData.takerCoin,
                normalizedData.price,
                normalizedData.remainingMakerAmount,
                normalizedData.remainingTakerAmount,
                ratesConverter.getCoinsPrice(
                    normalizedData.makerCoin.code,
                    normalizedData.remainingMakerAmount
                ),
                ratesConverter.getCoinsPrice(
                    normalizedData.takerCoin.code,
                    normalizedData.remainingTakerAmount
                ),
                TimeUtils.timestampToDisplay(orderRecord.order.expirationTimeSeconds.toLong()),
                side,
                isMine,
                orderInfo?.orderStatus ?: "unknown",
                filledAmount
            )
        }
    }
}
