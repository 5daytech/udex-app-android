package com.fridaytech.dex

import android.app.Application
import com.fridaytech.dex.core.AppConfiguration
import com.fridaytech.dex.core.IAppConfiguration
import com.fridaytech.dex.core.network.NetworkStateManager
import com.fridaytech.dex.core.shared.AppPreferences
import com.fridaytech.dex.core.shared.IAppPreferences
import com.fridaytech.dex.core.shared.ISharedStorage
import com.fridaytech.dex.core.shared.SharedStorage
import com.fridaytech.dex.data.adapter.AdapterFactory
import com.fridaytech.dex.data.buycrypto.BuyCryptoProvider
import com.fridaytech.dex.data.buycrypto.IBuyCryptoProvider
import com.fridaytech.dex.data.manager.*
import com.fridaytech.dex.data.manager.auth.AuthManager
import com.fridaytech.dex.data.manager.auth.IAuthManager
import com.fridaytech.dex.data.manager.auth.WordsManager
import com.fridaytech.dex.data.manager.duration.IProcessingDurationProvider
import com.fridaytech.dex.data.manager.duration.ProcessingDurationProvider
import com.fridaytech.dex.data.manager.fee.FeeRateProvider
import com.fridaytech.dex.data.manager.fee.IFeeRateProvider
import com.fridaytech.dex.data.manager.history.ExchangeHistoryManager
import com.fridaytech.dex.data.manager.history.IExchangeHistoryManager
import com.fridaytech.dex.data.manager.rates.IRatesManager
import com.fridaytech.dex.data.manager.rates.RatesConverter
import com.fridaytech.dex.data.manager.rates.RatesManager
import com.fridaytech.dex.data.manager.system.ISystemInfoManager
import com.fridaytech.dex.data.manager.system.SystemInfoManager
import com.fridaytech.dex.data.security.*
import com.fridaytech.dex.data.security.encryption.EncryptionManager
import com.fridaytech.dex.data.security.encryption.IEncryptionManager
import com.fridaytech.dex.data.storage.AppDatabase
import com.fridaytech.dex.data.storage.EnabledCoinsStorage
import com.fridaytech.dex.data.storage.IEnabledCoinsStorage
import com.fridaytech.dex.data.zrx.IRelayerAdapterManager
import com.fridaytech.dex.data.zrx.IZrxKitManager
import com.fridaytech.dex.data.zrx.RelayerAdapterManager
import com.fridaytech.dex.data.zrx.ZrxKitManager

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
        lateinit var buyCryptoProvider: IBuyCryptoProvider

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

        keyStoreChangeListener = KeyStoreChangeListener(
            systemInfoManager,
            keyStoreManager
        ).apply {
            backgroundManager.registerListener(this)
        }
        lockManager = LockManager(pinManager).apply {
            backgroundManager.registerListener(this)
        }

        // Init kits
        ethereumKitManager = EthereumKitManager(appConfiguration)
        zrxKitManager = ZrxKitManager(appConfiguration, authManager, feeRateProvider)

        buyCryptoProvider = BuyCryptoProvider(appConfiguration)

        // Rates
        ratesManager = RatesManager(this, coinManager)
        ratesConverter = RatesConverter(ratesManager = ratesManager)

        // Init adapter managers
        adapterFactory = AdapterFactory(ethereumKitManager, feeRateProvider)
        adapterManager = AdapterManager(
            coinManager,
            adapterFactory,
            ethereumKitManager,
            authManager
        ).also {
            authManager.adapterManager = it
        }
        relayerAdapterManager = RelayerAdapterManager(
            coinManager,
            ethereumKitManager,
            zrxKitManager,
            authManager
        ).also {
            authManager.relayerAdapterManager = it
        }
        exchangeHistoryManager = ExchangeHistoryManager(adapterManager)

        cleanupManager = CleanupManager(authManager, appPreferences, keyStoreManager)
    }
}
