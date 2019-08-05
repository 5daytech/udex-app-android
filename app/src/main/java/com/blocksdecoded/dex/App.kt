package com.blocksdecoded.dex

import android.app.Application
import com.blocksdecoded.dex.core.adapter.AdapterFactory
import com.blocksdecoded.dex.core.rates.bootstrap.BootstrapApiClient
import com.blocksdecoded.dex.core.manager.*
import com.blocksdecoded.dex.core.rates.IRatesManager
import com.blocksdecoded.dex.core.rates.RatesManager
import com.blocksdecoded.dex.core.rates.remote.RatesApiClient
import com.blocksdecoded.dex.core.rates.remote.RatesClientConfig
import com.blocksdecoded.dex.core.shared.ISharedStorage
import com.blocksdecoded.dex.core.shared.SharedStorage
import com.blocksdecoded.dex.core.zrx.IRelayerAdapterManager
import com.blocksdecoded.dex.core.zrx.RelayerAdapterManager

class App: Application() {
    companion object {
        private const val testMode = true

        lateinit var instance: App
            private set

        lateinit var appConfiguration: AppConfiguration

        // Kits

        lateinit var zrxKitManager: IZrxKitManager
        lateinit var ethereumKitManager: EthereumKitManager
        
        // Managers

        lateinit var feeRateProvider: IFeeRateProvider
        lateinit var adapterManager: IAdapterManager
        lateinit var relayerAdapterManager: IRelayerAdapterManager
        lateinit var ratesManager: IRatesManager

        // Factories

        lateinit var adapterFactory: AdapterFactory

        // Helpers

        lateinit var sharedStorage: ISharedStorage

    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        appConfiguration = AppConfiguration.DEFAULT

        sharedStorage = SharedStorage(this)

        // Init kits
        ethereumKitManager = EthereumKitManager(testMode, appConfiguration)
        zrxKitManager = ZrxKitManager(ethereumKitManager)
        feeRateProvider = FeeRateProvider(this)

        ratesManager = RatesManager(BootstrapApiClient(), RatesApiClient(), RatesClientConfig(sharedStorage))

        // Init adapter manager
        adapterFactory = AdapterFactory(appConfiguration, ethereumKitManager, feeRateProvider)
        adapterManager = AdapterManager(adapterFactory, ethereumKitManager)
    
        relayerAdapterManager = RelayerAdapterManager(ethereumKitManager, zrxKitManager)
        
        adapterManager.initAdapters(CoinManager.coins)
    }
}