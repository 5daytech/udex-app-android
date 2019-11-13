package com.fridaytech.dex.utils.ui

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import com.fridaytech.dex.utils.listeners.SimpleAnimationListener
import com.fridaytech.dex.utils.visible

object AnimationHelper {

    const val VERY_FAST_ANIMATION = 150L
    const val FAST_ANIMATION = 200L

    fun rotate(v: View) {
        v.post {
            v.clearAnimation()
            v.rotation = 0f
            ObjectAnimator.ofFloat(v, View.ROTATION, 0f, -180f)
                .apply {
                    duration = FAST_ANIMATION
                    interpolator = LinearInterpolator()
                }
                .start()
        }
    }

    fun expand(v: View, speed: Float = 1f) {
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        val targetHeight = v.measuredHeight

        v.alpha = 0.3f
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.visibility = View.VISIBLE
                v.layoutParams.height = if (interpolatedTime == 1f) targetHeight else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
                if (interpolatedTime > 0.3f) {
                    v.alpha = interpolatedTime
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = ((((targetHeight / v.context.resources.displayMetrics.density)) * 2) / speed).toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                    if (interpolatedTime > 0.3f) {
                        v.alpha = (1 - interpolatedTime) * 2
                    }
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        a.setAnimationListener(object : SimpleAnimationListener() {
            override fun onAnimationStart(animation: Animation?) {
                super.onAnimationStart(animation)
                v.postDelayed({
                    v.visible = false
                }, a.duration)
            }
        })

        // 1dp/ms
        a.duration = (((initialHeight / v.context.resources.displayMetrics.density)) * 2).toLong()
        v.startAnimation(a)
    }
}
