package com.blocksdecoded.dex.core.security

import com.blocksdecoded.dex.core.model.AuthData

class SecuredStorage: ISecuredStorage {
    override val authData: AuthData?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val savedPin: String?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun saveAuthData(authData: AuthData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun noAuthData(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun savePin(pin: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pinIsEmpty(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}