package com.blocksdecoded.dex.presentation.pin

import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.pin.PinInteractionType.*
import com.blocksdecoded.dex.presentation.pin.cases.*
import com.blocksdecoded.dex.presentation.pin.cases.manage.EditPinUseCase
import com.blocksdecoded.dex.presentation.pin.cases.manage.SetPinUseCase
import com.blocksdecoded.dex.presentation.pin.cases.unlock.UnlockPinUseCase

class PinViewModel : CoreViewModel(), IPinView {
    private val pinManager = App.pinManager

    val titleLiveData = MutableLiveData<Int>()
    val pagesLiveData = MutableLiveData<List<PinPage>>()
    val showBackButton = MutableLiveData<Boolean>()
    val showPageAtIndex = MutableLiveData<Int>()
    val showError = MutableLiveData<Int>()
    val showSuccess = MutableLiveData<Int>()
    val showErrorForPage = MutableLiveData<Pair<Int, Int>>()
    val fillPinCircles = MutableLiveData<Pair<Int, Int>>()

    val hideToolbar = SingleLiveEvent<Unit>()
    val showFingerprintInputEvent = SingleLiveEvent<FingerprintManagerCompat.CryptoObject>()
    val resetCirclesWithShakeAndDelayForPageEvent = SingleLiveEvent<Int>()

    val navigateToMain = SingleLiveEvent<Unit>()
    val dismissWithCancelEvent = SingleLiveEvent<Unit>()
    val dismissWithSuccessEvent = SingleLiveEvent<Unit>()
    val closeApplicationEvent = SingleLiveEvent<Unit>()

    private var useCase: IBasePinUseCase? = null

    fun init(interactionType: PinInteractionType, showCancel: Boolean) {
        useCase = when(interactionType) {
            SET_PIN -> SetPinUseCase(this, pinManager)
            UNLOCK -> UnlockPinUseCase(
                this,
                pinManager,
                App.lockManager,
                App.appPreferences,
                App.encryptionManager,
                App.systemInfoManager,
                showCancel
            )
            EDIT_PIN -> EditPinUseCase(
                this,
                pinManager
            )
        }

        useCase?.viewDidLoad()
    }

    //region IPinView

    override fun setTitle(title: Int) {
        titleLiveData.value = title
    }
    override fun setPages(pages: List<PinPage>) {
        pagesLiveData.value = pages
    }
    override fun showPage(pageIndex: Int) {
        showPageAtIndex.value = pageIndex
    }

    override fun showErrorForPage(pageIndex: Int, error: Int) {
        showErrorForPage.value = pageIndex to error
    }

    override fun showError(error: Int) {
        showError.value = error
    }

    override fun showPinWrong(pageIndex: Int) {
        resetCirclesWithShakeAndDelayForPageEvent.value = pageIndex
    }

    override fun showBackButton() {
        showBackButton.postValue(true)
    }

    override fun fillCircles(pageIndex: Int, length: Int) {
        fillPinCircles.value = pageIndex to length
    }

    override fun hideToolbar() {
        hideToolbar.call()
    }

    override fun showFingerprintDialog(cryptoObject: FingerprintManagerCompat.CryptoObject) {
        showFingerprintInputEvent.value = cryptoObject
    }

    override fun showSuccess(message: Int) {
        showSuccess.value = message
    }

    //endregion

    //region Navigation

    override fun dismissWithSuccess() {
        dismissWithSuccessEvent.call()
    }

    override fun dismissWithCancel() {
        dismissWithCancelEvent.call()
    }

    override fun closeApplication() {
        closeApplicationEvent.call()
    }

    override fun backToMain() {
        navigateToMain.call()
    }

    //endregion

    //region Public

    fun onBackPressed() {
        useCase?.onBackPressed()
    }

    fun onNumberEnter(page: Int, number: String) {
        useCase?.onEnter(page, number)
    }

    fun onDeleteClick(page: Int) {
        useCase?.onDelete(page)
    }

    fun resetPin() {
        useCase?.resetPin()
    }

    fun onBiometricUnlockClick() {
        useCase?.onBiometricClick()
    }

    fun onBiometricUnlock() {
        useCase?.onBiometricUnlocked()
    }

    //endregion
}