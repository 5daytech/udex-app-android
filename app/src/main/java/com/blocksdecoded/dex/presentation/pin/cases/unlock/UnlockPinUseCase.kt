package com.blocksdecoded.dex.presentation.pin.cases.unlock

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.manager.system.ISystemInfoManager
import com.blocksdecoded.dex.core.security.ILockManager
import com.blocksdecoded.dex.core.security.IPinManager
import com.blocksdecoded.dex.core.security.encryption.IEncryptionManager
import com.blocksdecoded.dex.core.shared.IAppPreferences
import com.blocksdecoded.dex.presentation.pin.PinPage
import com.blocksdecoded.dex.presentation.pin.cases.BasePinUseCase
import com.blocksdecoded.dex.presentation.pin.cases.IBasePinUseCase
import com.blocksdecoded.dex.presentation.pin.cases.IPinView

class UnlockPinUseCase(
    private val view: IPinView,
    private val pinManager: IPinManager,
    private val lockManager: ILockManager,
    private val appPreferences: IAppPreferences,
    private val encryptionManager: IEncryptionManager,
    private val systemInfoManager: ISystemInfoManager,
    private val showCancel: Boolean
) : IBasePinUseCase {

    private val unlockPageIndex = 0
    private var enteredPin = ""

    private fun unlock(pin: String): Boolean {
        val valid = pinManager.validate(pin)

        if (valid) {
            lockManager.onUnlock()
        }

        return valid
    }

    override fun viewDidLoad() {
        view.setPages(listOf(PinPage(R.string.passcode_title)))

        if (showCancel) {
            view.showBackButton()
        } else {
            view.hideToolbar()
        }

        onBiometricClick()
    }

    override fun onEnter(page: Int, number: String) {
        if (enteredPin.length < IPinView.PIN_COUNT) {
            enteredPin += number
            view.fillCircles(enteredPin.length, page)

            if (enteredPin.length == IPinView.PIN_COUNT) {
                if (unlock(enteredPin)) {
                    view.dismissWithSuccess()
                } else {
                    view.showPinWrong(unlockPageIndex)
                }
                enteredPin = ""
            }
        }
    }

    override fun onDelete(page: Int) {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.substring(0, enteredPin.length - 1)
            view.fillCircles(enteredPin.length, page)
        }
    }

    override fun resetPin() {
        enteredPin = ""
    }

    override fun onBiometricUnlocked() {
        lockManager.onUnlock()
        view.dismissWithSuccess()
    }

    override fun onBiometricClick() {
        if (appPreferences.isFingerprintEnabled && systemInfoManager.hasEnrolledFingerprints) {
            encryptionManager.getCryptoObject()?.let { view.showFingerprintDialog(it) }
        }
    }

    override fun onBackPressed() {
        if (showCancel) {
            view.dismissWithCancel()
        } else {
            view.closeApplication()
        }
    }
}