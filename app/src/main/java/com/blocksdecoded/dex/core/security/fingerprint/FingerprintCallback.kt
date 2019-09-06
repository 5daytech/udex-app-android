package com.blocksdecoded.dex.core.security.fingerprint

interface FingerprintCallback {
    fun onAuthenticated()

    fun onAuthenticationHelp(helpString: CharSequence?)

    fun onAuthenticationFailed()

    fun onAuthenticationError(errMsgId: Int, errString: CharSequence?)
}
