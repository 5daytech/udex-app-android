package com.blocksdecoded.dex.presentation.settings.addressbook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.SwipeableActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import kotlinx.android.synthetic.main.activity_about_app.*

class AddressBookActivity : SwipeableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_book)
        toolbar.bind(MainToolbar.getBackAction { finish() })
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, AddressBookActivity::class.java))
        }
    }
}
