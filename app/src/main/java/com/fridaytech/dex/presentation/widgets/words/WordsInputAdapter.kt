package com.fridaytech.dex.presentation.widgets.words

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fridaytech.dex.R
import com.fridaytech.dex.utils.inflate

class WordsInputAdapter(private val listener: WordInputViewHolder.OnWordChangeListener) : RecyclerView.Adapter<WordInputViewHolder>() {

    override fun getItemCount() = 12

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordInputViewHolder {
        val inputTextView = parent.inflate(R.layout.item_word_input) as WordInputView
        return WordInputViewHolder(
            inputTextView,
            listener
        )
    }

    override fun onBindViewHolder(holder: WordInputViewHolder, position: Int) {
        holder.bind(position)
    }
}
