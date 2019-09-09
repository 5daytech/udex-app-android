package com.blocksdecoded.dex.presentation.pin.cases

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.security.IPinManager
import com.blocksdecoded.dex.presentation.pin.PinPage

class SetPinUseCase(
    view: IPinView,
    pinManager: IPinManager
) : BasePinUseCase(view, pinManager, pages = listOf(Page.ENTER, Page.CONFIRM)) {
    override fun viewDidLoad() {
        view.setTitle(R.string.passcode_title)

        val pinPages = mutableListOf<PinPage>()

        pages.forEach { page ->
            when (page) {
                Page.ENTER -> pinPages.add(PinPage(R.string.passcode_info))
                Page.CONFIRM -> pinPages.add(PinPage(R.string.passcode_confirm_info))
            }
        }

        view.setPages(pinPages)
    }

    override fun onBackPressed() {
        view.dismissWithCancel()
    }

    override fun didSavePin() {
        view.showSuccess(R.string.passcode_success)
        view.dismissWithSuccess()
    }
}