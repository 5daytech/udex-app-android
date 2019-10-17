package com.blocksdecoded.dex

import android.app.Application
import com.blocksdecoded.dex.core.AppConfiguration
import com.blocksdecoded.dex.core.IAppConfiguration
import com.blocksdecoded.dex.core.adapter.AdapterFactory
import com.blocksdecoded.dex.core.manager.*
import com.blocksdecoded.dex.core.manager.auth.AuthManager
import com.blocksdecoded.dex.core.manager.auth.IAuthManager
import com.blocksdecoded.dex.core.manager.auth.WordsManager
import com.blocksdecoded.dex.core.manager.duration.IProcessingDurationProvider
import com.blocksdecoded.dex.core.manager.duration.ProcessingDurationProvider
import com.blocksdecoded.dex.core.manager.fee.FeeRateProvider
import com.blocksdecoded.dex.core.manager.fee.IFeeRateProvider
import com.blocksdecoded.dex.core.manager.history.ExchangeHistoryManager
import com.blocksdecoded.dex.core.manager.history.IExchangeHistoryManager
import com.blocksdecoded.dex.core.manager.rates.IRatesManager
import com.blocksdecoded.dex.core.manager.rates.RatesConverter
import com.blocksdecoded.dex.core.manager.rates.RatesManager
import com.blocksdecoded.dex.core.manager.rates.bootstrap.BootstrapApiClient
import com.blocksdecoded.dex.core.manager.rates.remote.RatesApiClient
import com.blocksdecoded.dex.core.manager.rates.remote.config.RatesClientConfig
import com.blocksdecoded.dex.core.manager.rates.stats.RatesStatsManager
import com.blocksdecoded.dex.core.manager.system.ISystemInfoManager
import com.blocksdecoded.dex.core.manager.system.SystemInfoManager
import com.blocksdecoded.dex.core.manager.zrx.IRelayerAdapterManager
import com.blocksdecoded.dex.core.manager.zrx.IZrxKitManager
import com.blocksdecoded.dex.core.manager.zrx.RelayerAdapterManager
import com.blocksdecoded.dex.core.manager.zrx.ZrxKitManager
import com.blocksdecoded.dex.core.network.NetworkStateManager
import com.blocksdecoded.dex.core.security.*
import com.blocksdecoded.dex.core.security.encryption.EncryptionManager
import com.blocksdecoded.dex.core.security.encryption.IEncryptionManager
import com.blocksdecoded.dex.core.shared.AppPreferences
import com.blocksdecoded.dex.core.shared.IAppPreferences
import com.blocksdecoded.dex.core.shared.ISharedStorage
import com.blocksdecoded.dex.core.shared.SharedStorage
import com.blocksdecoded.dex.core.storage.*

class App: Application() {
    companion object {
        lateinit var instance: App
            private set

        lateinit var appConfiguration: IAppConfiguration

        // Kits
        lateinit var zrxKitManager: IZrxKitManager
        lateinit var ethereumKitManager: IEthereumKitManager
        
        // Managers
        lateinit var coinManager: ICoinManager
        lateinit var adapterManager: IAdapterManager
        lateinit var exchangeHistoryManager: IExchangeHistoryManager
        lateinit var relayerAdapterManager: IRelayerAdapterManager
        lateinit var authManager: IAuthManager
        lateinit var wordsManager: IWordsManager
        lateinit var encryptionManager: IEncryptionManager
        lateinit var pinManager: IPinManager
        lateinit var keyStoreManager: IKeyStoreManager
        lateinit var keyStoreChangeListener: KeyStoreChangeListener
        lateinit var keyProvider: IKeyProvider
        lateinit var systemInfoManager: ISystemInfoManager
        lateinit var lockManager: ILockManager
        lateinit var backgroundManager: BackgroundManager
        lateinit var cleanupManager: ICleanupManager
        lateinit var networkStateManager: NetworkStateManager

        lateinit var feeRateProvider: IFeeRateProvider
        lateinit var processingDurationProvider: IProcessingDurationProvider

        // Rates
        lateinit var ratesManager: IRatesManager
        lateinit var ratesStatsManager: RatesStatsManager
        lateinit var ratesConverter: RatesConverter
        
        // Factories
        lateinit var adapterFactory: AdapterFactory
        
        // Storage
        lateinit var appDatabase: AppDatabase
        lateinit var enabledCoinsStorage: IEnabledCoinsStorage
        lateinit var securedStorage: ISecuredStorage
        lateinit var appPreferences: IAppPreferences
        lateinit var sharedStorage: ISharedStorage

        var lastExitDate: Long = 0
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        appConfiguration = AppConfiguration.DEFAULT

        sharedStorage = SharedStorage(this)
        appPreferences = AppPreferences(sharedStorage)
        appDatabase = AppDatabase.getInstance(this)

        enabledCoinsStorage = EnabledCoinsStorage(appDatabase.enabledCoinsDao())
        coinManager = CoinManager(appConfiguration, enabledCoinsStorage)

        KeyStoreManager("MASTER_KEY").apply {
            keyStoreManager = this
            keyProvider = this
            encryptionManager = EncryptionManager(this)
        }
        securedStorage = SecuredStorage(encryptionManager, sharedStorage)

        systemInfoManager = SystemInfoManager()
        backgroundManager = BackgroundManager(this)
        networkStateManager = NetworkStateManager()

        // Auth
	    wordsManager = WordsManager(appPreferences)
	    authManager = AuthManager(securedStorage, coinManager)
        pinManager = PinManager(securedStorage)

        keyStoreChangeListener = KeyStoreChangeListener(systemInfoManager, keyStoreManager).apply {
            backgroundManager.registerListener(this)
        }
        lockManager = LockManager(pinManager).apply {
            backgroundManager.registerListener(this)
        }

        // Init kits
        ethereumKitManager = EthereumKitManager(appConfiguration)
        zrxKitManager = ZrxKitManager(appConfiguration, authManager)

        feeRateProvider = FeeRateProvider(this)
        processingDurationProvider = ProcessingDurationProvider()

        // Rates
        val marketsStorage = MarketsStorage(appDatabase.marketsDao())
        val historicalRatesStorage = RatesStorage(appDatabase.ratesDao())
        ratesManager = RatesManager(
            coinManager,
            marketsStorage,
            historicalRatesStorage,
            BootstrapApiClient(),
            RatesApiClient(),
            RatesClientConfig(
                appConfiguration,
                sharedStorage
            )
        )
        ratesConverter = RatesConverter(ratesManager = ratesManager)
        ratesStatsManager = RatesStatsManager(RatesApiClient(), ratesManager)
        
        // Init adapter managers
        adapterFactory = AdapterFactory(ethereumKitManager, feeRateProvider)
        adapterManager = AdapterManager(coinManager, adapterFactory, ethereumKitManager, authManager).also {
            authManager.adapterManager = it
        }
        relayerAdapterManager = RelayerAdapterManager(coinManager, ethereumKitManager, zrxKitManager, authManager).also {
            authManager.relayerAdapterManager = it
        }
        exchangeHistoryManager = ExchangeHistoryManager(adapterManager)

        cleanupManager = CleanupManager(authManager, appPreferences, keyStoreManager)
    }
}