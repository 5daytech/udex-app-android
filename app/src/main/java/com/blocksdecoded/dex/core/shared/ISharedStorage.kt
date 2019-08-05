package com.blocksdecoded.dex.core.shared

interface ISharedStorage {
    fun <T> setPreference(key: String, value: T)

    fun <T> getPreference(key: String, defValue: T): T

    fun removePreference(key: String)

    fun containsPreference(key: String): Boolean
}