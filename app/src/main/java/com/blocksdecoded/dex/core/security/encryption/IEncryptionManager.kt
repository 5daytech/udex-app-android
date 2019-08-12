package com.blocksdecoded.dex.core.security.encryption

import androidx.core.hardware.fingerprint.FingerprintManagerCompat

interface IEncryptionManager {
	fun encrypt(data: String): String
	fun decrypt(data: String): String
	fun getCryptoObject(): FingerprintManagerCompat.CryptoObject?
}