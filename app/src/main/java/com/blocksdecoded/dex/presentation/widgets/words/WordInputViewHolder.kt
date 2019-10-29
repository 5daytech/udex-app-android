package com.blocksdecoded.dex.presentation.widgets.words

import androidx.recyclerview.widget.RecyclerView

class WordInputViewHolder(private val inputTextView: WordInputView, listener: OnWordChangeListener) :
    RecyclerView.ViewHolder(inputTextView) {

    interface OnWordChangeListener {
        fun onChange(position: Int, value: String)
    }

    init {
        inputTextView.setOnChangeListener { listener.onChange(adapterPosition, it) }
    }

    fun bind(position: Int) {
        inputTextView.bindPrefix("${position + 1}")
    }
}
