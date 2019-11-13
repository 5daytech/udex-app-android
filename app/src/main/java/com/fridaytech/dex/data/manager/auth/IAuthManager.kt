package com.fridaytech.dex.data.manager.auth

import android.security.keystore.UserNotAuthenticatedException
import com.fridaytech.dex.core.model.AuthData
import com.fridaytech.dex.data.manager.IAdapterManager
import com.fridaytech.dex.data.zrx.IRelayerAdapterManager
import io.reactivex.subjects.PublishSubject

interface IAuthManager {
    var adapterManager: IAdapterManager?
    var relayerAdapterManager: IRelayerAdapterManager?
    var authData: AuthData?
    val isLoggedIn: Boolean

    var authDataSubject: PublishSubject<Unit>

    @Throws(UserNotAuthenticatedException::class)
    fun safeLoad()

    @Throws(UserNotAuthenticatedException::class)
    fun login(words: List<String>)

    fun logout()
}
