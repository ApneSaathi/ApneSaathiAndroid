package com.nitiaayog.apnesaathi.ui.fragments

import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseFragment

class SeniorCitizenUpdateForm : BaseFragment<SeniorCitizenUpdateFormViewModel>() {
    override fun provideViewModel(): SeniorCitizenUpdateFormViewModel =
        getViewModel { SeniorCitizenUpdateFormViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.senior_citizen_update_form
    override fun onCallPermissionGranted() {
        TODO("Not yet implemented")
    }

    override fun onCallPermissionDenied() {
        TODO("Not yet implemented")
    }
}
