package com.blocksdecoded.dex.data.security

import io.reactivex.subjects.PublishSubject

interface IPinManager {
    val isPinSet: Boolean

    fun store(pin: String)
    fun validate(pin: String): Boolean
    fun clear()
}

interface ILockManager {
    val lockStateUpdatedSignal: PublishSubject<Unit>
    var isLocked: Boolean
    fun onUnlock()
}