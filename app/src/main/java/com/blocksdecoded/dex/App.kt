package com.blocksdecoded.dex

import android.app.Application
import com.blocksdecoded.dex.core.adapter.AdapterFactory
import com.blocksdecoded.dex.core.rates.bootstrap.BootstrapApiClient
import com.blocksdecoded.dex.core.manager.*
import com.blocksdecoded.dex.core.manager.fee.FeeRateProvider
import com.blocksdecoded.dex.core.manager.fee.IFeeRateProvider
import com.blocksdecoded.dex.core.rates.IRatesManager
import com.blocksdecoded.dex.core.rates.RatesManager
import com.blocksdecoded.dex.core.rates.remote.RatesApiClient
import com.blocksdecoded.dex.core.rates.remote.RatesClientConfig
import com.blocksdecoded.dex.core.security.ISecuredStorage
import com.blocksdecoded.dex.core.security.SecuredStorage
import com.blocksdecoded.dex.core.security.encryption.EncryptionManager
import com.blocksdecoded.dex.core.security.encryption.IEncryptionManager
import com.blocksdecoded.dex.core.AppConfiguration
import com.blocksdecoded.dex.core.rates.RatesConverter
import com.blocksdecoded.dex.core.storage.MarketsStorage
import com.blocksdecoded.dex.core.shared.AppLocalStorage
import com.blocksdecoded.dex.core.shared.IAppLocalStorage
import com.blocksdecoded.dex.core.shared.ISharedStorage
import com.blocksdecoded.dex.core.shared.SharedStorage
import com.blocksdecoded.dex.core.storage.AppDatabase
import com.blocksdecoded.dex.core.storage.RatesStorage
import com.blocksdecoded.dex.core.tradehistory.ITradeHistoryManager
import com.blocksdecoded.dex.core.tradehistory.TradeHistoryManager
import com.blocksdecoded.dex.core.zrx.IRelayerAdapterManager
import com.blocksdecoded.dex.core.zrx.RelayerAdapterManager
import com.blocksdecoded.dex.core.zrx.IZrxKitManager
import com.blocksdecoded.dex.core.zrx.ZrxKitManager

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

        lateinit var coinManager: ICoinManager
        lateinit var feeRateProvider: IFeeRateProvider
        lateinit var adapterManager: IAdapterManager
        lateinit var tradeHistoryManager: ITradeHistoryManager
        lateinit var relayerAdapterManager: IRelayerAdapterManager
        lateinit var authManager: AuthManager
        lateinit var wordsManager: IWordsManager
        lateinit var encryptionManager: IEncryptionManager
    
        // Rates
        
        lateinit var ratesManager: IRatesManager
        lateinit var ratesConverter: RatesConverter
        
        // Factories

        lateinit var adapterFactory: AdapterFactory
        
        // Helpers

        lateinit var appDatabase: AppDatabase
        lateinit var securedStorage: ISecuredStorage
        lateinit var localStorage: IAppLocalStorage
        lateinit var sharedStorage: ISharedStorage

    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        appConfiguration = AppConfiguration.DEFAULT

        coinManager = CoinManager(appConfiguration)
        sharedStorage = SharedStorage(this)
        localStorage = AppLocalStorage(sharedStorage)
        appDatabase = AppDatabase.getInstance(this)
        encryptionManager = EncryptionManager()
        securedStorage = SecuredStorage(encryptionManager, sharedStorage)

        // Auth
	    wordsManager = WordsManager(localStorage)
	    authManager = AuthManager(securedStorage)

        // Init kits
        ethereumKitManager = EthereumKitManager(appConfiguration)
        zrxKitManager = ZrxKitManager(
            appConfiguration,
            ethereumKitManager,
            authManager
        )

        feeRateProvider = FeeRateProvider(this)

        // Rates
        val marketsStorage = MarketsStorage(appDatabase.marketsDao())
        val historicalRatesStorage = RatesStorage(appDatabase.ratesDao())
        ratesManager = RatesManager(
            coinManager,
            marketsStorage,
            historicalRatesStorage,
            BootstrapApiClient(),
            RatesApiClient(),
            RatesClientConfig(appConfiguration, sharedStorage)
        )
        ratesConverter = RatesConverter(ratesManager = ratesManager)
        
        // Init adapter manager
        adapterFactory = AdapterFactory(appConfiguration, ethereumKitManager, feeRateProvider)
        adapterManager = AdapterManager(coinManager, adapterFactory, ethereumKitManager, authManager)
    
        relayerAdapterManager = RelayerAdapterManager(coinManager, ethereumKitManager, zrxKitManager, authManager)
        tradeHistoryManager =
            TradeHistoryManager(adapterManager)
    }
}