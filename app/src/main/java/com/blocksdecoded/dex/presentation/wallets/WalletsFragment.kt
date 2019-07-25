package com.blocksdecoded.dex.presentation.wallets

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.ui.CoreFragment

class WalletsFragment : CoreFragment(R.layout.fragment_wallets) {

    private lateinit var viewModel: WalletsViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WalletsViewModel::class.java)
    }

    companion object {
        fun newInstance() = WalletsFragment()
    }

}
