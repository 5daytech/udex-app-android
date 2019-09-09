package com.blocksdecoded.dex.presentation.pin.cases

import com.blocksdecoded.dex.core.security.IPinManager

class SetPinUseCase(
    view: IPinView,
    pinManager: IPinManager
) : BasePinUseCase(view, pinManager, pages = listOf(Page.ENTER, Page.CONFIRM)) {
    override fun viewDidLoad() {

    }
}