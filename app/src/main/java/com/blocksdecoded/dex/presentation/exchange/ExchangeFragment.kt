package com.blocksdecoded.dex.presentation.exchange

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.ui.CoreFragment

class ExchangeFragment : CoreFragment(R.layout.fragment_exchange) {

    private lateinit var viewModel: ExchangeViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ExchangeViewModel::class.java)
        // TODO: Use the ViewModel
    }

    companion object {
        fun newInstance() = ExchangeFragment()
    }

}
