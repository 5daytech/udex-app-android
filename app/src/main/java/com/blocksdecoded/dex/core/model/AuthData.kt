package com.blocksdecoded.dex.core.model

import io.horizontalsystems.ethereumkit.core.hexStringToByteArray
import io.horizontalsystems.ethereumkit.core.toHexString
import io.horizontalsystems.hdwalletkit.HDWallet
import io.horizontalsystems.hdwalletkit.Mnemonic
import java.math.BigInteger

class AuthData {
    var words: List<String> = listOf()
    var walletId: String = ""
    var seed: ByteArray = byteArrayOf()
    var privateKey: BigInteger = BigInteger.ZERO
    private var version = 2

    private val wordsSeparator = " "
    private val partsSeparator = "|"

    constructor(words: List<String>, walletId: String) {
        this.words = words
        this.walletId = walletId
        this.seed = Mnemonic().toSeed(words)

        val hdWallet = HDWallet(seed, 60)
        privateKey = hdWallet.privateKey(0, 0, true).privKey
    }

    constructor(serialized: String) {
        if (!serialized.contains(partsSeparator)) {
            val wordsAndWalletId = serialized.split(wordsSeparator)
            version = 1
            words = wordsAndWalletId.subList(0, 12)
            wordsAndWalletId.getOrNull(12)?.let { walletId = it }
            seed = Mnemonic().toSeed(words)
        } else {
            val (version, wordsString, walletId, seedString) = serialized.split(partsSeparator)

            this.version = version.toInt()
            this.words = wordsString.split(wordsSeparator)
            this.walletId = walletId
            this.seed = seedString.hexStringToByteArray()
        }
    }

    override fun toString(): String {
        return listOf(version, words.joinToString(wordsSeparator), walletId, seed.toHexString()).joinToString(partsSeparator)
    }
}