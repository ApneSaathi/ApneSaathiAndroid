package com.nitiaayog.apnesaathi.ui.emergencycontact

import android.content.Intent
import android.os.Bundle
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.emergencycontact.contact_data.ContactDataActivity
import kotlinx.android.synthetic.main.activity_emergency__contact_.*
import kotlinx.android.synthetic.main.include_toolbar.*

class MainEmergency_Contact_Activity : BaseActivity<MainEmergencyContact_ViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.title = getString(R.string.emergency_contact)
        toolBar.setNavigationOnClickListener {
            finish()
        }
        bindviews();
    }

    private fun bindviews() {
        card_hospitls.throttleClick().subscribe() {
            val intent = Intent(this, ContactDataActivity::class.java)
            intent.putExtra("title", ApiConstants.titleHospital)
            intent.putExtra("Toolbar_title", getString(R.string.hospitalcontact))
            startActivity(intent)
        }.autoDispose(disposables)
        card_police_stations.throttleClick().subscribe() {
            val intent = Intent(this, ContactDataActivity::class.java)
            intent.putExtra("title", ApiConstants.titlePoliceStation)
            intent.putExtra("Toolbar_title", getString(R.string.police_contact))
            startActivity(intent)
        }.autoDispose(disposables)
        card_apneSathi_counsultant.throttleClick().subscribe() {
            val intent = Intent(this, ContactDataActivity::class.java)
            intent.putExtra("title", ApiConstants.titleApneSathiConsulatant)
            intent.putExtra("Toolbar_title", getString(R.string.apnesatthiConsulatant))
            startActivity(intent)
        }.autoDispose(disposables)
        card_customeContact.throttleClick ().subscribe() {
            val intent = Intent(this, ContactDataActivity::class.java)
            intent.putExtra("title", ApiConstants.titleCustomContact)
            intent.putExtra("Toolbar_title", getString(R.string.customcontact))
            startActivity(intent)
        }.autoDispose(disposables)
        card_ambulance.throttleClick ().subscribe() {
            val intent = Intent(this, ContactDataActivity::class.java)
            intent.putExtra("title", ApiConstants.title108Ambulance)
            intent.putExtra("Toolbar_title", getString(R.string.ambulance))
            startActivity(intent)
        }.autoDispose(disposables)
        card_covidcontrol_room.throttleClick ().subscribe() {
            val intent = Intent(this, ContactDataActivity::class.java)
            intent.putExtra("title", ApiConstants.titlecovidcontrolroom)
            intent.putExtra("Toolbar_title", getString(R.string.covidcontrolroom))
            startActivity(intent)
        }.autoDispose(disposables)

    }

    override fun provideViewModel(): MainEmergencyContact_ViewModel {
        TODO("Not yet implemented")
    }

    override fun provideLayoutResource(): Int = R.layout.activity_emergency__contact_
}