package com.blocksdecoded.dex.presentation.account

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment

class AccountFragment : CoreFragment(R.layout.fragment_account) {

    private lateinit var viewModel: AccountViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        // TODO: Use the ViewModel
    }

    companion object {
        fun newInstance() = AccountFragment()
    }

}
