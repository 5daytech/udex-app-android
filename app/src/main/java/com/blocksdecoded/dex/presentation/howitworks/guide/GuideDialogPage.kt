package com.blocksdecoded.dex.presentation.howitworks.guide

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import kotlinx.android.synthetic.main.dialog_guide_page.*

class GuideDialogPage : CoreFragment(R.layout.dialog_guide_page) {

    lateinit var config: GuidePageConfig

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        guide_page_title.setText(config.titleRes)
        guide_page_description.setText(config.descriptionRes)
        guide_page_image.setImageResource(config.imageRes)
    }

    companion object {
        fun newInstance(config: GuidePageConfig): Fragment = GuideDialogPage().apply {
            this.config = config
        }
    }
}

class GuidePageConfig(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int
)