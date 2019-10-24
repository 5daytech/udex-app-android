package com.blocksdecoded.dex.presentation.coinmanager

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.IAppConfiguration
import com.blocksdecoded.dex.core.manager.ICoinManager
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.EnabledCoin
import com.blocksdecoded.dex.core.storage.IEnabledCoinsStorage
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.utils.rx.uiSubscribe

class CoinManagerViewModel(
    private val appConfiguration: IAppConfiguration = App.appConfiguration,
    private val coinManager: ICoinManager = App.coinManager,
    private val enabledCoinsStorage: IEnabledCoinsStorage = App.enabledCoinsStorage
) : CoreViewModel() {
    private var allCoins = listOf<Coin>()
        set(value) {
            field = value
            setDisabledCoins()
        }

    private var enabledCoins = mutableListOf<Coin>()
        set(value) {
            field = value
            setDisabledCoins()
        }
    private var disabledCoins = listOf<Coin>()

    val enabledCoinsCount: Int
        get() = enabledCoins.size
    val disabledCoinsCount: Int
        get() = disabledCoins.size

    val syncCoinsEvent = SingleLiveEvent<Unit>()
    val finishEvent = SingleLiveEvent<Unit>()

    fun init() {
        allCoins = coinManager.allCoins
        syncCoinsEvent.call()

        enabledCoinsStorage.enabledCoinsObservable()
            .uiSubscribe(disposables, { enabledCoinsFromStorage ->
                val enabledCoins = mutableListOf<Coin>()

                enabledCoinsFromStorage.forEach { enabledCoin ->
                    coinManager.allCoins
                        .firstOrNull { coin -> coin.code == enabledCoin.coinCode}?.let { enabledCoins.add(it) }
                }

                this.enabledCoins = enabledCoins
                syncCoinsEvent.call()
            }, { })
    }

    private fun saveEnabledCoins(coins: List<Coin>) {
        val enabledCoins = mutableListOf<EnabledCoin>()

        coins.forEachIndexed{order, coinCode ->
            enabledCoins.add(EnabledCoin(coinCode.code, order))
        }

        enabledCoinsStorage.save(enabledCoins)
    }

    //region Update state

    private fun enable(coin: Coin) {
        enabledCoins.add(coin)
        setDisabledCoins()
    }

    private fun disable(coin: Coin) {
        enabledCoins.remove(coin)
        setDisabledCoins()
    }

    private fun move(coin: Coin, index: Int) {
        enabledCoins.remove(coin)
        enabledCoins.add(index, coin)
    }

    private fun setDisabledCoins() {
        disabledCoins = allCoins.filter { !enabledCoins.contains(it) }
    }

    //endregion

    fun canBeDisabled(position: Int): Boolean {
        return enabledCoins.isValidIndex(position) &&
                !appConfiguration.fixedCoinCodes.contains(enabledCoins[position].code)
    }

    fun onBackPress() {
        finishEvent.call()
    }

    fun onSaveClick() {
        saveEnabledCoins(enabledCoins)
        finishEvent.call()
    }

    fun enabledItemForIndex(position: Int): Coin = enabledCoins[position]

    fun disabledItemForIndex(disabledIndex: Int): Coin = disabledCoins[disabledIndex]

    fun moveCoin(from: Int, to: Int) {
        move(enabledCoins[from], to)
    }

    fun enableCoin(position: Int) {
        enable(disabledCoins[position])
        syncCoinsEvent.call()
    }

    fun disableCoin(position: Int) {
        if (canBeDisabled(position)) {
            disable(enabledCoins[position])
            syncCoinsEvent.call()
        }
    }
}