package com.fridaytech.dex.presentation.pin.cases.manage

import com.fridaytech.dex.R
import com.fridaytech.dex.data.security.IPinManager
import com.fridaytech.dex.presentation.pin.PinPage
import com.fridaytech.dex.presentation.pin.cases.BasePinUseCase
import com.fridaytech.dex.presentation.pin.cases.IPinView

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
