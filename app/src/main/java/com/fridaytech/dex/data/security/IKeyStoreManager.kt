package com.fridaytech.dex.data.security

import javax.crypto.SecretKey

interface IKeyStoreManager {
    val isKeyInvalidated: Boolean
    val isUserNotAuthenticated: Boolean

    fun createKey(): SecretKey
    fun getKey(): SecretKey
    fun removeKey()
}

interface IKeyProvider {
    val transformationSymmetric: String

    fun getKey(): SecretKey
}
