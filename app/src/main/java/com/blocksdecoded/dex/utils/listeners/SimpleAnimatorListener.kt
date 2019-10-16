package com.blocksdecoded.dex.utils.listeners

import android.animation.Animator

abstract class SimpleAnimatorListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) { }

    override fun onAnimationEnd(animation: Animator?) { }

    override fun onAnimationCancel(animation: Animator?) { }

    override fun onAnimationStart(animation: Animator?) { }
}