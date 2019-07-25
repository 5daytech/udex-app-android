package com.blocksdecoded.dex

import android.app.Application
import com.blocksdecoded.dex.core.manager.EthereumKitManager
import com.blocksdecoded.dex.core.manager.IZrxKitManager
import com.blocksdecoded.dex.core.manager.ZrxKitManager

class App: Application() {
    companion object {
        private val testMode = true

        lateinit var instance: App
            private set

        lateinit var zrxKitManager: IZrxKitManager
        lateinit var ethereumKitManager: EthereumKitManager
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        ethereumKitManager = EthereumKitManager(testMode)
        zrxKitManager = ZrxKitManager(ethereumKitManager)
    }
}