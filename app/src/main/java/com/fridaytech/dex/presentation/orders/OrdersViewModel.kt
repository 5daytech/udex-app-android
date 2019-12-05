package com.fridaytech.dex.presentation.orders

import androidx.lifecycle.MutableLiveData
import com.fridaytech.dex.App
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.CoreViewModel
import com.fridaytech.dex.core.ui.SingleLiveEvent
import com.fridaytech.dex.data.manager.duration.ETransactionType
import com.fridaytech.dex.data.zrx.IRelayerAdapter
import com.fridaytech.dex.data.zrx.adapter.OrdersWatcher
import com.fridaytech.dex.data.zrx.model.SimpleOrder
import com.fridaytech.dex.presentation.orders.model.*
import com.fridaytech.dex.presentation.orders.model.EOrderSide.*
import com.fridaytech.dex.utils.Logger
import com.fridaytech.dex.utils.isValidIndex
import com.fridaytech.dex.utils.normalizedDiv
import com.fridaytech.dex.utils.rx.uiSubscribe
import io.reactivex.disposables.Disposable
import java.math.BigDecimal
import kotlin.math.absoluteValue

class OrdersViewModel : CoreViewModel() {
    private val coinManager = App.coinManager
    private val relayerManager = App.relayerAdapterManager
    private val ratesConverter = App.ratesConverter
    private val ratesManager = App.ratesManager
    private val adapterManager = App.adapterManager
    private val processingTimeProvider = App.processingDurationProvider

    private var marketsDisposable: Disposable? = null

    private val relayer: IRelayerAdapter?
        get() = relayerManager.mainRelayer
    private var zrxOrdersWatcher: OrdersWatcher? = null

    private val currentPair: Pair<String, String>?
        get() = availablePairs.value?.let { pairs ->
            selectedPairPosition.value?.let { position ->
                if (pairs.isValidIndex(position))
                    pairs[position].baseCoin to pairs[position].quoteCoin
                else
                    null
            }
        }

    private var mBuyOrders = listOf<SimpleOrder>()
    private var mSellOrders = listOf<SimpleOrder>()

    val availablePairs = MutableLiveData<List<ExchangePairViewItem>>()
    val selectedPairPosition = MutableLiveData<Int>()
    val buyOrders: MutableLiveData<List<SimpleOrder>> = MutableLiveData()
    val sellOrders: MutableLiveData<List<SimpleOrder>> = MutableLiveData()
    val myOrders: MutableLiveData<List<SimpleOrder>> = MutableLiveData()
    val exchangeCoinSymbol = MutableLiveData<String>()
    val spreadPercent = MutableLiveData<Double>()

    val orderInfoEvent = MutableLiveData<OrderInfoConfig>()
    val fillOrderEvent = MutableLiveData<FillOrderInfo>()
    val transactionSentEvent = SingleLiveEvent<String>()
    val cancelAllConfirmEvent = SingleLiveEvent<CancelOrderInfo>()

    init {
        relayerManager.mainRelayerUpdatedSignal
            .subscribe {
                relayer?.let {
                    zrxOrdersWatcher = OrdersWatcher(it, ratesConverter)
                    onRelayerInitialized()
                }
            }.let { disposables.add(it) }

        observeMarketsChange()

        coinManager.coinsUpdatedSubject
            .subscribe { observeMarketsChange() }
            .let { disposables.add(it) }

        spreadPercent.value = 0.0
    }

    //region Private

    private fun observeMarketsChange() {
        marketsDisposable?.dispose()
        ratesManager.getMarketsObservable()
            .subscribe {
                onPairsRefresh()
            }.let { marketsDisposable = it }
    }

    private fun onRelayerInitialized() {
        relayer?.pairsSyncSubject?.subscribe({
            onPairsRefresh()
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher?.buyOrdersSubject?.subscribe({ orders ->
            buyOrders.postValue(orders)
            mBuyOrders = orders
            updateSpread()
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher?.sellOrdersSubject?.subscribe({ orders ->
            sellOrders.postValue(orders)
            mSellOrders = orders
            updateSpread()
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher?.myOrdersSubject?.subscribe({ orders ->
            myOrders.postValue(orders)
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher?.selectedPairSubject?.subscribe({ position ->
            selectedPairPosition.postValue(position)

            availablePairs.value?.let {
                exchangeCoinSymbol.postValue(it[position].baseCoin)
            }
        }, { Logger.e(it) })?.let { disposables.add(it) }

        selectedPairPosition.postValue(0)
        refreshOrders()
    }

    private fun updateSpread() {
        val buyOrder = mBuyOrders.firstOrNull()
        val buyPrice = buyOrder?.makerAmount?.normalizedDiv(buyOrder.takerAmount)?.toDouble()

        val sellPrice = mSellOrders.firstOrNull()?.price?.toDouble()

        if (buyPrice != null && sellPrice != null) {
            val diffPercent = 100 * (buyPrice - sellPrice).absoluteValue / ((buyPrice + sellPrice) / 2)
            spreadPercent.postValue(diffPercent)
        } else {
            spreadPercent.postValue(0.0)
        }
    }

    private fun onPairsRefresh() {
        val pairs = relayer?.exchangePairs
            ?.map { it.baseCoinCode to it.quoteCoinCode } ?: listOf()

        val exchangePairs = pairs.map {
            ExchangePairViewItem(
                it.first,
                ratesConverter.getCoinPrice(it.first),
                it.second,
                ratesConverter.getCoinPrice(it.second)
            )
        }

        availablePairs.postValue(exchangePairs)

        if (pairs.isNotEmpty()) {
            val selectedPair = zrxOrdersWatcher?.currentSelectedPair
            selectedPairPosition.postValue(selectedPair)

            (selectedPair ?: 0).let {
                exchangeCoinSymbol.postValue(pairs[it].first)
            }
        }
    }

    private fun refreshOrders() {
        if (zrxOrdersWatcher != null) {
            mBuyOrders = zrxOrdersWatcher?.simpleBuyOrders ?: listOf()
            mSellOrders = zrxOrdersWatcher?.simpleSellOrders ?: listOf()

            buyOrders.postValue(zrxOrdersWatcher?.simpleBuyOrders)
            sellOrders.postValue(zrxOrdersWatcher?.simpleSellOrders)
            myOrders.postValue(zrxOrdersWatcher?.simpleMyOrders)
            updateSpread()
        }
    }

    private fun cancelAllMyOrders() {
        if (!myOrders.value.isNullOrEmpty()) {
            val orders = zrxOrdersWatcher?.getMyOrders()

            if (orders != null) {
                messageEvent.value = R.string.message_cancel_started
                relayer?.batchCancelOrders(orders)
                    ?.uiSubscribe(disposables, {
                        transactionSentEvent.postValue(it)
                    }, {
                        errorEvent.postValue(R.string.error_cancel_order)
                    })
            }
        }
    }

    //endregion

    fun onPickPair(position: Int) {
        zrxOrdersWatcher?.currentSelectedPair = position
    }

    fun onOrderClick(position: Int, side: EOrderSide) {
        when (side) {
            BUY -> {
                if (buyOrders.value != null && buyOrders.value!!.isValidIndex(position)) {
                    currentPair?.let {
                        fillOrderEvent.postValue(
                            FillOrderInfo(
                                it,
                                buyOrders.value!![position].remainingTakerAmount,
                                side
                            )
                        )
                    }
                }
            }
            SELL -> {
                if (sellOrders.value != null && sellOrders.value!!.isValidIndex(position)) {
                    currentPair?.let {
                        fillOrderEvent.postValue(
                            FillOrderInfo(
                                it,
                                sellOrders.value!![position].remainingTakerAmount,
                                side
                            )
                        )
                    }
                }
            }
            MY -> {
                if (myOrders.value != null && myOrders.value!!.isValidIndex(position)) {
                    val order = zrxOrdersWatcher?.getMyOrder(position, side)

                    if (order != null) {
                        orderInfoEvent.postValue(
                            OrderInfoConfig(
                                order.first,
                                order.second,
                                order.third
                            )
                        )
                    }
                }
            }
        }
    }

    fun onActionClick(side: EOrderSide) {
        when (side) {
            MY -> {
                if (!myOrders.value.isNullOrEmpty()) {
                    val orders = zrxOrdersWatcher?.getMyOrders()

                    if (orders != null) {
                        val adapter = adapterManager.adapters
                            .firstOrNull { it.coin.code == currentPair?.first } ?: return

                        val cancelInfo =
                            CancelOrderInfo(
                                orders.size,
                                BigDecimal.ZERO,
                                adapter.feeCoinCode,
                                processingTimeProvider.getEstimatedDuration(
                                    adapter.coin,
                                    ETransactionType.CANCEL
                                )
                            ) { cancelAllMyOrders() }

                        cancelAllConfirmEvent.postValue(cancelInfo)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        zrxOrdersWatcher?.stop()
        super.onCleared()
    }
}
