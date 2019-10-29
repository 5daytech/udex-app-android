package com.blocksdecoded.dex.data.security.encryption

import androidx.biometric.BiometricPrompt

interface IEncryptionManager {
	fun encrypt(data: String): String
	fun decrypt(data: String): String
	fun getCryptoObject(): BiometricPrompt.CryptoObject?
}