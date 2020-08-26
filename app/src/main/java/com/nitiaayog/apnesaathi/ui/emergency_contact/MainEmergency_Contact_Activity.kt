package com.nitiaayog.apnesaathi.ui.emergency_contact

import android.content.Intent
import android.os.Bundle
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.emergency_contact.hospital.ContactDataActivity
import kotlinx.android.synthetic.main.activity_emergency__contact_.*
import kotlinx.android.synthetic.main.include_toolbar.*

class MainEmergency_Contact_Activity : BaseActivity<MainEmergencyContact_ViewModel>() {
    private val titleHospital="Hospital's Contact"
    private val titlePoliceStation="Police Station's"
    private val titleApneSathiConsulatant="Apne Sathi Consulatant's"
    private val titleCustomContact="Custom Contact's"
    private val title108Ambulance="108 Ambulance"
    private val titlecovidcontrolroom="Covid-Control-Room"
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
            intent.putExtra("title", titleHospital)
            startActivity(intent)
        }.autoDispose(disposables)
        card_police_stations.throttleClick().subscribe() {
            val intent = Intent(this, ContactDataActivity::class.java)
            intent.putExtra("title", titlePoliceStation)
            startActivity(intent)
        }.autoDispose(disposables)
        card_apneSathi_counsultant.throttleClick().subscribe() {
            val intent = Intent(this, ContactDataActivity::class.java)
            intent.putExtra("title", titleApneSathiConsulatant)
            startActivity(intent)
        }.autoDispose(disposables)
        card_customeContact.throttleClick ().subscribe() {
            val intent = Intent(this, ContactDataActivity::class.java)
            intent.putExtra("title", titleCustomContact)
            startActivity(intent)
        }.autoDispose(disposables)
        card_ambulance.throttleClick ().subscribe() {
            val intent = Intent(this, ContactDataActivity::class.java)
            intent.putExtra("title", title108Ambulance)
            startActivity(intent)
        }.autoDispose(disposables)
        card_covidcontrol_room.throttleClick ().subscribe() {
            val intent = Intent(this, ContactDataActivity::class.java)
            intent.putExtra("title", titlecovidcontrolroom)
            startActivity(intent)
        }.autoDispose(disposables)

    }

    override fun provideViewModel(): MainEmergencyContact_ViewModel {
        TODO("Not yet implemented")
    }

    override fun provideLayoutResource(): Int = R.layout.activity_emergency__contact_
}