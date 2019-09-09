package com.blocksdecoded.dex.presentation.settings.security

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent

class SecurityCenterViewModel : CoreViewModel() {

    private val appPreferences = App.appPreferences
    private val pinManager = App.pinManager
    private val systemInfoManager = App.systemInfoManager

    val passcodeOptionsEnabled = MutableLiveData<Boolean>()
    val fingerprintVisible = MutableLiveData<Boolean>()
    val fingerprintEnabled = MutableLiveData<Boolean>()

    val passcodeEnabled = SingleLiveEvent<Boolean>()
    val openEditPinEvent = SingleLiveEvent<Unit>()
    val openSetPinEvent = SingleLiveEvent<Unit>()
    val openUnlockPinEvent = SingleLiveEvent<Unit>()
    val showNoEnrolledFingerprints = SingleLiveEvent<Unit>()

    init {
        refreshPinState()
    }

    private fun refreshPinState() {
        val isPinSet = pinManager.isPinSet
        passcodeEnabled.value = isPinSet
        passcodeOptionsEnabled.value = isPinSet

        fingerprintVisible.value = systemInfoManager.hasFingerprintSensor
        fingerprintEnabled.value = appPreferences.isFingerprintEnabled
    }

    private fun clearPin() {
        pinManager.clear()
        appPreferences.isFingerprintEnabled = false
    }

    //region Public

    //region Results handle

    fun onPinForDisableUnlocked() {
        clearPin()
        refreshPinState()
    }

    fun onPinUnlockCancel() {
        refreshPinState()
    }

    fun onPinUpdated() {
        refreshPinState()
    }

    fun onPinUpdateCancel() {
        refreshPinState()
    }

    fun onEditPasscodeClick() {
        openEditPinEvent.call()
    }

    //endregion

    fun onPasscodeSwitch(isEnabled: Boolean) {
        if (isEnabled) {
            openSetPinEvent.call()
        } else {
            openUnlockPinEvent.call()
        }
        Log.d("ololo", "Passcode enabled $isEnabled")
    }

    fun onFingerprintSwitch(isEnabled: Boolean) {
        appPreferences.isFingerprintEnabled = isEnabled
    }

    //endregion
}