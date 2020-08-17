package com.nitiaayog.apnesaathi.ui.emergency_contact.hospital

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import kotlinx.android.synthetic.main.hospital_list_activity.*
import kotlinx.android.synthetic.main.include_toolbar.*

class HospitalsActivity : BaseActivity<HospitalViewModel>() {
    lateinit var mContext: Context
    lateinit var contact_listadapter: ContactListAdapter

    lateinit var contactList: ArrayList<ContactData>


//    var list = listOf(
//        "Pune police , Hinjewadi-024-252205", "Pune police , Hinjewadi-024-252205",
//        "Pune police , Hinjewadi-024-252205", "Pune police , Hinjewadi-024-252205"
//        , "Pune police , Hinjewadi-024-252205", "Pune police , Hinjewadi-024-252205"
//        , "Pune police , Hinjewadi-024-252205", "Pune police , Hinjewadi-024-252205"
//    )

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
        contactList = ArrayList()
        preparePoliceStationData()


        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item, resources.getStringArray(R.array.states_array)
        )

        actDistrict.adapter = adapter
        val rvList = findViewById(R.id.rvList) as RecyclerView

        rvList.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)

        contact_listadapter = ContactListAdapter(mContext, preparePoliceStationData())
        rvList.adapter = contact_listadapter


    }

    private fun prepareHospitalData(): List<ContactData> {
        contactList.add(ContactData("Pune Hinjewaji Hospital", "024-93XXXXXXX"))
        contactList.add(ContactData("Mumbai Katraj Hospital", "024-93XXXXXXX"))
        contactList.add(ContactData("Ahmednagar Hinjewaji Hospital", "024-93XXXXXXX"))
        contactList.add(ContactData("Kolhapur Hinjewaji Hospital", "024-93XXXXXXX"))
        contactList.add(ContactData("Pune Hinjewaji Hospital", "024-93XXXXXXX"))
        contactList.add(ContactData("Pune Hinjewaji Hospital", "024-93XXXXXXX"))
        contactList.add(ContactData("Pune Hinjewaji Hospital", "024-93XXXXXXX"))

        return contactList
    }

    private fun preparePoliceStationData(): List<ContactData> {
        contactList.add(ContactData("Pune Hinjewaji ", "024-93XXXXXXX"))
        contactList.add(ContactData("Mumbai Katraj ", "024-93XXXXXXX"))
        contactList.add(ContactData("Ahmednagar Hinjewaji ", "024-93XXXXXXX"))
        contactList.add(ContactData("Kolhapur Hinjewaji ", "024-93XXXXXXX"))
        contactList.add(ContactData("Pune Hinjewaji ", "024-93XXXXXXX"))
        contactList.add(ContactData("Pune Hinjewaji ", "024-93XXXXXXX"))
        contactList.add(ContactData("Pune Hinjewaji ", "024-93XXXXXXX"))

        return contactList
    }

    override fun provideLayoutResource(): Int = R.layout.hospital_list_activity
}

