package com.blocksdecoded.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.utils.ui.TimeUtils
import com.blocksdecoded.dex.utils.visible
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.view_toolbar.view.*
import java.util.*

class MainToolbar : AppBarLayout {
    var menuVisible: Boolean
        get() = toolbar_menu?.visible ?: false
        set(value) { toolbar_menu?.visible = value }

    var title = ""
        set(value) {
            field = value
            toolbar_title?.text = value
        }

    init {
        inflate(context, R.layout.view_toolbar, this)
        setBackgroundResource(android.R.color.transparent)
        elevation = 0f
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

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

        toolbar_title?.text = title
        toolbar_date?.text = TimeUtils.dateSimpleFormat(Date())
    }
}
