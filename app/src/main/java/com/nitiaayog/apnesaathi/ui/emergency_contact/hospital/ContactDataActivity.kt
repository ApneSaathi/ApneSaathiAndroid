package com.nitiaayog.apnesaathi.ui.emergency_contact.hospital

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.ProgressDialog
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.emergencycontact.EmergencyContactResponse
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.emergency_contact.adapter.ContactDummyData
import com.nitiaayog.apnesaathi.ui.emergency_contact.adapter.ContactListAdapter
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import com.nitiaayog.apnesaathi.utility.ROLE_MASTER_ADMIN
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.hospital_list_activity.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.util.concurrent.TimeUnit


class ContactDataActivity : BaseActivity<ContactDataViewModel>() {
    lateinit var mContext: Context
    lateinit var contact_listadapter: ContactListAdapter
    var districtNames = mutableListOf<String>()
    lateinit var contactList: ArrayList<ContactDummyData>
    override fun provideViewModel(): ContactDataViewModel {
        return ContactDataViewModel.getInstance(dataManager)
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

    private val progressDialog: ProgressDialog.Builder by lazy {
        ProgressDialog.Builder(this!!).setMessage(R.string.wait_for_emergency_data)
    }

    private fun bindview() {
        mContext = this
        contactList = ArrayList()
        observeStates()
        if (dataManager.getRole() == ROLE_MASTER_ADMIN) {
            relDistrictLayout.visibility = VISIBLE
            viewModel.getDistrictList().removeObservers(this)
            viewModel.getDistrictList().observe(this, Observer {

                for (districtItem in it) {
                    districtItem.districtName?.let { it1 -> districtNames.add(it1) }
                }
                val adapter = ArrayAdapter(this, R.layout.spinner_item, districtNames)
                actDistrict.adapter = adapter
            })


            actDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    try {
                        Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                                viewModel.callEmergencyDataApi(
                                    mContext,
                                    "2"
                                )
                            }.autoDispose(disposables)
                    } catch (e: Exception) {
                        println("TAG -- MyData --> ${e.message}")
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

            // static state list
        } else {
            relDistrictLayout.visibility = GONE
        }


        val rvList = findViewById(R.id.rvList) as RecyclerView
        rvList.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)


    }

    private fun observeStates() {
        viewModel.getDataObserver().removeObservers(this)
        viewModel.getDataObserver().observe(this, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable -> {
                    BaseUtility.showAlertMessage(
                        mContext!!,
                        R.string.alert,
                        R.string.check_internet
                    )
                }
                is NetworkRequestState.LoadingData -> {
                    progressDialog.show()
                }
                is NetworkRequestState.ErrorResponse -> {
                    progressDialog.show()
                }
                is NetworkRequestState.SuccessResponse<*> -> {
                    progressDialog.dismiss()
                    val loginResponse = it.data as EmergencyContactResponse
                    contactList.clear()
                    for (item in loginResponse.emergencyContactsList!!) {
                        when {
                            intent.getStringExtra("title")
                                .equals(ApiConstants.titlePoliceStation) -> {
                                contactList.add(ContactDummyData("", item.police!!))
                            }
                            intent.getStringExtra("title").equals(ApiConstants.titleHospital) -> {
                                contactList.add(ContactDummyData("", item.hospital!!))
                            }
                            intent.getStringExtra("title")
                                .equals(ApiConstants.titleApneSathiConsulatant) -> {
                                contactList.add(
                                    ContactDummyData(
                                        item.consultantName!!,
                                        item.hospital!!
                                    )
                                )
                            }
                            intent.getStringExtra("title")
                                .equals(ApiConstants.titleCustomContact) -> {
                                contactList.add(ContactDummyData("", item.customeContact!!))
                            }
                            intent.getStringExtra("title")
                                .equals(ApiConstants.title108Ambulance) -> {
                                contactList.add(ContactDummyData("", item.ambulance!!))
                            }
                            intent.getStringExtra("title")
                                .equals(ApiConstants.titlecovidcontrolroom) -> {
                                contactList.add(ContactDummyData("", item.covidCtrlRoom!!))
                            }

                        }
                    }

                    if (contactList.isEmpty() || contactList.size < 0) {
                        Toast.makeText(this, "No Data Found", Toast.LENGTH_LONG).show()
                    } else {
                        contact_listadapter =
                            ContactListAdapter(
                                mContext,
                                contactList,
                                intent.getStringExtra("title"),
                                object :
                                    ContactListAdapter.ItemClickListener {
                                    override fun itemClick(data: ContactDummyData) {
                                        val intent = Intent(
                                            Intent.ACTION_CALL,
                                            Uri.parse("tel:" + data.contactnumber)
                                        )
                                        startActivity(intent)
                                    }


                                }
                            )
                        rvList.adapter = contact_listadapter
                    }


                }
            }
        })
    }


    override fun provideLayoutResource(): Int = R.layout.hospital_list_activity
}

