package com.nitiaayog.apnesaathi.ui.emergency_contact.hospital

import android.content.Context
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.emergency_contact.adapter.ContactDummyData
import com.nitiaayog.apnesaathi.ui.emergency_contact.adapter.ContactListAdapter
import com.nitiaayog.apnesaathi.utility.ROLE_MASTER_ADMIN
import kotlinx.android.synthetic.main.hospital_list_activity.*
import kotlinx.android.synthetic.main.include_toolbar.*

class ContactDataActivity : BaseActivity<ContactDataViewModel>() {
    lateinit var mContext: Context
    lateinit var contact_listadapter: ContactListAdapter

    lateinit var contactList: ArrayList<ContactDummyData>
    override fun provideViewModel(): ContactDataViewModel {
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
        contactList = ArrayList()
        prepareDummyData()

        if (dataManager.getRole() == ROLE_MASTER_ADMIN) {
            relDistrictLayout.visibility=VISIBLE
            dataManager.getDistrictList()

//            val adapter = ArrayAdapter( this,R.layout.spinner_item, resources.getStringArray(R.array.states_array))
//            actDistrict.adapter = adapter // static state list
        } else {
            relDistrictLayout.visibility=GONE
        }



        val rvList = findViewById(R.id.rvList) as RecyclerView
        rvList.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        contact_listadapter =
            ContactListAdapter(
                mContext,
                prepareDummyData()
            )
        rvList.adapter = contact_listadapter


    }



    private fun prepareDummyData(): List<ContactDummyData> {
        contactList.add(
            ContactDummyData(
                "Pune Hinjewaji ",
                "024-93XXXXXXX"
            )
        )
        contactList.add(
            ContactDummyData(
                "Mumbai Katraj ",
                "024-93XXXXXXX"
            )
        )
        contactList.add(
            ContactDummyData(
                "Ahmednagar Hinjewaji ",
                "024-93XXXXXXX"
            )
        )
        contactList.add(
            ContactDummyData(
                "Kolhapur Hinjewaji ",
                "024-93XXXXXXX"
            )
        )
        contactList.add(
            ContactDummyData(
                "Pune Hinjewaji ",
                "024-93XXXXXXX"
            )
        )
        contactList.add(
            ContactDummyData(
                "Pune Hinjewaji ",
                "024-93XXXXXXX"
            )
        )
        contactList.add(
            ContactDummyData(
                "Pune Hinjewaji ",
                "024-93XXXXXXX"
            )
        )
        return contactList
    }

    override fun provideLayoutResource(): Int = R.layout.hospital_list_activity
}

