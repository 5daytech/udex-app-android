package com.blocksdecoded.dex.presentation.pin.cases

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.security.IPinManager

abstract class BasePinUseCase(
    var view: IPinView,
    private val pinManager: IPinManager,
    val pages: List<Page>
) {
    enum class Page { UNLOCK, ENTER, CONFIRM }

    private var enteredPin = ""
    private var storedPin: String? = null

    abstract fun viewDidLoad()

    open fun onBiometricClick() {

    }

    fun onEnter(pageIndex: Int, number: String) {
        if (enteredPin.length < IPinView.PIN_COUNT) {
            enteredPin += number
            view.fillCircles(enteredPin.length, pageIndex)

            if (enteredPin.length == IPinView.PIN_COUNT) {
                navigateToPage(pageIndex, enteredPin)
                enteredPin = ""
            }
        }
    }

    fun onDelete(pageIndex: Int) {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.substring(0, enteredPin.length - 1)
            view.fillCircles(enteredPin.length, pageIndex)
        }
    }

    fun navigateToPage(pageIndex: Int, pin: String) {
        when (pages[pageIndex]) {
            Page.UNLOCK -> onEnterFromUnlock(pin)
            Page.ENTER -> onEnterFromEnterPage(pin)
            Page.CONFIRM -> onEnterFromConfirmPage(pin)
        }
    }

    fun resetPin() {
        enteredPin = ""
    }

    fun didSavePin() {

    }

    fun onBackPressed() {

    }

    fun didFailToSavePin() {
        showEnterPage()
        view.showError(R.string.error_failed_save_pin)
    }

    private fun show(page: Page) {
        val pageIndex = pages.indexOfFirst { it == page }
        if (pageIndex >= 0) {
            view.showPage(pageIndex)
        }
    }

    private fun show(error: Int, page: Page) {
        val pageIndex = pages.indexOfFirst { it == page }
        if (pageIndex >= 0) {
            view.showErrorForPage(error, pageIndex)
        }
    }

    private fun showEnterPage() {
        storedPin = null
        show(Page.ENTER)
    }

    private fun onEnterFromUnlock(pin: String) {
        if (pinManager.validate(pin)) {
            show(Page.ENTER)
        } else {
            val pageUnlockIndex = pages.indexOfFirst { it == Page.UNLOCK }
            if (pageUnlockIndex >= 0) {
                enteredPin = ""
                view.showPinWrong(pageUnlockIndex)
            }
        }
    }

    private fun onEnterFromEnterPage(pin: String) {
        storedPin = pin
        show(Page.CONFIRM)
    }

    private fun onEnterFromConfirmPage(pin: String) {
        if (storedPin == pin) {
            pinManager.store(pin)
        } else {
            showEnterPage()
            show(R.string.error_pins_not_match, page = Page.ENTER)
        }
    }
}