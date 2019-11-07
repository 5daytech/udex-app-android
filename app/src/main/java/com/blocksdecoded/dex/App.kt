package com.blocksdecoded.dex

import android.app.Application
import com.blocksdecoded.dex.core.AppConfiguration
import com.blocksdecoded.dex.core.IAppConfiguration
import com.blocksdecoded.dex.core.network.NetworkStateManager
import com.blocksdecoded.dex.core.shared.AppPreferences
import com.blocksdecoded.dex.core.shared.IAppPreferences
import com.blocksdecoded.dex.core.shared.ISharedStorage
import com.blocksdecoded.dex.core.shared.SharedStorage
import com.blocksdecoded.dex.data.adapter.AdapterFactory
import com.blocksdecoded.dex.data.manager.*
import com.blocksdecoded.dex.data.manager.auth.AuthManager
import com.blocksdecoded.dex.data.manager.auth.IAuthManager
import com.blocksdecoded.dex.data.manager.auth.WordsManager
import com.blocksdecoded.dex.data.manager.duration.IProcessingDurationProvider
import com.blocksdecoded.dex.data.manager.duration.ProcessingDurationProvider
import com.blocksdecoded.dex.data.manager.fee.FeeRateProvider
import com.blocksdecoded.dex.data.manager.fee.IFeeRateProvider
import com.blocksdecoded.dex.data.manager.history.ExchangeHistoryManager
import com.blocksdecoded.dex.data.manager.history.IExchangeHistoryManager
import com.blocksdecoded.dex.data.manager.rates.IRatesManager
import com.blocksdecoded.dex.data.manager.rates.RatesConverter
import com.blocksdecoded.dex.data.manager.rates.RatesManager
import com.blocksdecoded.dex.data.manager.system.ISystemInfoManager
import com.blocksdecoded.dex.data.manager.system.SystemInfoManager
import com.blocksdecoded.dex.data.security.*
import com.blocksdecoded.dex.data.security.encryption.EncryptionManager
import com.blocksdecoded.dex.data.security.encryption.IEncryptionManager
import com.blocksdecoded.dex.data.storage.AppDatabase
import com.blocksdecoded.dex.data.storage.EnabledCoinsStorage
import com.blocksdecoded.dex.data.storage.IEnabledCoinsStorage
import com.blocksdecoded.dex.data.zrx.IRelayerAdapterManager
import com.blocksdecoded.dex.data.zrx.IZrxKitManager
import com.blocksdecoded.dex.data.zrx.RelayerAdapterManager
import com.blocksdecoded.dex.data.zrx.ZrxKitManager

class App : Application() {
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
        feeRateProvider = FeeRateProvider(this)
        processingDurationProvider = ProcessingDurationProvider()

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
        zrxKitManager = ZrxKitManager(appConfiguration, authManager, feeRateProvider)

        // Rates
        ratesManager = RatesManager(this, coinManager)
        ratesConverter = RatesConverter(ratesManager = ratesManager)

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
