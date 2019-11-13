package com.fridaytech.dex.data.security

import android.text.TextUtils
import com.fridaytech.dex.core.model.AuthData
import com.fridaytech.dex.core.shared.ISharedStorage
import com.fridaytech.dex.data.security.encryption.IEncryptionManager

class SecuredStorage(
    private val encryptionManager: IEncryptionManager,
    private val sharedStorage: ISharedStorage
) : ISecuredStorage {
    private val AUTH_DATA = "auth_data"
    private val LOCK_PIN = "lock_pin"

    override val authData: AuthData?
        get() {
            if (sharedStorage.containsPreference(AUTH_DATA)) {
                val data = sharedStorage.getPreference(AUTH_DATA, "")

                if (data.isNotEmpty()) {
                    return AuthData(encryptionManager.decrypt(data))
                }
            }

            return null
        }

    override fun saveAuthData(authData: AuthData) {
        sharedStorage.setPreference(AUTH_DATA, encryptionManager.encrypt(authData.toString()))
    }

    override fun noAuthData(): Boolean = sharedStorage.getPreference(AUTH_DATA, "").isEmpty()

    override val savedPin: String?
        get() {
            val pin = sharedStorage.getPreference(LOCK_PIN, "")

            return if (TextUtils.isEmpty(pin)) {
                null
            } else {
                encryptionManager.decrypt(pin)
            }
        }

    override fun savePin(pin: String) {
        sharedStorage.setPreference(LOCK_PIN, encryptionManager.encrypt(pin))
    }

    override fun removePin() {
        sharedStorage.removePreference(LOCK_PIN)
    }

    override fun pinIsEmpty(): Boolean {
        if (sharedStorage.containsPreference(LOCK_PIN)) {
            val string = sharedStorage.getPreference(LOCK_PIN, "")
            return string.isEmpty()
        }

        return true
    }
}
