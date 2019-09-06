package com.blocksdecoded.dex.core.security

import javax.crypto.SecretKey

interface IKeyStoreManager {
    val isKeyInvalidated: Boolean
    val isUserNotAuthenticated: Boolean

    fun createKey(): SecretKey
    fun getKey(): SecretKey
    fun removeKey()
}

interface IKeyProvider {
    fun getKey(): SecretKey
}