package com.blocksdecoded.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.utils.getAttr
import com.blocksdecoded.dex.utils.inflate

class OptionSelectorView : LinearLayout, View.OnClickListener {
    private var mOptions = arrayListOf("first", "second")

    private var mSelectedTextColor = R.attr.PrimaryTextColor
    private var mDefaultTextColor = R.attr.SecondaryHintTextColor

    private var mListener: ((number: Int) -> Unit)? = null
    private var mNumberViews = HashMap<Int, View>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val a = context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.OptionSelectorView,
                0, 0
        )

        try {
            mSelectedTextColor = a.getInt(R.styleable.OptionSelectorView_selectedTextColor, R.attr.PrimaryTextColor)
            mDefaultTextColor = a.getInt(R.styleable.OptionSelectorView_defaultTextColor, R.attr.SecondaryHintTextColor)

            a.getString(R.styleable.OptionSelectorView_osv_options)?.let {
                mOptions.clear()
                mOptions.addAll(it.split(","))
            }

            inflateViews(mOptions)
        } finally {
            a.recycle()
        }
    }

    private fun inflateViews(options: ArrayList<String>) {
        mNumberViews.clear()

        options.forEachIndexed { index, it ->
            val view = this.inflate(R.layout.view_selector_text)
            if (view is TextView) view.text = it

            mNumberViews[index] = view
            this.addView(view)

            view.setOnClickListener(this)
        }

        setSelectedView(0)
    }

    fun setOptions(options: ArrayList<String>) {
        mOptions.clear()
        mOptions.addAll(options)
        inflateViews(mOptions)
    }

    private fun selectView(view: View) {
        deselectAll()

        if (view is TextView) {
            context.theme.getAttr(mSelectedTextColor)?.let { view.setTextColor(it) }
        }
    }

    private fun setDefaultView(view: View) {
        if (view is TextView) {
            context.theme.getAttr(mDefaultTextColor)?.let { view.setTextColor(it) }
        }
    }

    private fun deselectAll() {
        mNumberViews.values.forEach { setDefaultView(it) }
    }

    private fun onViewInteract(v: View?) {
        v?.let {
            if (it is TextView) {
                it.text?.let { text ->
                    val index = mOptions.indexOfFirst { it == text }
                    setSelectedView(index)
                    mListener?.invoke(index)
                }
            }
        }
    }

    fun setSelectedView(value: Int) {
        mNumberViews[value]?.let {
            selectView(it)
        }
    }

    fun addClickListener(listener: (position: Int) -> Unit) {
        mListener = listener
    }

    override fun onClick(v: View?) {
        onViewInteract(v)
    }
}