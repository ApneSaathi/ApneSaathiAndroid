package com.nitiaayog.apnesaathi.ui.citizen_update

import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseActivity

class SeniorCitizenUpdateActivity  : BaseActivity<SeniorCitizenUpdateViewModel>(){
    override fun provideViewModel(): SeniorCitizenUpdateViewModel  =
    getViewModel { SeniorCitizenUpdateViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.activity_citizen_update
}