package com.blocksdecoded.dex.presentation.pin.cases

import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.blocksdecoded.dex.presentation.pin.PinPage

interface IPinView {
    fun setTitle(title: Int)
    fun setPages(pages: List<PinPage>)
    fun showPage(pageIndex: Int)
    fun showErrorForPage(pageIndex: Int, error: Int)
    fun showError(error: Int)
    fun showPinWrong(pageIndex: Int)
    fun showBackButton()
    fun fillCircles(pageIndex: Int, length: Int)
    fun hideToolbar()
    fun showFingerprintDialog(cryptoObject: FingerprintManagerCompat.CryptoObject)

    fun dismissWithSuccess()
    fun dismissWithCancel()
    fun closeApplication()
    fun backToMain()

    companion object {
        const val PIN_COUNT = 6
    }
}