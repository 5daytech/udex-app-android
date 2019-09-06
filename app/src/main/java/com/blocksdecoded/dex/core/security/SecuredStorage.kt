package com.blocksdecoded.dex.core.security

import android.text.TextUtils
import com.blocksdecoded.dex.core.model.AuthData
import com.blocksdecoded.dex.core.security.encryption.IEncryptionManager
import com.blocksdecoded.dex.core.shared.ISharedStorage

class SecuredStorage(
    private val encryptionManager: IEncryptionManager,
    private val sharedStorage: ISharedStorage
): ISecuredStorage {
    private val AUTH_DATA = "auth_data"
    private val LOCK_PIN = "lock_pin"
    
    override val authData: AuthData?
        get() {
            if (sharedStorage.containsPreference(AUTH_DATA)) {
                val data = sharedStorage.getPreference(AUTH_DATA, "")
                
                if (data.isNotEmpty()) {
//                    return AuthData(encryptionManager.decrypt(data))
                    return AuthData(data)
                }
            }
            
            return null
        }
    
    override fun saveAuthData(authData: AuthData) {
//        sharedStorage.setPreference(AUTH_DATA, encryptionManager.encrypt(authData.toString()))
        sharedStorage.setPreference(AUTH_DATA, authData.toString())
    }
    
    override fun noAuthData(): Boolean = sharedStorage.getPreference(AUTH_DATA, "").isEmpty()
    
    override val savedPin: String?
        get() {
            val pin = sharedStorage.getPreference(LOCK_PIN, "")
            
            return if (TextUtils.isEmpty(pin)) {
                null
            } else {
//                encryptionManager.decrypt(pin)
                pin
            }
        }
    
    override fun savePin(pin: String) {
//        sharedStorage.setPreference(LOCK_PIN, encryptionManager.encrypt(pin))
        sharedStorage.setPreference(LOCK_PIN, pin)
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