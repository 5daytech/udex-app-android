package com.blocksdecoded.dex.presentation.pin.cases.manage

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.data.security.IPinManager
import com.blocksdecoded.dex.presentation.pin.cases.IPinView
import com.blocksdecoded.dex.presentation.pin.PinPage
import com.blocksdecoded.dex.presentation.pin.cases.BasePinUseCase

class EditPinUseCase(
    view: IPinView,
    pinManager: IPinManager
) : BasePinUseCase(view, pinManager, pages = listOf(
    Page.UNLOCK,
    Page.ENTER,
    Page.CONFIRM
)) {

    override fun viewDidLoad() {
        view.setTitle(R.string.passcode_edit)

        val pinPages = mutableListOf<PinPage>()
        pages.forEach { page ->
            when (page) {
                Page.UNLOCK -> pinPages.add(PinPage(R.string.passcode_current_info))
                Page.ENTER -> pinPages.add(PinPage(R.string.passcode_new_info))
                Page.CONFIRM -> pinPages.add(PinPage(R.string.passcode_confirm_info))
            }
        }

        view.setPages(pinPages)
        view.showBackButton()
    }

    override fun onBackPressed() {
        view.dismissWithCancel()
    }

    override fun didSavePin() {
        view.showSuccess(R.string.passcode_success)
        view.dismissWithSuccess()
    }
}