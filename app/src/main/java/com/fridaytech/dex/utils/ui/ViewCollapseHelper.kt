package com.fridaytech.dex.utils.ui

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import com.fridaytech.dex.utils.listeners.SimpleAnimationListener
import com.fridaytech.dex.utils.visible

class ViewCollapseHelper(
    val view: View
) {
    private var measuredHeight: Int = 0

    fun expand(speed: Float = 1f) {
        if (measuredHeight == 0) {
            view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            measuredHeight = view.measuredHeight
        }

        val targetHeight = measuredHeight

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                view.alpha = 0.3f
                view.layoutParams.height = if (interpolatedTime == 1f) targetHeight else (targetHeight * interpolatedTime).toInt()
                view.visibility = View.VISIBLE
                view.requestLayout()
                if (interpolatedTime > 0.3f) {
                    view.alpha = interpolatedTime
                }
            }

            override fun willChangeBounds(): Boolean = true
        }

        a.duration = ((((targetHeight / view.context.resources.displayMetrics.density)) * 2) / speed).toLong()
        view.startAnimation(a)
    }

    fun collapse() {
        measuredHeight = view.measuredHeight
        val initialHeight = measuredHeight

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    view.visibility = View.GONE
                } else {
                    view.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    view.requestLayout()
                    if (interpolatedTime > 0.3f) {
                        view.alpha = (1 - interpolatedTime) * 2
                    }
                }
            }

            override fun willChangeBounds(): Boolean = true
        }

        a.setAnimationListener(object : SimpleAnimationListener() {
            override fun onAnimationStart(animation: Animation?) {
                super.onAnimationStart(animation)
                view.postDelayed({
                    view.visible = false
                }, a.duration)
            }
        })

        a.duration = (((initialHeight / view.context.resources.displayMetrics.density)) * 2).toLong()
        view.startAnimation(a)
    }
}
