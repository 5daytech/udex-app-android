package com.blocksdecoded.dex.presentation.guest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.main.MainActivity
import com.blocksdecoded.dex.presentation.restore.RestoreWalletActivity
import com.blocksdecoded.dex.presentation.widgets.click.setSingleClickListener
import kotlinx.android.synthetic.main.activity_guest.*
import kotlinx.android.synthetic.main.fragment_guest_onboarding.*

class GuestActivity : CoreActivity() {

    private lateinit var viewModel: GuestViewModel

    private val onboardingPages = listOf(
        R.string.onboarding_about_app to R.drawable.ic_about,
        R.string.onboarding_long_text to R.drawable.ic_about,
        R.string.onboarding_about_app to R.drawable.ic_about,
        R.string.onboarding_about_app to R.drawable.ic_about,
        R.string.onboarding_about_app to R.drawable.ic_about
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

class GuestOnboardingAdapter(
    fragmentManager: FragmentManager,
    private val pages: List<Pair<Int, Int>>
): FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment =
        GuestOnboardingFragment.newInstance(pages[position])

    override fun getCount(): Int = pages.size
}

class GuestOnboardingFragment: CoreFragment(R.layout.fragment_guest_onboarding) {
    private var config: Pair<Int, Int>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config?.let {
            onboarding_title?.setText(it.first)
            onboarding_icon?.setImageResource(it.second)
        }
    }

    companion object {
        fun newInstance(config: Pair<Int, Int>): Fragment {
            return GuestOnboardingFragment().apply {
                this.config = config
            }
        }
    }
}