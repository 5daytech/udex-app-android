package com.blocksdecoded.dex.presentation.pin

import androidx.biometric.BiometricPrompt
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
    val showFingerprintInputEvent = SingleLiveEvent<BiometricPrompt.CryptoObject>()
    val resetCirclesWithShakeAndDelayForPageEvent = SingleLiveEvent<Int>()

    val navigateToMain = SingleLiveEvent<Unit>()
    val dismissWithCancelEvent = SingleLiveEvent<Unit>()
    val dismissWithSuccessEvent = SingleLiveEvent<Unit>()
    val closeApplicationEvent = SingleLiveEvent<Unit>()

    private var useCase: IBasePinUseCase? = null

    fun init(interactionType: PinInteractionType, showCancel: Boolean) {
        useCase = when (interactionType) {
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
        titleLiveData.postValue(title)
    }
    override fun setPages(pages: List<PinPage>) {
        pagesLiveData.postValue(pages)
    }
    override fun showPage(pageIndex: Int) {
        showPageAtIndex.postValue(pageIndex)
    }

    override fun showErrorForPage(pageIndex: Int, error: Int) {
        showErrorForPage.postValue(pageIndex to error)
    }

    override fun showError(error: Int) {
        showError.postValue(error)
    }

    override fun showPinWrong(pageIndex: Int) {
        resetCirclesWithShakeAndDelayForPageEvent.postValue(pageIndex)
    }

    override fun showBackButton() {
        showBackButton.postValue(true)
    }

    override fun fillCircles(pageIndex: Int, length: Int) {
        fillPinCircles.postValue(pageIndex to length)
    }

    override fun hideToolbar() {
        hideToolbar.postValue(Unit)
    }

    override fun showFingerprintDialog(cryptoObject: BiometricPrompt.CryptoObject) {
        showFingerprintInputEvent.postValue(cryptoObject)
    }

    override fun showSuccess(message: Int) {
        showSuccess.value = message
    }

    //endregion

    //region Navigation

    override fun dismissWithSuccess() {
        dismissWithSuccessEvent.postValue(Unit)
    }

    override fun dismissWithCancel() {
        dismissWithCancelEvent.postValue(Unit)
    }

    override fun closeApplication() {
        closeApplicationEvent.postValue(Unit)
    }

    override fun backToMain() {
        navigateToMain.postValue(Unit)
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
