package com.blocksdecoded.dex.core.manager

import android.security.keystore.UserNotAuthenticatedException
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.adapter.Erc20Adapter
import com.blocksdecoded.dex.core.adapter.EthereumAdapter
import com.blocksdecoded.dex.core.model.AuthData
import com.blocksdecoded.dex.core.security.ISecuredStorage
import io.reactivex.subjects.PublishSubject

class AuthManager(
    private val securedStorage: ISecuredStorage
) {
    var adapterManager: IAdapterManager? = null

    var authData: AuthData? = null
        get() = securedStorage.authData//TODO: Load via safeLoad
    
    var authDataSignal = PublishSubject.create<Unit>()

    val isLoggedIn: Boolean
        get() = !securedStorage.noAuthData()

    @Throws(UserNotAuthenticatedException::class)
    fun safeLoad() {
        authData = securedStorage.authData
        authDataSignal.onNext(Unit)
    }

    @Throws(UserNotAuthenticatedException::class)
    fun login(words: List<String>) {
        AuthData(words).let {
            securedStorage.saveAuthData(it)
            authData = it
            authDataSignal.onNext(Unit)
        }
    }

    fun logout() {
        adapterManager?.stopKits()

        EthereumAdapter.clear(App.instance)
        Erc20Adapter.clear(App.instance)

        authData = null
    }
}