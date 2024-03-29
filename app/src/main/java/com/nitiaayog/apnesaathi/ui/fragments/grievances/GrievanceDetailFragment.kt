package com.nitiaayog.apnesaathi.ui.fragments.grievances

import android.graphics.Paint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.BaseArrayAdapter
import com.nitiaayog.apnesaathi.base.ProgressDialog
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.interfaces.ReloadApiRequiredListener
import com.nitiaayog.apnesaathi.model.GrievanceData
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.utility.BaseUtility
import kotlinx.android.synthetic.main.fragment_grievance_details.*
import kotlinx.android.synthetic.main.list_item_grievance_status.*
import kotlinx.android.synthetic.main.updated_progress_layout.view.*
import org.threeten.bp.format.DateTimeFormatter

/**
 * Fragment for showing the detailed view of grievances!
 * [grievanceData] is the real data which is fetched form the API and it's used to bind the values into view.
 * [BaseFragment] is the base fragment with functions that are common in all the fragments
 * [GrievanceDetailsViewModel] is the view model for performing fetching data from API, caching it in data base and fetching the data back from database
 */
class GrievanceDetailFragment(private val grievanceData: GrievanceData) :
    BaseFragment<GrievanceDetailsViewModel>() {

    private lateinit var reloadApiRequiredListener: ReloadApiRequiredListener
    private var selectedStatus: String = ""
    private val progressDialog: ProgressDialog.Builder by lazy {
        ProgressDialog.Builder(context!!).setMessage(R.string.wait_saving_data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener { activity?.onBackPressed() }
        bindData()
        getDataStream()
    }
    /**
     * Method for fetching the data stream.
     * This method also handles all the network issues.
     */
    private fun getDataStream() {
        viewModel.getDataStream().removeObservers(viewLifecycleOwner)
        viewModel.getDataStream().observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable -> {
                    progressDialog.dismiss()
                    BaseUtility.showAlertMessage(
                        context!!, R.string.error, R.string.api_connection_error
                    )
                }

                is NetworkRequestState.LoadingData -> {
                    progressDialog.show()
                }
                is NetworkRequestState.ErrorResponse -> {
                    progressDialog.dismiss()
                    BaseUtility.showAlertMessage(
                        context!!, R.string.error, R.string.api_connection_error
                    )
                }
                is NetworkRequestState.SuccessResponse<*> -> {
                    progressDialog.dismiss()
                    reloadApiRequiredListener.onReloadRequired()
                    activity!!.onBackPressed()
                }
            }
        })
    }
    /**
     * Method for binding the data using the data that was fetched from the API
     */
    private fun bindData() {
        tv_grievance_type.text = grievanceData.grievanceType
        val priorityText = grievanceData.priority
        val priority = context?.getString(R.string.priority).plus(": ").plus(priorityText)
        val spanPriority = SpannableString(priority)
        spanPriority.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.colorAccent)),
            spanPriority.length - priorityText!!.length,
            spanPriority.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        img_call_button.setOnClickListener { prepareToCallPerson() }
        tv_priority.text = spanPriority
        if (grievanceData.status == "UNDER REVIEW") {
            tv_volunteer_desc2.text = getString(R.string.in_progress_remarks).plus(" ")
                .plus(grievanceData.reviewRemarks)
            cgInProgress.visibility = View.VISIBLE
        } else {
            cgInProgress.visibility = View.GONE
        }
        if (grievanceData.status == "RESOLVED") {
            tv_update_status.text = getString(R.string.reopen)
            tv_volunteer_desc3.text =
                getString(R.string.resolvd_remarks).plus(" ").plus(grievanceData.resolvedRemarks)
            cgResolved.visibility = View.VISIBLE
            if (!grievanceData.reviewRemarks.isNullOrEmpty()) {
                tv_volunteer_desc2.text = getString(R.string.in_progress_remarks).plus(" ")
                    .plus(grievanceData.reviewRemarks)
                cgInProgress.visibility = View.VISIBLE
            }
        } else {
            cgResolved.visibility = View.GONE
        }
        tv_volunteer_desc.text =
            getString(R.string.raised_on_remarks).plus(" ")
                .plus(getFormattedDate(grievanceData.createdDate))
        tv_description.text = grievanceData.description
        tv_issue_id.text = grievanceData.trackingId.toString()
        tv_created_date.text = getFormattedDate(grievanceData.createdDate)
        tv_update_on.text = getFormattedDate(grievanceData.lastUpdateOn)
        tv_name.text = grievanceData.srCitizenName
        tv_name.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        if (grievanceData.gender == "M") {
            img_gender.background = ContextCompat.getDrawable(context!!, R.drawable.ic_male_user)
        } else {
            img_gender.background = ContextCompat.getDrawable(context!!, R.drawable.ic_female_user)
        }
        if (dataManager.getGender() == "M") {
            img_user_icon.background =
                ContextCompat.getDrawable(context!!, R.drawable.ic_volunteer_male)
            img_user_icon2.background =
                ContextCompat.getDrawable(context!!, R.drawable.ic_volunteer_male)
            img_user_icon3.background =
                ContextCompat.getDrawable(context!!, R.drawable.ic_volunteer_male)
        } else {
            img_user_icon.background =
                ContextCompat.getDrawable(context!!, R.drawable.ic_volunteer_female)
            img_user_icon2.background =
                ContextCompat.getDrawable(context!!, R.drawable.ic_volunteer_female)
            img_user_icon3.background =
                ContextCompat.getDrawable(context!!, R.drawable.ic_volunteer_female)
        }
        tv_volunteer_name.text = dataManager.getFirstName()
        tv_volunteer_name2.text = dataManager.getFirstName()
        tv_volunteer_name3.text = dataManager.getFirstName()

        tv_volunteer_name.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        tv_volunteer_name2.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        tv_volunteer_name3.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        tv_update_status.setOnClickListener {
            showBottomSheetDialogFragment()
        }
    }
    /**
     * Method for showing the popup in which we are showing the UI for updating the grievances
     * Also handles the update status click and based on the status selected update the server with corresponding status.
     */
    private fun showBottomSheetDialogFragment() {
        val view = layoutInflater.inflate(R.layout.updated_progress_layout, null)
        val dialog = BottomSheetDialog(activity!!)
        dialog.setContentView(view)
        val grievanceStatusList = resources.getStringArray(R.array.grievance_status_array)
        val grievanceAdapter =
            BaseArrayAdapter(activity!!, R.layout.item_layout_dropdown_menu, grievanceStatusList)
        view.actGrievanceStatus.threshold = 0
        view.actGrievanceStatus.setAdapter(grievanceAdapter)
        view.actGrievanceStatus.setOnKeyListener(null)
        view.actGrievanceStatus.setOnItemClickListener { _, _, position, _ ->
            selectedStatus = grievanceStatusList[position]
        }
        view.actGrievanceStatus.throttleClick().subscribe { view.actGrievanceStatus.showDropDown() }
            .autoDispose(disposables)
        view.txtProgressCancel.setOnClickListener { dialog.dismiss() }
        view.txtProgressUpdate.throttleClick()
            .subscribe {
                val edt = view.edtDescription.text.toString()
                if (selectedStatus.isNotEmpty()) {
                    if (edt.isNotEmpty()) {
                        updateStatus(edt)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            getString(R.string.enter_description_warning),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    Toast.makeText(context, getString(R.string.status_warning), Toast.LENGTH_SHORT)
                        .show()
                }

            }
            .autoDispose(disposables)


        dialog.show()
    }
    /**
     * Method for updating the grievance status which was selected bt the user.
     * This will make a call to the server
     */
    private fun updateStatus(desc: String) {
        viewModel.updateGrievance(
            context!!,
            grievanceData.trackingId ?: -1,
            selectedStatus,
            desc,
            grievanceData.grievanceType ?: ""
        )
    }

    /**
     * Method for getting the formatted date
     * [date] is a date which is required to be converted in the required format
     */
    private fun getFormattedDate(date: String?): String {
        if (date.isNullOrEmpty()) return ""
        val input = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val output = DateTimeFormatter.ofPattern("MMM dd,yyyy")
        val fa = input.parse(date)
        return output.format(fa)
    }

    override fun provideViewModel(): GrievanceDetailsViewModel =
        getViewModel { GrievanceDetailsViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_grievance_details

    override fun onCallPermissionGranted() {
        grievanceData.srPhoneNumber?.let { initiateCall(it) }
    }

    override fun onCallPermissionDenied() {
        Toast.makeText(requireContext(), R.string.not_handle_action, Toast.LENGTH_LONG).show()
    }
    /**
     * Method for setting the listener for reload API
     * [ReloadApiRequiredListener] is listener for checking if reload is required
     */
    fun setReloadApiListener(reloadApiRequiredListener: ReloadApiRequiredListener) {
        this.reloadApiRequiredListener = reloadApiRequiredListener
    }
}