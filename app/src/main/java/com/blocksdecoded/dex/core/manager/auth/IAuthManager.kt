package com.blocksdecoded.dex.core.manager.auth

import android.security.keystore.UserNotAuthenticatedException
import com.blocksdecoded.dex.core.manager.IAdapterManager
import com.blocksdecoded.dex.core.manager.zrx.IRelayerAdapterManager
import com.blocksdecoded.dex.core.model.AuthData
import io.reactivex.subjects.PublishSubject

interface IAuthManager {
    var adapterManager: IAdapterManager?
    var relayerAdapterManager: IRelayerAdapterManager?
    var authData: AuthData?
    val isLoggedIn: Boolean

    var authDataSignal: PublishSubject<Unit>

    @Throws(UserNotAuthenticatedException::class)
    fun safeLoad()

    @Throws(UserNotAuthenticatedException::class)
    fun login(words: List<String>)

    fun logout()
}