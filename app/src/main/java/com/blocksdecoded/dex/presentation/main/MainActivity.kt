package com.blocksdecoded.dex.presentation.main

import android.os.Bundle
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.ui.CoreActivity

class MainActivity : CoreActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
