package com.blocksdecoded.dex.presentation.widgets.words

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.widget.LinearLayout
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.utils.listeners.SimpleTextWatcher
import kotlinx.android.synthetic.main.view_word_input.view.*

class WordInputView : LinearLayout {

    init {
        inflate(context, R.layout.view_word_input, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun bindPrefix(prefix: String) {
        word_prefix?.text = prefix
    }

    fun getEnteredText(): String? = word_input.text?.toString()

    fun setOnChangeListener(onChanged: (String) -> Unit) {
        word_input.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                onChanged.invoke(s.toString().trim())
            }
        })
    }
}
