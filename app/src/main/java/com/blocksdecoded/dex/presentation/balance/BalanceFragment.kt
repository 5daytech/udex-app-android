package com.blocksdecoded.dex.presentation.balance

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.ui.CoreFragment

class BalanceFragment : CoreFragment(R.layout.fragment_balance) {

    private lateinit var viewModel: BalanceViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BalanceViewModel::class.java)
        // TODO: Use the ViewModel
    }

    companion object {
        fun newInstance() = BalanceFragment()
    }

}
