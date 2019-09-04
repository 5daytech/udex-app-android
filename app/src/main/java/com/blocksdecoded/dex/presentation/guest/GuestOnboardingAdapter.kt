package com.blocksdecoded.dex.presentation.guest

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import kotlinx.android.synthetic.main.fragment_guest_onboarding.*

class GuestOnboardingAdapter(
    fragmentManager: FragmentManager,
    private val pages: List<GuestPageConfig>
): FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment =
        GuestOnboardingFragment.newInstance(pages[position])

    override fun getCount(): Int = pages.size
}

class GuestOnboardingFragment: CoreFragment(R.layout.fragment_guest_onboarding) {
    private var config: GuestPageConfig? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config?.let {
            onboarding_title?.setText(it.titleRes)
            onboarding_icon?.setImageResource(it.imageRes)
        }
    }

    companion object {
        fun newInstance(config: GuestPageConfig): Fragment =
            GuestOnboardingFragment().apply { this.config = config }
    }
}

data class GuestPageConfig(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int = 0
)