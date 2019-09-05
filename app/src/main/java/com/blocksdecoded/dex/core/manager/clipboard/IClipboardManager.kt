package com.blocksdecoded.dex.core.manager.clipboard

interface IClipboardManager {
    fun copyText(text: String)
    fun getCopiedText(): String
    val hasPrimaryClip: Boolean
}