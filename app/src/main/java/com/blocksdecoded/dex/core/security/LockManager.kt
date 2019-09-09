package com.blocksdecoded.dex.core.security

import android.app.Activity
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.manager.BackgroundManager
import com.blocksdecoded.dex.utils.TimeUtils
import io.reactivex.subjects.PublishSubject
import java.util.*

class LockManager(private val pinManager: IPinManager) : ILockManager, BackgroundManager.Listener {

    private val lockTimeout: Double = 30.0

    override val lockStateUpdatedSignal: PublishSubject<Unit> = PublishSubject.create()

    override var isLocked: Boolean = false
        set(value) {
            field = value
            lockStateUpdatedSignal.onNext(Unit)
        }

    override fun didEnterBackground() {
        if (isLocked) {
            return
        }

        App.lastExitDate = Date().time
    }

    override fun willEnterForeground(activity: Activity) {
        if (isLocked || !pinManager.isPinSet) {
            return
        }

        val secondsAgo = TimeUtils.getSecondsAgo(App.lastExitDate)
        if (secondsAgo > lockTimeout) {
            isLocked = true
        }
    }

    override fun onUnlock() {
        isLocked = false
    }
}
