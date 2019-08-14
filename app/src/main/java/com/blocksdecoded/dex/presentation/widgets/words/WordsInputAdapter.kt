package com.blocksdecoded.dex.presentation.widgets.words

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R

class WordsInputAdapter(private val listener: WordInputViewHolder.OnWordChangeListener) : RecyclerView.Adapter<WordInputViewHolder>() {

    override fun getItemCount() = 12

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordInputViewHolder {
        val inputTextView = LayoutInflater.from(parent.context).inflate(R.layout.item_word_input, parent, false) as WordInputView
        return WordInputViewHolder(inputTextView, listener)
    }

    override fun onBindViewHolder(holder: WordInputViewHolder, position: Int) {
        holder.bind(position)
    }

}
