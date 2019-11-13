package com.fridaytech.dex.presentation.keystore

import androidx.lifecycle.MutableLiveData
import com.fridaytech.dex.App
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.CoreViewModel
import com.fridaytech.dex.core.ui.SingleLiveEvent
import com.fridaytech.dex.presentation.keystore.KeyStoreActivity.Companion.ModeType.*

class KeyStoreViewModel : CoreViewModel() {

    private val cleanupManager = App.cleanupManager

    val title = MutableLiveData<Int>()

    val showNoSystemLockWarning = SingleLiveEvent<Void>()
    val showInvalidKeyWarning = SingleLiveEvent<Void>()
    val promptUserAuthentication = SingleLiveEvent<Void>()
    val openLaunchScreen = SingleLiveEvent<Void>()
    val closeApplication = SingleLiveEvent<Void>()

    fun init(mode: KeyStoreActivity.Companion.ModeType) {
        title.value = when (mode) {
            NO_SYSTEM_LOCK -> R.string.keystore_system_lock_required
            INVALID_KEY -> R.string.keystore_key_invalidated
            USER_AUTHENTICATION -> R.string.keystore_user_auth
        }

        cleanupManager.cleanUserData()

        when (mode) {
            NO_SYSTEM_LOCK -> showNoSystemLockWarning.call()
            INVALID_KEY -> showInvalidKeyWarning.call()
            USER_AUTHENTICATION -> promptUserAuthentication.call()
        }
    }

    fun onCloseInvalidKeyWarning() {
        cleanupManager.removeKey()
        openLaunchScreen.call()
    }

    fun onAuthenticationCanceled() {
        closeApplication.call()
    }

    fun onAuthenticationSuccess() {
        openLaunchScreen.call()
    }
}
