package com.nitiaayog.apnesaathi.ui.emergencycontact.contact_data

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.ProgressDialog
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.emergencycontact.EmergencyContactResponse
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.emergencycontact.adapter.ContactListAdapter
import com.nitiaayog.apnesaathi.ui.emergencycontact.adapter.ContactRealData
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.contact_data_activity.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.util.concurrent.TimeUnit


class ContactDataActivity : BaseActivity<ContactDataViewModel>() {
    val PERMISSION_CODE: Int = 200
    private val contactListAdapter: ContactListAdapter by lazy {
        ContactListAdapter(this).apply {
            this.setOnItemClickListener(object : ContactListAdapter.ItemClickListener {
                override fun itemClick(data: ContactRealData) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (PermissionChecker.checkSelfPermission(
                                this@ContactDataActivity,
                                Manifest.permission.CALL_PHONE
                            ) == PermissionChecker.PERMISSION_DENIED
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
            })
        }
    }
    lateinit var contactList: ArrayList<ContactRealData>
    override fun provideViewModel(): ContactDataViewModel {
        return ContactDataViewModel.getInstance(dataManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.title = intent.getStringExtra("Toolbar_title")
        toolBar.setNavigationOnClickListener {
            finish()
        }
        bindView()
        initClicks()

    }

    private fun initClicks() {
        actDistrict.throttleClick().subscribe {
            if (actState.text.toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.select_a_state), Toast.LENGTH_SHORT).show()
            } else {
                actDistrict.showDropDown()
                updateDropDownIndicator(actDistrict, R.drawable.ic_arrow_up)
            }
        }.autoDispose(disposables)

        actState.throttleClick().subscribe {
            actState.showDropDown()
            updateDropDownIndicator(actState, R.drawable.ic_arrow_up)
        }.autoDispose(disposables)
    }

    private val progressDialog: ProgressDialog.Builder by lazy {
        ProgressDialog.Builder(this).setMessage(R.string.wait_for_emergency_data)
    }

    private fun bindView() {
        contactList = ArrayList()
        observeStates()
        val stateList = resources.getStringArray(R.array.states_array)
        val stateAdapter = ArrayAdapter(this, R.layout.item_layout_dropdown_menu, stateList)
        actState.threshold = 0
        actState.setAdapter(stateAdapter)
        actState.setOnKeyListener(null)
        actState.setOnDismissListener {
            updateDropDownIndicator(actState, R.drawable.ic_arrow_down)
        }
        actState.setOnItemClickListener { _, _, position, _ ->
            actDistrict.setText("")
            actDistrict.hint = getString(R.string.district)
            setDistrictAdapter(position)
        }
        rvList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvList.adapter = contactListAdapter
    }

    //Method for setting the district adapter
    private fun setDistrictAdapter(position: Int) {
        var districtsList = arrayOf<String>()
        when (position) {
            0 -> districtsList = resources.getStringArray(R.array.district0)
            1 -> districtsList = resources.getStringArray(R.array.district1)
            2 -> districtsList = resources.getStringArray(R.array.district2)
            3 -> districtsList = resources.getStringArray(R.array.district3)
            4 -> districtsList = resources.getStringArray(R.array.district4)
            5 -> districtsList = resources.getStringArray(R.array.district5)
            6 -> districtsList = resources.getStringArray(R.array.district6)
            7 -> districtsList = resources.getStringArray(R.array.district7)
        }
        val adapter = ArrayAdapter(this, R.layout.item_layout_dropdown_menu, districtsList)
        actDistrict.threshold = 0
        actDistrict.setAdapter(adapter)
        actDistrict.setOnKeyListener(null)
        actDistrict.setOnDismissListener {
            updateDropDownIndicator(actDistrict, R.drawable.ic_arrow_down)
        }
        actDistrict.setOnItemClickListener { _, _, pos, _ ->
            contactList.clear()
            contactListAdapter.notifyDataSetChanged()
            getEmergencyContactList(districtsList[pos])
        }
    }

    //Method for getting the emergency contact list
    private fun getEmergencyContactList(districtName: String) {
        try {
            Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    viewModel.callEmergencyDataApi(
                        this,
                        districtName
                    )
                }.autoDispose(disposables)
        } catch (e: Exception) {
            println("TAG -- MyData --> ${e.message}")
        }
    }

    private fun updateDropDownIndicator(autoCompleteTextView: AutoCompleteTextView, icon: Int) =
        autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)

    private fun observeStates() {
        viewModel.getDataObserver().removeObservers(this)
        viewModel.getDataObserver().observe(this, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable -> {
                    BaseUtility.showAlertMessage(
                        this,
                        R.string.alert,
                        R.string.check_internet
                    )
                }
                is NetworkRequestState.ErrorResponse -> {
                    progressDialog.dismiss()
                    BaseUtility.showAlertMessage(
                        this,
                        getString(R.string.error),
                        getString(R.string.cannt_connect_to_server_try_later),
                        getString(R.string.okay)
                    )
                }
                is NetworkRequestState.LoadingData -> {
                    progressDialog.show()
                }
                is NetworkRequestState.Error -> {
                    progressDialog.dismiss()
                    BaseUtility.showAlertMessage(
                        this,
                        getString(R.string.error),
                        getString(R.string.emergency_contact_not_available),
                        getString(R.string.okay)
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
                                    contactList.add(ContactRealData(item.policeRegion!!, item.police!!))
                                }
                                intent.getStringExtra("title")
                                    .equals(ApiConstants.titleHospital) -> {
                                    contactList.add(ContactRealData(item.hospitalName!!, item.hospital!!))
                                }
                                intent.getStringExtra("title")
                                    .equals(ApiConstants.titleApneSathiConsulatant) -> {
                                    contactList.add(
                                        ContactRealData(
                                            item.consultantName!!,
                                            item.apnisathiContact!!
                                        )
                                    )
                                }
                                intent.getStringExtra("title")
                                    .equals(ApiConstants.titleCustomContact) -> {
                                    contactList.add(ContactRealData(item.contactName!!, item.customeContact!!))
                                }
                                intent.getStringExtra("title")
                                    .equals(ApiConstants.title108Ambulance) -> {
                                    contactList.add(ContactRealData(item.hospitalName!!, item.ambulance!!))
                                }
                                intent.getStringExtra("title")
                                    .equals(ApiConstants.titlecovidcontrolroom) -> {
                                    contactList.add(ContactRealData(item.ctrlRoomRegion!!, item.covidCtrlRoom!!))
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
                        contactListAdapter.setData(contactList)
                        contactListAdapter.notifyDataSetChanged()
                    }

                }
            }
        })
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
                            Uri.fromParts("package", this@ContactDataActivity.packageName, null)
                    }
                    startActivityForResult(intent, BaseFragment.CONST_PERMISSION_FROM_SETTINGS)
                }
                this.setNegativeButton(R.string.not_now) { dialog, _ -> dialog.dismiss() }
            }.create()
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.color_orange))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.color_orange))
    }

    override fun provideLayoutResource(): Int = R.layout.contact_data_activity
}

