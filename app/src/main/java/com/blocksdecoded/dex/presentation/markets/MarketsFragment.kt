package com.blocksdecoded.dex.presentation.markets

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment

class MarketsFragment : CoreFragment(R.layout.fragment_markets) {

    private lateinit var viewModel: MarketsViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MarketsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    companion object {
        fun newInstance() = MarketsFragment()
    }

}
