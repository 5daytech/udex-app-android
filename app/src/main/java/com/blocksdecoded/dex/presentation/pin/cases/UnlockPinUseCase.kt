package com.blocksdecoded.dex.presentation.pin.cases

import com.blocksdecoded.dex.core.security.IPinManager

class UnlockPinUseCase(
    view: IPinView,
    pinManager: IPinManager,
    val showCancel: Boolean
) : BasePinUseCase(view, pinManager, pages = listOf(Page.UNLOCK)) {
    override fun viewDidLoad() {

    }
}