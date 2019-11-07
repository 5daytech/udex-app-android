package com.blocksdecoded.dex.presentation.howitworks

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity

class HowItWorksActivity : CoreActivity() {

    private lateinit var viewModel: HowItWorksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_it_works)

        viewModel = ViewModelProviders.of(this).get(HowItWorksViewModel::class.java)
    }
}
