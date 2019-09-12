package com.blocksdecoded.dex.presentation.keystore

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.keystore.KeyStoreActivity.Companion.ModeType.*

class KeyStoreViewModel : CoreViewModel() {

    private val authManager = App.authManager
    private val appPreferences = App.appPreferences
    private val keyStoreManager = App.keyStoreManager

    val title = MutableLiveData<Int>()

    val showNoSystemLockWarning = SingleLiveEvent<Void>()
    val showInvalidKeyWarning = SingleLiveEvent<Void>()
    val promptUserAuthentication = SingleLiveEvent<Void>()
    val openLaunchScreen = SingleLiveEvent<Void>()
    val closeApplication = SingleLiveEvent<Void>()

    private fun resetApp() {
        authManager.logout()
        appPreferences.clear()
    }

    private fun removeKey() {
        keyStoreManager.removeKey()
    }

    fun init(mode: KeyStoreActivity.Companion.ModeType) {
        title.value = when(mode) {
            NO_SYSTEM_LOCK -> R.string.keystore_system_lock_required
            INVALID_KEY -> R.string.keystore_key_invalidated
            USER_AUTHENTICATION -> R.string.keystore_user_auth
        }

        resetApp()

        when(mode) {
            NO_SYSTEM_LOCK -> showNoSystemLockWarning.call()
            INVALID_KEY -> showInvalidKeyWarning.call()
            USER_AUTHENTICATION -> promptUserAuthentication.call()
        }
    }

    fun onCloseInvalidKeyWarning() {
        removeKey()
        openLaunchScreen.call()
    }

    fun onAuthenticationCanceled() {
        closeApplication.call()
    }

    fun onAuthenticationSuccess() {
        openLaunchScreen.call()
    }
}