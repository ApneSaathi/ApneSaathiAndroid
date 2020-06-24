package com.nitiaayog.apnesaathi.ui.fragments.calls.registernewseniorcitizen

import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseFragment

class RegisterNewSeniorCitizenFragment : BaseFragment<RegisterSeniorCitizenViewModel>() {

    override fun provideViewModel(): RegisterSeniorCitizenViewModel =
        getViewModel { RegisterSeniorCitizenViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_register_new_sr_citizen

    override fun onCallPermissionGranted() {

    }

    override fun onCallPermissionDenied() {

    }
}