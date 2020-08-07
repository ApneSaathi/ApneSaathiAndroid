package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteerdetails

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.FragmentViewPagerAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.about.AboutVolunteerFragment
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.calls.allcalls.AllCallsFragment
import kotlinx.android.synthetic.main.fragment_calls.*
import kotlinx.android.synthetic.main.include_toolbar.*

class VolunteerDetailsFragment : BaseFragment<VolunteerDetailsViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.title = getString(R.string.volunteer_details)

        fabRegisterNewSrCitizen.visibility = View.GONE

        setUpViewPager()

        TabLayoutMediator(
            tabLayout, viewPager, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.about)
                    1 -> tab.text = getString(R.string.reviews)
                }
            }).attach()
    }

    override fun provideViewModel(): VolunteerDetailsViewModel {
        return getViewModel { VolunteerDetailsViewModel.getInstance(dataManager) }
    }

    override fun provideLayoutResource(): Int {
        return R.layout.fragment_calls
    }

    override fun onCallPermissionGranted() {

    }

    override fun onCallPermissionDenied() {

    }

    private fun setUpViewPager() {
        val adapter = FragmentViewPagerAdapter(requireActivity())
        adapter.addFragment(AboutVolunteerFragment(), getString(R.string.about))
        adapter.addFragment(AllCallsFragment(), getString(R.string.reviews))
        viewPager.adapter = adapter
    }
}