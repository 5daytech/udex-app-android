package com.blocksdecoded.dex.presentation.pin.cases

interface IBasePinUseCase {
    fun viewDidLoad()

    fun onBackPressed()

    fun onEnter(page: Int, number: String)

    fun onDelete(page: Int)

    fun resetPin()

    fun onBiometricUnlocked()

    fun onBiometricClick()
}