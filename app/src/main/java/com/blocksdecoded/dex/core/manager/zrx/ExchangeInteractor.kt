package com.blocksdecoded.dex.core.manager.zrx

import com.blocksdecoded.dex.core.CancelOrderException
import com.blocksdecoded.dex.core.CreateOrderException
import com.blocksdecoded.dex.core.manager.ICoinManager
import com.blocksdecoded.dex.core.manager.zrx.model.RelayerOrdersList
import com.blocksdecoded.dex.core.model.CoinType
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.zrxkit.ZrxKit
import com.blocksdecoded.zrxkit.contracts.ZrxExchangeWrapper
import com.blocksdecoded.zrxkit.model.Order
import com.blocksdecoded.zrxkit.model.SignedOrder
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.Flowable
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

class ExchangeInteractor(
    private val coinManager: ICoinManager,
    private val ethereumKit: EthereumKit,
    private val zrxKit: ZrxKit,
    private val exchangeWrapper: ZrxExchangeWrapper,
    private val allowanceChecker: IAllowanceChecker
) : IExchangeInteractor {

    //region Private

    private fun postOrder(
        feeRecipient: String,
        makeAsset: String,
        makeAmount: BigInteger,
        takeAsset: String,
        takeAmount: BigInteger,
        side: EOrderSide
    ): Flowable<SignedOrder> {
        val expirationTime = ((Date().time / 1000) + (60 * 60 * 24 * 7)).toString() // Order valid for 7 days

        val makerAsset = when(side) {
            EOrderSide.BUY -> takeAsset
            else -> makeAsset
        }

        val takerAsset = when(side) {
            EOrderSide.BUY -> makeAsset
            else -> takeAsset
        }

        val order = Order(
            makerAddress = ethereumKit.receiveAddress.toLowerCase(),
            exchangeAddress = exchangeWrapper.contractAddress,
            makerAssetData = makerAsset,
            takerAssetData = takerAsset,
            makerAssetAmount = makeAmount.toString(),
            takerAssetAmount = takeAmount.toString(),
            expirationTimeSeconds = expirationTime,
            senderAddress = "0x0000000000000000000000000000000000000000",
            takerAddress = "0x0000000000000000000000000000000000000000",
            makerFee = "0",
            takerFee = "0",
            feeRecipientAddress = feeRecipient,
            salt = Date().time.toString()
        )

        val signedOrder = zrxKit.signOrder(order)

        return if (signedOrder != null) {
            zrxKit.relayerManager.postOrder(0, signedOrder)
                .map { signedOrder }
        } else {
            Flowable.error(CreateOrderException())
        }
    }

    //endregion

    //region Public

    override fun createOrder(
        feeRecipient: String,
        coinPair: Pair<String, String>,
        side: EOrderSide,
        amount: BigDecimal,
        price: BigDecimal
    ): Flowable<SignedOrder> {
        val baseCoin = coinManager.getCoin(coinPair.first).type as CoinType.Erc20
        val quoteCoin = coinManager.getCoin(coinPair.second).type as CoinType.Erc20

        val baseAsset = ZrxKit.assetItemForAddress(baseCoin.address)
        val quoteAsset = ZrxKit.assetItemForAddress(quoteCoin.address)

        val makerAmount = amount.movePointRight(
            if (side == EOrderSide.BUY) quoteCoin.decimal else baseCoin.decimal
        ).stripTrailingZeros().toBigInteger()

        val takerAmount = amount.multiply(price).movePointRight(
            if (side == EOrderSide.BUY) baseCoin.decimal else quoteCoin.decimal
        ).stripTrailingZeros().toBigInteger()

        return allowanceChecker.enableAssetPairAllowance(baseAsset to quoteAsset)
            .flatMap { postOrder(feeRecipient, baseAsset.assetData, makerAmount, quoteAsset.assetData, takerAmount, side) }
    }

    override fun cancelOrder(order: SignedOrder): Flowable<String> =
        if (order.makerAddress.equals(ethereumKit.receiveAddress, true)) {
            exchangeWrapper.cancelOrder(order)
        } else {
            Flowable.error(CancelOrderException(order.makerAddress, ethereumKit.receiveAddress))
        }

    override fun fill(
        orders: RelayerOrdersList<SignedOrder>,
        coinPair: Pair<String, String>,
        side: EOrderSide,
        amount: BigDecimal
    ): Flowable<String> {
        val baseCoin = coinManager.getCoin(coinPair.first).type as CoinType.Erc20
        val quoteCoin = coinManager.getCoin(coinPair.second).type as CoinType.Erc20

        val pairOrders = orders.getPair(
            ZrxKit.assetItemForAddress(baseCoin.address).assetData,
            ZrxKit.assetItemForAddress(quoteCoin.address).assetData
        )

        val calcAmount = amount.movePointRight(
            if (side == EOrderSide.BUY) quoteCoin.decimal else baseCoin.decimal
        )

        return allowanceChecker.enablePairAllowance(baseCoin.address to quoteCoin.address)
            .flatMap { exchangeWrapper.marketBuyOrders(pairOrders.orders, calcAmount.toBigInteger()) }
    }

    //endregion
}