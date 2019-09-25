package com.blocksdecoded.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.StringRes
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.utils.visible
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.view_toolbar.view.*

class MainToolbar : AppBarLayout {
    var title = ""
        set(value) {
            field = value
            toolbar_title?.text = value
        }

    //region Init

    init {
        inflate(context, R.layout.view_toolbar, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs) }

    private fun init(attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MainToolbar, 0, 0)
        try {
            title = ta.getString(R.styleable.MainToolbar_mt_title) ?: ""
        } finally {
            ta.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        setBackgroundResource(android.R.color.transparent)
        targetElevation = 0f
        toolbar_title?.text = title
    }

    //endregion

    fun bind(
        leftActionButton: ActionInfo? = null,
        rightActionButton: ActionInfo? = null
    ) {
        toolbar_left_action?.visible = leftActionButton != null

        leftActionButton?.let {
            toolbar_left_action?.setImageResource(it.iconRes)
            toolbar_left_action?.setOnClickListener { leftActionButton.onClick() }
        }
    }

    fun setTitle(@StringRes stringRes: Int) {
        title = context.getString(stringRes)
    }

    data class ActionInfo(
        val iconRes: Int,
        val text: String = "",
        val onClick: () -> Unit
    )

    companion object {
        fun getBackAction(onClick: () -> Unit): ActionInfo =
            ActionInfo(R.drawable.ic_back, "", onClick)
    }
}
