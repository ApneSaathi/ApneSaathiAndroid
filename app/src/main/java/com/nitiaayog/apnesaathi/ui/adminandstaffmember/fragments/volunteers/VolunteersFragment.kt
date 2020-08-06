package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteers

import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.home.HomeViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseFragment

class VolunteersFragment : BaseFragment<HomeViewModel>() {

    override fun provideViewModel(): HomeViewModel {
        return HomeViewModel.getInstance(requireContext(), dataManager)
    }

    override fun provideLayoutResource(): Int {
        return R.layout.fragment_calls_status
    }

    override fun onCallPermissionGranted() {
    }

    override fun onCallPermissionDenied() {
    }
}