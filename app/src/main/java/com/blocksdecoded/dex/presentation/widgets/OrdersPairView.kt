package com.blocksdecoded.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.blocksdecoded.dex.R
import kotlinx.android.synthetic.main.view_current_pair.view.*

class OrdersPairView: LinearLayout {
    
    var selectedPair: Int = 0
        set(value) {
            field = value
            current_pair_spinner?.let { spinner ->
                if (!spinner.adapter.isEmpty) current_pair_spinner?.setSelection(value)
            }
        }
    
    init {
        View.inflate(context, R.layout.view_current_pair, this)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    
    fun init(onSelectPair: (Int) -> Unit) {
        current_pair_spinner.onItemSelectedListener = object: ItemSelectedListener() {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) =
                onSelectPair(position)
        }
    }
    
    fun refreshPairs(pairs: List<Pair<String, String>>) {
        val adapter = ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item)
        adapter.addAll(pairs.map { "${it.first}/${it.second}" })
    
        current_pair_spinner.adapter = adapter
    }
}