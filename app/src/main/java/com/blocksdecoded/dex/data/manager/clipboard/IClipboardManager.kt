package com.blocksdecoded.dex.data.manager.clipboard

interface IClipboardManager {
    fun copyText(text: String)
    fun getCopiedText(): String
    val hasPrimaryClip: Boolean
}
