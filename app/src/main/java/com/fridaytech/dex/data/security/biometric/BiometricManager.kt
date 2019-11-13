package com.fridaytech.dex.data.security.biometric

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executors

class BiometricManager(
    activity: FragmentActivity,
    private val listener: IBiometricListener
) {
    private val biometric: BiometricPrompt
    private val defaultPromptInfo: BiometricPrompt.PromptInfo
        get() = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Verify your identity")
            .setDescription("Use your biometric to verify identity")
            .setNegativeButtonText("Cancel")
            .build()

    init {
        val executor = Executors.newSingleThreadExecutor()

        biometric = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)

                val type = when (errorCode) {
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> ErrorType.CANCEL
                    BiometricPrompt.ERROR_LOCKOUT, BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> ErrorType.LOCKOUT
                    else -> ErrorType.UNKNOWN
                }

                listener.onAuthFail(type)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                listener.onAuthSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                listener.onAuthFail(ErrorType.UNKNOWN)
            }
        })
    }

    fun cancel() {
        biometric.cancelAuthentication()
    }

    fun request(
        promptInfo: BiometricPrompt.PromptInfo = defaultPromptInfo,
        cryptoObject: BiometricPrompt.CryptoObject
    ) {
        biometric.authenticate(promptInfo)
//        biometric.authenticate(promptInfo, cryptoObject)
    }

    interface IBiometricListener {
        fun onAuthSuccess()

        fun onAuthFail(type: ErrorType)
    }

    enum class ErrorType {
        CANCEL,
        LOCKOUT,
        UNKNOWN
    }
}
