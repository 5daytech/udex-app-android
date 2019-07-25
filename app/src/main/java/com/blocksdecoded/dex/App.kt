package com.blocksdecoded.dex

import android.app.Application
import com.blocksdecoded.dex.core.adapter.AdapterFactory
import com.blocksdecoded.dex.core.manager.*

class App: Application() {
    companion object {
        private const val testMode = true

        lateinit var instance: App
            private set

        lateinit var appConfiguration: AppConfiguration

        // Managers

        lateinit var zrxKitManager: IZrxKitManager
        lateinit var ethereumKitManager: EthereumKitManager
        lateinit var adapterManager: IAdapterManager

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

        // Init adapter manager
        adapterFactory = AdapterFactory(appConfiguration, ethereumKitManager)
        adapterManager = AdapterManager(adapterFactory, ethereumKitManager)


        adapterManager.initAdapters(CoinManager.coins)
    }
}