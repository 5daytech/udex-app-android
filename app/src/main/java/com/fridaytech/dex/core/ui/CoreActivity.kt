package com.fridaytech.dex.core.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.fridaytech.dex.App
import com.fridaytech.dex.BuildConfig
import com.fridaytech.dex.utils.ThemeHelper

abstract class CoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeHelper.getActivityTheme())

        if (!BuildConfig.DEBUG) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        if (App.appConfiguration.testMode) {
            addTestLabel()
        }
    }

    //region Status bar

    protected fun initTransparentStatusBar() {
        val contentParent = findViewById<ViewGroup>(android.R.id.content)
        val content = contentParent.getChildAt(0)
        setFitsSystemWindows(content, fitSystemWindows = false, applyToChildren = true)
    }

    private fun setFitsSystemWindows(view: View?, fitSystemWindows: Boolean, applyToChildren: Boolean) {
        if (view == null) return

        view.fitsSystemWindows = fitSystemWindows
        if (applyToChildren && view is ViewGroup) {
            var i = 0
            view.let {
                val n = view.childCount
                while (i < n) {
                    view.getChildAt(i).fitsSystemWindows = fitSystemWindows
                    i ++
                }
            }
        }
    }

    protected fun setStatusBarImmersiveMode(@ColorInt color: Int) {
        val win = window
        win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        win.attributes.systemUiVisibility = win.attributes.systemUiVisibility or (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        win.statusBarColor = color
    }

    //endregion

    open fun addTestLabel() {
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        val testLabelTv = TextView(this)
        testLabelTv.text = "Ropsten Testnet"
        testLabelTv.setPadding(10, 3, 10, 3)
        testLabelTv.includeFontPadding = false
        testLabelTv.setBackgroundColor(Color.WHITE)
        testLabelTv.setTextColor(Color.BLACK)
        testLabelTv.textSize = 12f
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL
        testLabelTv.layoutParams = layoutParams
        rootView.addView(testLabelTv)
    }
}
