package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteers

import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.home.HomeViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseFragment

class VolunteersFragment : BaseFragment<HomeViewModel>() {

    override fun provideViewModel(): HomeViewModel {
        return HomeViewModel.getInstance(dataManager)
    }

    override fun provideLayoutResource(): Int {
        TODO("Not yet implemented")
    }

    override fun onCallPermissionGranted() {
        TODO("Not yet implemented")
    }

    override fun onCallPermissionDenied() {
        TODO("Not yet implemented")
    }

}