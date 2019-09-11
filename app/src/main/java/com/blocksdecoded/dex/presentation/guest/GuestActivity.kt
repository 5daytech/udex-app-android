package com.blocksdecoded.dex.presentation.guest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.main.MainActivity
import com.blocksdecoded.dex.presentation.restore.RestoreWalletActivity
import com.blocksdecoded.dex.presentation.widgets.click.setSingleClickListener
import kotlinx.android.synthetic.main.activity_guest.*

class GuestActivity : CoreActivity() {

    private lateinit var viewModel: GuestViewModel

    private val onboardingPages = listOf(
        GuestPageConfig(GuestPageType.MAIN, R.string.onboarding_about_app, 0, 0),
        GuestPageConfig(GuestPageType.INFO, R.string.onboarding_p2p_page, 0, R.drawable.ic_onboarding_p2p),
        GuestPageConfig(GuestPageType.INFO, R.string.onboarding_main_page, 0, R.drawable.ic_onboarding_main),
        GuestPageConfig(GuestPageType.INFO, R.string.onboarding_tokens_page, 0, R.drawable.ic_onboarding_tokens)
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest)
        initTransparentStatusBar()

        viewModel = ViewModelProviders.of(this).get(GuestViewModel::class.java)
        
        viewModel.openBackupEvent.observe(this, Observer {
            MainActivity.start(this)
        })
    
        viewModel.openRestoreEvent.observe(this, Observer {
            RestoreWalletActivity.start(this)
        })
        
        viewModel.finishEvent.observe(this, Observer {
            finish()
        })
        
        guest_create_wallet.setSingleClickListener { viewModel.onCreateClick() }
        guest_restore_wallet.setSingleClickListener { viewModel.onRestoreClick() }

        onboarding_viewpager.adapter = GuestOnboardingAdapter(supportFragmentManager, onboardingPages)
        onboarding_indicator.setViewPager(onboarding_viewpager)

        initTransparentStatusBar()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setStatusBarImmersiveMode(ContextCompat.getColor(this, R.color.transparent))
    }

    override fun addTestLabel() = Unit

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, GuestActivity::class.java))
        }
    }
}