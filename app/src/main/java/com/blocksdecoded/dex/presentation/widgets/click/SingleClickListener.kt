package com.blocksdecoded.dex.presentation.widgets.click

import android.view.View

abstract class SingleClickListener : View.OnClickListener {
    abstract fun onSingleClick(v: View)

    override fun onClick(v: View) {
        if (SingleClickManager.canBeClicked()) {
            onSingleClick(v)
        }
    }
}

fun View.setSingleClickListener(listener: ((v: View) -> Unit)) {
    setOnClickListener(object : SingleClickListener() {
        override fun onSingleClick(v: View) = listener(v)
    })
}
