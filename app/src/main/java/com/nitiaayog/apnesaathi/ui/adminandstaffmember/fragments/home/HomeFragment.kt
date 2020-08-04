package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.home

import android.os.Bundle
import android.view.View
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.ui.base.BaseFragment

class HomeFragment : BaseFragment<HomeViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        
    }

    override fun provideViewModel(): HomeViewModel {
        return HomeViewModel.getInstance(dataManager)
    }

    override fun provideLayoutResource(): Int {
        return R.layout.fragment_admin_staff_home
    }

    override fun onCallPermissionGranted() {
        TODO("Not yet implemented")
    }

    override fun onCallPermissionDenied() {
        TODO("Not yet implemented")
    }
}