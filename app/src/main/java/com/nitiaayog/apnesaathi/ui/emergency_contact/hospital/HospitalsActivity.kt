package com.nitiaayog.apnesaathi.ui.emergency_contact.hospital

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import kotlinx.android.synthetic.main.include_register_new_sr_citizen.*
import kotlinx.android.synthetic.main.include_toolbar.*

class HospitalsActivity : BaseActivity<HospitalViewModel>() {
    lateinit var mContext: Context
    override fun provideViewModel(): HospitalViewModel {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.title = intent.getStringExtra("title")
        toolBar.setNavigationOnClickListener {
            finish()
        }
        bindview()
    }

    private fun bindview() {
        mContext = this

    }

    override fun provideLayoutResource(): Int = R.layout.hospital_list_activity
}