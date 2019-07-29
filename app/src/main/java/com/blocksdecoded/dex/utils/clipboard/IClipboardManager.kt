package com.blocksdecoded.dex.utils.clipboard

interface IClipboardManager {
    fun copyText(text: String)
    fun getCopiedText(): String
    val hasPrimaryClip: Boolean
}