package com.blocksdecoded.dex.core.security

interface IPinManager {
    val isPinSet: Boolean

    fun store(pin: String)
    fun validate(pin: String): Boolean
    fun clear()
}