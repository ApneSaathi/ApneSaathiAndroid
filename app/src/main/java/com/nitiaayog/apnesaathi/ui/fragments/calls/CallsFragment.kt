package com.nitiaayog.apnesaathi.ui.fragments.calls

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.FragmentViewPagerAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_calls.*
import kotlinx.android.synthetic.main.include_toolbar.*

class CallsFragment : BaseFragment<HomeViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.title = getString(R.string.menu_calls)

        setUpViewPager()

        TabLayoutMediator(
            tabLayout, viewPager, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.call_status_1)
                    1 -> tab.text = getString(R.string.all_calls)
                }
            }).attach()
    }

    override fun provideViewModel(): HomeViewModel =
        getViewModel { HomeViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_calls

    override fun onCallPermissionGranted() {
    }

    override fun onCallPermissionDenied() {}

    private fun setUpViewPager() {
        val adapter = FragmentViewPagerAdapter(activity!!)
        adapter.addFragment(CallsStatusFragment(), getString(R.string.call_status_1))
        adapter.addFragment(AllCallsFragment(), getString(R.string.all_calls))
        viewPager.adapter = adapter
    }
}