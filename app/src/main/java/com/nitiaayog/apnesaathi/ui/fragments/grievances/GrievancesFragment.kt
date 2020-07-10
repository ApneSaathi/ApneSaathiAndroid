package com.nitiaayog.apnesaathi.ui.fragments.grievances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.FragmentViewPagerAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.calls.AllCallsFragment
import com.nitiaayog.apnesaathi.ui.fragments.calls.CallsStatusFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_calls.*
import kotlinx.android.synthetic.main.include_toolbar.*

class GrievancesFragment : BaseFragment<HomeViewModel>() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBar.title = getString(R.string.menu_issues)

        setUpViewPager()

        TabLayoutMediator(
            tabLayout, viewPager, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.pending)
                    1 -> tab.text = getString(R.string.in_progress)
                    2 -> tab.text = getString(R.string.resolved)
                }
            }).attach()
    }

    private fun setUpViewPager() {
        val adapter = FragmentViewPagerAdapter(activity!!)
        adapter.addFragment(PendingGrievanceFragment(), getString(R.string.pending))
        adapter.addFragment(InProgressGrievanceFragment(), getString(R.string.in_progress))
        adapter.addFragment(ResolvedGrievanceFragment(), getString(R.string.resolved))
        viewPager.adapter = adapter
    }

    override fun provideViewModel(): HomeViewModel =
        getViewModel { HomeViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_grievances

    override fun onCallPermissionGranted() {
    }

    override fun onCallPermissionDenied() {
    }
}