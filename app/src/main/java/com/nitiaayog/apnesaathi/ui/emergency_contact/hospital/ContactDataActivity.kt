package com.nitiaayog.apnesaathi.ui.emergency_contact.hospital

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
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
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.emergency_contact.adapter.ContactRealData
import com.nitiaayog.apnesaathi.ui.emergency_contact.adapter.ContactListAdapter
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import com.nitiaayog.apnesaathi.utility.ROLE_MASTER_ADMIN
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.hospital_list_activity.*
import kotlinx.android.synthetic.main.include_toolbar.toolBar
import java.util.concurrent.TimeUnit


class ContactDataActivity : BaseActivity<ContactDataViewModel>() {
    val PERMISSION_CODE: Int = 200
    lateinit var mContext: Context
    lateinit var contact_listadapter: ContactListAdapter
    var districtNames = mutableListOf<String>()
    lateinit var contactList: ArrayList<ContactRealData>
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
                    progressDialog.dismiss()

                    BaseUtility.showAlertMessage(
                        this, getString(R.string.error),
                        getString(R.string.cannt_connect_to_server_try_later), getString(R.string.okay)
                    )
                }
                is NetworkRequestState.SuccessResponse<*> -> {
                    progressDialog.dismiss()
                    val loginResponse = it.data as EmergencyContactResponse
                    contactList.clear()
                    if (loginResponse.emergencyContactsList!!.isNotEmpty()) {
                        for (item in loginResponse.emergencyContactsList!!) {
                            when {
                                intent.getStringExtra("title")
                                    .equals(ApiConstants.titlePoliceStation) -> {
                                    contactList.add(ContactRealData("", item.police!!))
                                }
                                intent.getStringExtra("title")
                                    .equals(ApiConstants.titleHospital) -> {
                                    contactList.add(ContactRealData("", item.hospital!!))
                                }
                                intent.getStringExtra("title")
                                    .equals(ApiConstants.titleApneSathiConsulatant) -> {
                                    contactList.add(
                                        ContactRealData(
                                            item.consultantName!!,
                                            item.hospital!!
                                        )
                                    )
                                }
                                intent.getStringExtra("title")
                                    .equals(ApiConstants.titleCustomContact) -> {
                                    contactList.add(ContactRealData("", item.customeContact!!))
                                }
                                intent.getStringExtra("title")
                                    .equals(ApiConstants.title108Ambulance) -> {
                                    contactList.add(ContactRealData("", item.ambulance!!))
                                }
                                intent.getStringExtra("title")
                                    .equals(ApiConstants.titlecovidcontrolroom) -> {
                                    contactList.add(ContactRealData("", item.covidCtrlRoom!!))
                                }

                            }
                        }
                    } else {
                        rvList.visibility = GONE
                        TxtNodata.visibility = VISIBLE
                        TxtNodata.text = loginResponse.message
                    }

                    if (contactList.isEmpty() || contactList.size < 0) {
                        rvList.visibility = GONE
                        TxtNodata.visibility = VISIBLE
                        TxtNodata.text = loginResponse.message

                    } else {
                        rvList.visibility = VISIBLE
                        setadapter(contactList)
                    }


                }
            }
        })
    }

    private fun setadapter(contactList: ArrayList<ContactRealData>) {
        contact_listadapter =
            ContactListAdapter(
                mContext,
                contactList,
                intent.getStringExtra("title"),
                object :
                    ContactListAdapter.ItemClickListener {
                    override fun itemClick(data: ContactRealData) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (PermissionChecker.checkSelfPermission(
                                    this@ContactDataActivity!!,
                                    Manifest.permission.CALL_PHONE
                                ) == PackageManager.PERMISSION_DENIED
                            ) {
                                val permission =
                                    arrayOf(Manifest.permission.CALL_PHONE)
                                requestPermissions(permission, PERMISSION_CODE)
                            } else {
                                initiateCall(data)
                            }
                        } else {
                            initiateCall(data)
                        }


                    }


                }
            )
        rvList.adapter = contact_listadapter
    }

    private fun initiateCall(data: ContactRealData) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + data.contactnumber))
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    phoneCallPermissionTextPopup(R.string.phoneCallpermission_text)
                }
            }
        }
    }

    private fun phoneCallPermissionTextPopup(@StringRes message: Int) {
        val dialog = AlertDialog.Builder(this, R.style.Theme_AlertDialog)
            .setTitle(R.string.permission_detail).apply {
                this.setMessage(message)
                this.setPositiveButton(R.string.accept) { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data =
                            Uri.fromParts("package", this@ContactDataActivity!!.packageName, null)
                    }
                    startActivityForResult(intent, BaseFragment.CONST_PERMISSION_FROM_SETTINGS)
                }
                this.setNegativeButton(R.string.not_now) { dialog, _ -> dialog.dismiss() }
            }.create()
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this!!, R.color.color_orange))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this!!, R.color.color_orange))
    }

    override fun provideLayoutResource(): Int = R.layout.hospital_list_activity
}

