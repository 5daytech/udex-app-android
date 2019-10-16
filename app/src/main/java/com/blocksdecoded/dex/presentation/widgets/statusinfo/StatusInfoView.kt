package com.blocksdecoded.dex.presentation.widgets.statusinfo

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.utils.getColorRes
import com.blocksdecoded.dex.utils.isTranslucentStatus
import com.blocksdecoded.dex.utils.listeners.SimpleAnimatorListener
import com.blocksdecoded.dex.utils.statusBarHeight
import kotlin.math.absoluteValue

@SuppressLint("ViewConstructor")
class StatusInfoView (
    context: Context,
    stringText: String?,
    @ColorRes alertColorRes: Int,
    @StringRes textRes: Int?
) : LinearLayout(context, null,0) {
    private val activity: Activity
        get() = context as Activity

    var statusBarColor: Int = 0
    var isStatusBarTranslucent: Boolean = false
    private var textView: TextView? = null

    init {
        this.observeLifecycle(activity)
        this.initView(stringText, alertColorRes, textRes)
    }

    private fun observeLifecycle(any: Context) {
        if(any is AppCompatActivity) {
            any.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    hide(any, Runnable{})
                    any.lifecycle.removeObserver(this)
                }
            })
        }
    }

    private fun initView(
        text: String?,
        @ColorRes alertColorRes: Int,
        @StringRes textRes: Int?
    ) {
        this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, activity.statusBarHeight)
        this.gravity = Gravity.CENTER_HORIZONTAL

        val decor = activity.window.decorView as ViewGroup

        if (alertColorRes.absoluteValue > 0) {
            setBackgroundColor(ContextCompat.getColor(context, alertColorRes))
        } else {
            setBackgroundResource(android.R.color.transparent)
        }

        val childContainer = LinearLayout(context)
        childContainer.orientation = HORIZONTAL
        childContainer.gravity = Gravity.CENTER_VERTICAL
        childContainer.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        textView = TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            textSize = 13f
            setTextColor(context.getColorRes(R.color.red_warning))
            gravity = Gravity.CENTER
            includeFontPadding = false
            this.text = when {
                textRes != null && textRes != 0 -> activity.resources.getString(textRes)
                text != "" -> "$text"
                else -> ""
            }
        }


        childContainer.addView(textView)
        addView(childContainer)

        configureWindowFlags()

        decor.addView(this)

        childContainer.translationY = -context.statusBarHeight.toFloat()
        childContainer.animate()
            .translationY(0f)
            .setDuration(150)
            .setStartDelay(350)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun configureWindowFlags() {
        activity.window.decorView.rootView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
            setOnSystemUiVisibilityChangeListener {
                this.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
            }
        }

        isStatusBarTranslucent = activity.isTranslucentStatus

        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        statusBarColor = activity.window.statusBarColor
        activity.window.statusBarColor = Color.TRANSPARENT
    }

    override fun onDetachedFromWindow() {
        (context as Activity).window.decorView.setOnSystemUiVisibilityChangeListener(null)
        super.onDetachedFromWindow()
    }

    fun setText(text: String) {
        textView?.text = "$text"
    }

    fun setTextRes(@StringRes text: Int) {
        textView?.text = "${context.resources.getString(text)}"
    }

    companion object {
        @JvmField
        val activeInfoViews: MutableMap<String, MutableList<StatusInfoView>?> = mutableMapOf()

        internal fun addStatusBarInfoText(
            activity: Activity,
            text: String? = "",
            @StringRes textRes: Int? = 0,
            @ColorRes alertColor: Int = 0
        ): StatusInfoView? {
            this.hide(activity,null)

            val statusBarInfoView = StatusInfoView(activity, text, alertColor, textRes)

            if(activeInfoViews[activity.componentName.className] == null) {
                activeInfoViews[activity.componentName.className] = mutableListOf()
            }

            activeInfoViews[activity.componentName.className]?.add(statusBarInfoView)

            return statusBarInfoView
        }

        fun hide(activity: Activity, onHidden: Runnable?) {
            if (activeInfoViews[activity.componentName.className] == null || activeInfoViews[activity.componentName.className]?.size == 0) {
                onHidden?.run()
            } else {
                activeInfoViews[activity.componentName.className]?.forEach {
                    hideInternal(activity,it,onHidden)
                }
                activeInfoViews[activity.componentName.className]?.clear()
            }
        }

        private fun hideInternal(
            activity: Activity,
            statusInfoView: StatusInfoView,
            onHidden: Runnable?
        ) {
            if (statusInfoView.parent != null) {
                activity.window.decorView.rootView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

                activity.window.statusBarColor = statusInfoView.statusBarColor
                if (statusInfoView.isStatusBarTranslucent) {
                    activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                }

                val decor = activity.window.decorView as ViewGroup

                statusInfoView.animate()
                    .translationY(-activity.statusBarHeight.toFloat())
                    .setDuration(150)
                    .setStartDelay(500)
                    .setInterpolator(AccelerateInterpolator())
                    .setListener(object : SimpleAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            decor.removeView(statusInfoView)
                            onHidden?.run()
                        }
                    }).start()
            }
        }
    }
}