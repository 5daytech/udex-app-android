package com.blocksdecoded.dex.core.ui

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blocksdecoded.dex.App

abstract class CoreActivity: AppCompatActivity() {

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        if (App.appConfiguration.testMode) {
            addTestLabel()
        }
    }

    private fun addTestLabel() {
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        val testLabelTv = TextView(this)
        testLabelTv.text = "Ropsten"
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