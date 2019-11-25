package com.fridaytech.dex.presentation.widgets.statusinfo

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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.fridaytech.dex.R
import com.fridaytech.dex.utils.*
import com.fridaytech.dex.presentation.widgets.listeners.SimpleAnimatorListener
import kotlin.math.absoluteValue

@SuppressLint("ViewConstructor")
class StatusInfoView(
    context: Context,
    stringText: String?,
    @ColorRes alertColorRes: Int,
    @StringRes textRes: Int?
) : LinearLayout(context, null, 0) {
    private val activity: Activity
        get() = context as Activity

    var statusBarColor: Int = 0
    var isStatusBarTranslucent: Boolean = false
    private var textView: TextView? = null
    private var progressView: ProgressBar? = null

    init {
        this.observeLifecycle(activity)
        this.initView(stringText, alertColorRes, textRes)
    }

    private fun observeLifecycle(activity: Context) {
        if (activity is AppCompatActivity) {
            activity.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    hide(
                        activity,
                        Runnable {})
                    activity.lifecycle.removeObserver(this)
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

        val childContainer = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        progressView = ProgressBar(context).apply {
            val params = MarginLayoutParams(dp(12f), ViewGroup.LayoutParams.WRAP_CONTENT)
            params.marginEnd = dp(4f)
            layoutParams = params
            isIndeterminate = true
        }

        textView = TextView(context).apply {
            val lp = MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.marginEnd = dp(16f)
            layoutParams = lp
            textSize = 12f
            setTextColor(context.theme.getAttr(R.attr.PrimaryTextColor) ?: 0)
            gravity = Gravity.CENTER
            includeFontPadding = false
            this.text = when {
                textRes != null && textRes != 0 -> activity.resources.getString(textRes)
                text != "" -> "$text"
                else -> ""
            }
        }

        childContainer.addView(progressView)
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
            var systemUiFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE
            if (ThemeHelper.isLightTheme()) {
                systemUiFlags = systemUiFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            systemUiVisibility = systemUiFlags
            setOnSystemUiVisibilityChangeListener {
                this.systemUiVisibility = systemUiFlags
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
        textView?.text = text
    }

    fun setText(@StringRes text: Int) {
        textView?.text = "${context.resources.getString(text)}"
    }

    fun setProgressVisible(visible: Boolean) {
        progressView?.isInvisible = !visible
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
            val key = activity.toString()
            hide(
                activity,
                null
            )

            val statusBarInfoView =
                StatusInfoView(
                    activity,
                    text,
                    alertColor,
                    textRes
                )

            if (activeInfoViews[key] == null) {
                activeInfoViews[key] = mutableListOf()
            }

            activeInfoViews[key]?.add(statusBarInfoView)

            return statusBarInfoView
        }

        fun hide(activity: Activity, onHidden: Runnable?) {
            val key = activity.toString()
            if (activeInfoViews[key] == null || activeInfoViews[key]?.size == 0) {
                onHidden?.run()
            } else {
                activeInfoViews[key]?.forEach {
                    hideInternal(
                        activity,
                        it,
                        onHidden
                    )
                }
                activeInfoViews[key]?.clear()
            }
        }

        private fun hideInternal(
            activity: Activity,
            statusInfoView: StatusInfoView,
            onHidden: Runnable?
        ) {
            if (statusInfoView.parent != null) {
                val decor = activity.window.decorView as ViewGroup

                statusInfoView.animate()
                    .translationY(-activity.statusBarHeight.toFloat())
                    .setDuration(150)
                    .setStartDelay(500)
                    .setInterpolator(AccelerateInterpolator())
                    .setListener(object : SimpleAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            var systemUiFlags = View.SYSTEM_UI_FLAG_VISIBLE
                            if (ThemeHelper.isLightTheme()) {
                                systemUiFlags = systemUiFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            }
                            activity.window.decorView.rootView.systemUiVisibility = systemUiFlags

                            activity.window.statusBarColor = statusInfoView.statusBarColor
                            if (statusInfoView.isStatusBarTranslucent) {
                                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                            }

                            decor.removeView(statusInfoView)
                            onHidden?.run()
                        }
                    }).start()
            }
        }
    }
}
