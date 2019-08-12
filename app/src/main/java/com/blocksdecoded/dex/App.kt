package com.blocksdecoded.dex

import android.app.Application
import com.blocksdecoded.dex.core.adapter.AdapterFactory
import com.blocksdecoded.dex.core.rates.bootstrap.BootstrapApiClient
import com.blocksdecoded.dex.core.manager.*
import com.blocksdecoded.dex.core.rates.IRatesManager
import com.blocksdecoded.dex.core.rates.RatesManager
import com.blocksdecoded.dex.core.rates.remote.RatesApiClient
import com.blocksdecoded.dex.core.rates.remote.RatesClientConfig
import com.blocksdecoded.dex.core.security.ISecuredStorage
import com.blocksdecoded.dex.core.security.SecuredStorage
import com.blocksdecoded.dex.core.security.encryption.EncryptionManager
import com.blocksdecoded.dex.core.security.encryption.IEncryptionManager
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
        lateinit var authManager: AuthManager
        lateinit var wordsManager: IWordsManager
        lateinit var encryptionManager: IEncryptionManager
        
        lateinit var securedStorage: ISecuredStorage
        lateinit var localStorage: IAppLocalStorage

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
        localStorage = AppLocalStorage(sharedStorage)
        
	    // Auth
        encryptionManager = EncryptionManager()
	    securedStorage = SecuredStorage(encryptionManager, sharedStorage)
	    wordsManager = WordsManager(localStorage)
	    authManager = AuthManager(securedStorage)

        // Init kits
        ethereumKitManager = EthereumKitManager(appConfiguration)
        zrxKitManager = ZrxKitManager(ethereumKitManager, authManager)
        feeRateProvider = FeeRateProvider(this)

        ratesManager = RatesManager(BootstrapApiClient(), RatesApiClient(), RatesClientConfig(sharedStorage))

        // Init adapter manager
        adapterFactory = AdapterFactory(appConfiguration, ethereumKitManager, feeRateProvider)
        adapterManager = AdapterManager(CoinManager, adapterFactory, ethereumKitManager, authManager)
    
        relayerAdapterManager = RelayerAdapterManager(ethereumKitManager, zrxKitManager, authManager)
    }
}