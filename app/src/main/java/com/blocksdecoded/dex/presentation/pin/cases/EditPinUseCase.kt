package com.blocksdecoded.dex.presentation.pin.cases

import com.blocksdecoded.dex.core.security.IPinManager

class EditPinUseCase(
    view: IPinView,
    pinManager: IPinManager
) : BasePinUseCase(view, pinManager, pages = listOf(Page.UNLOCK, Page.ENTER, Page.CONFIRM)) {
    override fun viewDidLoad() {

    }

    override fun onBackPressed() {

    }

    override fun didSavePin() {

    }
}