package com.blocksdecoded.dex.core.security.encryption

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.security.keystore.KeyPermanentlyInvalidatedException
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import javax.crypto.Cipher
import javax.crypto.SecretKey

class EncryptionManager : IEncryptionManager {
    
    private val keyStoreWrapper = KeyStoreWrapper()
    
    @Synchronized
    override fun encrypt(data: String): String {
        var masterKey = keyStoreWrapper.getAndroidKeyStoreSymmetricKey(MASTER_KEY)

        if (masterKey == null) {
            masterKey = keyStoreWrapper.createAndroidKeyStoreSymmetricKey(MASTER_KEY)
        }
        return CipherWrapper().encrypt(data, masterKey)
    }

    @Synchronized
    override fun decrypt(data: String): String {
        val masterKey = keyStoreWrapper.getAndroidKeyStoreSymmetricKey(MASTER_KEY)
                ?: throw KeyPermanentlyInvalidatedException()
        return CipherWrapper().decrypt(data, masterKey)
    }


    override fun getCryptoObject(): FingerprintManagerCompat.CryptoObject {
        var masterKey = keyStoreWrapper.getAndroidKeyStoreSymmetricKey(MASTER_KEY)

        if (masterKey == null) {
            masterKey = keyStoreWrapper.createAndroidKeyStoreSymmetricKey(MASTER_KEY)
        }

        val cipher = CipherWrapper().cipher
        cipher.init(Cipher.ENCRYPT_MODE, masterKey)

        return FingerprintManagerCompat.CryptoObject(cipher)
    }

    companion object {
        const val MASTER_KEY = "MASTER_KEY"

        fun isDeviceLockEnabled(ctx: Context): Boolean {
            val keyguardManager = ctx.getSystemService(Activity.KEYGUARD_SERVICE) as KeyguardManager
            return keyguardManager.isKeyguardSecure
        }

    }

}
