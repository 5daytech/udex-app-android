package com.blocksdecoded.dex.presentation.launch

import android.os.Bundle
import android.os.PersistableBundle
import com.blocksdecoded.dex.presentation.main.MainActivity
import com.blocksdecoded.dex.ui.CoreActivity

class LaunchActivity: CoreActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.start(this)
    }

}