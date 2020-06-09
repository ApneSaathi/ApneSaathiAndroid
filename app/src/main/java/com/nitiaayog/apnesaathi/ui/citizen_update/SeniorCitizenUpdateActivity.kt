package com.nitiaayog.apnesaathi.ui.citizen_update

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import kotlinx.android.synthetic.main.senior_citizen_update_form.*

class SeniorCitizenUpdateActivity  : BaseActivity<SeniorCitizenUpdateViewModel>(){
    override fun provideViewModel(): SeniorCitizenUpdateViewModel  =
    getViewModel { SeniorCitizenUpdateViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.senior_citizen_update_form

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNavigationDrawer()
    }

    private fun initNavigationDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
}