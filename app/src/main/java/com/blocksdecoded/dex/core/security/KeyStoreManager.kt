package com.blocksdecoded.dex.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import com.blocksdecoded.dex.core.security.encryption.CipherWrapper
import com.blocksdecoded.dex.utils.Logger
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.UnrecoverableKeyException
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class KeyStoreManager(private val keyAlias: String) : IKeyStoreManager, IKeyProvider {
    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"

        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7

        private const val AUTH_DURATION_SEC = 86400 //24 hours in seconds (24x60x60)
    }

    override val transformationSymmetric: String = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    private val keyStore: KeyStore

    private val keyGenerator: KeyGenerator
        get() = KeyGenerator.getInstance(ALGORITHM, ANDROID_KEY_STORE).apply {

            init(KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .setUserAuthenticationValidityDurationSeconds(AUTH_DURATION_SEC)
                .setUserAuthenticationRequired(true)
                .setRandomizedEncryptionRequired(false)
                .build())
        }

    init {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
    }

    override val isKeyInvalidated: Boolean
        get() = try {
            validateKey()
            false
        } catch (e: Exception) {
            Logger.e(e)

            recreateKeyOnInvalid(e) || e is KeyPermanentlyInvalidatedException ||
                    e is UnrecoverableKeyException
        }

    override val isUserNotAuthenticated: Boolean
        get() = try {
            validateKey()
            false
        } catch (e: Exception) {
            Logger.e(e)
            recreateKeyOnInvalid(e)
            e is UserNotAuthenticatedException
        }

    private fun recreateKeyOnInvalid(e: Exception): Boolean {
        if (e is InvalidKeyException) {
            Logger.d("Remove key")
            removeKey()
            createKey()
        }

        return e is InvalidKeyException
    }

    override fun createKey(): SecretKey = keyGenerator.generateKey()

    override fun getKey(): SecretKey {
        val key = keyStore.getKey(keyAlias, null) ?: createKey()
        return key as SecretKey
    }

    override fun removeKey() {
        try {
            keyStore.deleteEntry(keyAlias)
        } catch (e: KeyStoreException) {
            Logger.e(e)
        }
    }

    private fun validateKey() {
        CipherWrapper(transformationSymmetric).encrypt("abc", getKey())
    }

}
