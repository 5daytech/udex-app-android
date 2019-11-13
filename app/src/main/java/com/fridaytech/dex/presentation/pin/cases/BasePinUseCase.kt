package com.fridaytech.dex.presentation.pin.cases

import com.fridaytech.dex.R
import com.fridaytech.dex.data.security.IPinManager

abstract class BasePinUseCase(
    var view: IPinView,
    private val pinManager: IPinManager,
    val pages: List<Page>
) : IBasePinUseCase {
    enum class Page { UNLOCK, ENTER, CONFIRM }

    private var enteredPin = ""
    private var storedPin: String? = null

    abstract fun didSavePin()

    override fun onBiometricClick() = Unit
    override fun onBiometricUnlocked() = Unit

    override fun onEnter(page: Int, number: String) {
        if (enteredPin.length < IPinView.PIN_COUNT) {
            enteredPin += number
            view.fillCircles(enteredPin.length, page)

            if (enteredPin.length == IPinView.PIN_COUNT) {
                navigateToPage(page, enteredPin)
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

    fun navigateToPage(pageIndex: Int, pin: String) {
        when (pages[pageIndex]) {
            Page.UNLOCK -> onEnterFromUnlock(pin)
            Page.ENTER -> onEnterFromEnterPage(pin)
            Page.CONFIRM -> onEnterFromConfirmPage(pin)
        }
    }

    fun didFailToSavePin() {
        showEnterPage()
        view.showError(R.string.passcode_error_failed_to_save_pin)
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
            didSavePin()
        } else {
            showEnterPage()
            show(R.string.passcode_error_pins_dont_match, page = Page.ENTER)
        }
    }
}
