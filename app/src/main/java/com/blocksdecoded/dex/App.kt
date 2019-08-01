package com.blocksdecoded.dex

import android.app.Application
import com.blocksdecoded.dex.core.adapter.AdapterFactory
import com.blocksdecoded.dex.core.manager.*
import com.blocksdecoded.dex.core.zrx.IRelayerAdapterManager
import com.blocksdecoded.dex.core.zrx.RelayerAdapterManager

class App: Application() {
    companion object {
        private const val testMode = true

        lateinit var instance: App
            private set

        lateinit var appConfiguration: AppConfiguration

        // Kit Managers
        lateinit var zrxKitManager: IZrxKitManager
        lateinit var ethereumKitManager: EthereumKitManager
        
        // Helper Managers
        
        lateinit var feeRateProvider: IFeeRateProvider
        lateinit var adapterManager: IAdapterManager
        lateinit var relayerAdapterManager: IRelayerAdapterManager

        // Factories

        lateinit var adapterFactory: AdapterFactory

    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        appConfiguration = AppConfiguration.DEFAULT

        // Init kits
        ethereumKitManager = EthereumKitManager(testMode, appConfiguration)
        zrxKitManager = ZrxKitManager(ethereumKitManager)
        feeRateProvider = FeeRateProvider(this)

        // Init adapter manager
        adapterFactory = AdapterFactory(appConfiguration, ethereumKitManager, feeRateProvider)
        adapterManager = AdapterManager(adapterFactory, ethereumKitManager)
    
        relayerAdapterManager = RelayerAdapterManager(ethereumKitManager, zrxKitManager)
        
        adapterManager.initAdapters(CoinManager.coins)
    }
}