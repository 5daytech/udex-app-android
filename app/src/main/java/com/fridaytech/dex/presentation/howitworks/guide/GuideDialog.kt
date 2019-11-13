package com.fridaytech.dex.presentation.howitworks.guide

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.fridaytech.dex.R
import com.fridaytech.dex.presentation.dialogs.BaseBottomDialog
import kotlinx.android.synthetic.main.dialog_guide.*

class GuideDialog : BaseBottomDialog(R.layout.dialog_guide) {

    private val pages = listOf(
        GuidePageConfig(
            R.string.guide_page_1,
            R.string.guide_page_1_description,
            R.drawable.img_guide_1
        ),
        GuidePageConfig(
            R.string.guide_page_2,
            R.string.guide_page_2_description,
            R.drawable.img_guide_2
        ),
        GuidePageConfig(
            R.string.guide_page_3,
            R.string.guide_page_3_description,
            R.drawable.img_guide_3
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        guide_view_pager.scrollEnabled = true
        guide_view_pager.offscreenPageLimit = 3
        guide_view_pager.adapter =
            GuidePagerAdapter(
                childFragmentManager,
                pages
            )
        guide_pager_indicator.bindViewPager(guide_view_pager)

        guide_view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position >= pages.size - 1) {
                    guide_action_text.text = "Finish"
                } else {
                    guide_action_text.text = "Next"
                }
            }
        })

        guide_action.setOnClickListener {
            if (guide_view_pager.currentItem >= pages.size - 1) {
                dismiss()
            } else {
                guide_view_pager.currentItem++
            }
        }
    }

    companion object {
        fun show(fragmentManager: FragmentManager) {
            val dialog = GuideDialog()

            dialog.show(fragmentManager, "guide_dialog")
        }
    }

    private class GuidePagerAdapter(
        fm: FragmentManager,
        val pages: List<GuidePageConfig>
    ) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment =
            GuideDialogPage.newInstance(
                pages[position]
            )

        override fun getCount(): Int = pages.size
    }
}
