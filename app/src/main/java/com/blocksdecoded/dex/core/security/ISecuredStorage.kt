package com.blocksdecoded.dex.core.security

import com.blocksdecoded.dex.core.model.AuthData

interface ISecuredStorage {
    val authData: AuthData?
    val savedPin: String?

    fun saveAuthData(authData: AuthData)

    fun noAuthData(): Boolean

    fun savePin(pin: String)

    fun pinIsEmpty(): Boolean
}