package com.fridaytech.dex.data.security

import com.fridaytech.dex.core.model.AuthData

interface ISecuredStorage {
    val authData: AuthData?
    val savedPin: String?

    fun saveAuthData(authData: AuthData)

    fun noAuthData(): Boolean

    fun savePin(pin: String)

    fun pinIsEmpty(): Boolean

    fun removePin()
}
