package com.blocksdecoded.dex.core.manager.auth

import com.blocksdecoded.dex.core.manager.IWordsManager
import com.blocksdecoded.dex.core.shared.IAppLocalStorage
import io.horizontalsystems.hdwalletkit.Mnemonic
import io.reactivex.subjects.PublishSubject

class WordsManager(private val localStorage: IAppLocalStorage) :
    IWordsManager {

    override var isBackedUp: Boolean
        get() = localStorage.isBackedUp
        set(value) {
            localStorage.isBackedUp = value
            backedUpSignal.onNext(Unit)
        }

    override var backedUpSignal = PublishSubject.create<Unit>()

    @Throws(Mnemonic.MnemonicException::class)
    override fun validate(words: List<String>) {
        Mnemonic().validate(words)
    }

    override fun generateWords() : List<String> = Mnemonic().generate()

}
