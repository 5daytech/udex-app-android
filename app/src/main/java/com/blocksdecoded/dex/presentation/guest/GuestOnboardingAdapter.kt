package com.blocksdecoded.dex.presentation.guest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.blocksdecoded.dex.R
import kotlinx.android.synthetic.main.fragment_guest_onboarding_main.*

class GuestOnboardingAdapter(
    fragmentManager: FragmentManager,
    private val pages: List<GuestPageConfig>
): FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment =
        GuestOnboardingFragment.newInstance(pages[position])

    override fun getCount(): Int = pages.size
}

class GuestOnboardingFragment: Fragment() {
    private var config: GuestPageConfig? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutRes = if (config?.type == GuestPageType.MAIN) {
            R.layout.fragment_guest_onboarding_main
        } else {
            R.layout.fragment_guest_onboarding_info
        }

        return inflater.inflate(layoutRes, container, false)
    }

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
    val type: GuestPageType,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int = 0
)

enum class GuestPageType { MAIN, INFO }