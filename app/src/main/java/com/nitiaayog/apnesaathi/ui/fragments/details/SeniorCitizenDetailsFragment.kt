package com.nitiaayog.apnesaathi.ui.fragments.details

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.SeniorCitizenDateAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.replaceFragment
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.DateItem
import com.nitiaayog.apnesaathi.model.SeniorCitizen
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform.SeniorCitizenFeedbackFormActivity
import com.nitiaayog.apnesaathi.utility.USER_DETAILS
import kotlinx.android.synthetic.main.activity_senior_citizen_feedback_form.*
import kotlinx.android.synthetic.main.fragment_senior_citizen_details.*


class SeniorCitizenDetailsFragment : BaseFragment<SeniorCitizenDetailsViewModel>(),
    SeniorCitizenDateAdapter.OnItemClickListener, SeniorCitizenEditFragment.OnItemClickListener {

    private lateinit var adapter: SeniorCitizenDateAdapter
    lateinit var callData: CallData
    override fun provideViewModel(): SeniorCitizenDetailsViewModel =
        getViewModel {
            SeniorCitizenDetailsViewModel.getInstance(dataManager)
        }

    override fun provideLayoutResource(): Int = R.layout.fragment_senior_citizen_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener { activity?.onBackPressed() }

        initClicks()
        initRecyclerView()
        initAdapter()
        bindData()

    }

    private fun bindData() {
        if (user.gender == "M") {
            img_user_icon.background =
                activity?.let { ContextCompat.getDrawable(it, R.drawable.ic_male_user) }
        } else {
            img_user_icon.background =
                activity?.let { ContextCompat.getDrawable(it, R.drawable.ic_female_user) }
        }

        user?.let {
            val address = getString(R.string.address).plus(" : ")
            val dataString = address.plus(it.block).plus(", ").plus(it.district)
                .plus(", ").plus(it.state)
            val spanAddress = SpannableString(dataString)
            spanAddress.setSpan(StyleSpan(Typeface.BOLD), 0, address.length, 0)
            spanAddress.setSpan(StyleSpan(Typeface.ITALIC), 0, address.length, 0)

            txt_user_name.text = it.userName.plus("(").plus(it.age).plus(" Yrs)")
            txt_user_phone_number.text = it.phoneNumber
            txt_address.text = spanAddress
        }
//       activity?.let { viewModel.getSeniorCitizenDetails(it) }  API call
    }

    private fun initClicks() {
        txt_more_details.setOnClickListener {
            if (cg_more_details_group.isVisible) {
                cg_more_details_group.visibility = View.GONE
                txt_more_details.text = getString(R.string.more_details)
                txt_more_details.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    activity?.let { it1 ->
                        ContextCompat.getDrawable(
                            it1,
                            R.drawable.ic_downward_arrow
                        )
                    },
                    null
                )
                txt_more_details.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            } else {
                cg_more_details_group.visibility = View.VISIBLE
                txt_more_details.text = getString(R.string.less_details)
                txt_more_details.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    activity?.let { it1 ->
                        ContextCompat.getDrawable(
                            it1,
                            R.drawable.ic_upward_arrow
                        )
                    },
                    null
                )
                txt_more_details.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            }
        }
        img_call_button.setOnClickListener { prepareToCallPerson() }
        txt_edit.setOnClickListener {
            val intent = Intent(activity, SeniorCitizenFeedbackFormActivity::class.java)
            intent.putExtra(USER_DETAILS, user)
            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        val layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rcl_call_dates.layoutManager = layoutManager
    }

    private fun initAdapter() {
        adapter = SeniorCitizenDateAdapter(viewModel.prepareData())
        adapter.setOnItemClickListener(this)
        rcl_call_dates.adapter = adapter
    }

    override fun onItemClick(position: Int, dateItem: DateItem) {
        adapter.notifyDataSetChanged()
        if (dateItem.status == "Attended") {
            cl_uneditable_container.visibility = View.VISIBLE
            ll_status_container.visibility = View.GONE
            txt_edit.visibility = View.GONE
        } else {
            txt_edit.visibility = View.VISIBLE
            cl_uneditable_container.visibility = View.GONE
            ll_status_container.visibility = View.VISIBLE
        }
    }

    override fun onCallPermissionGranted() {
        placeCall(callData, R.id.fragment_edit_container)
    }

    override fun onCallPermissionDenied() =
        Toast.makeText(context, R.string.not_handle_action, Toast.LENGTH_LONG).show()

    override fun onSaveButton(status: String) {
        val index = viewModel.getDataList().size - 1
        viewModel.getDataList().get(index).status = status
        if (status == "Attended") {
            cl_uneditable_container.visibility = View.VISIBLE
            ll_status_container.visibility = View.GONE
            txt_edit.visibility = View.GONE
        }

    }

    private fun observeData() = viewModel.getDataObserver().observe(this, Observer {
        when (it) {
            is NetworkRequestState.NetworkNotAvailable -> {

            }
            is NetworkRequestState.LoadingData -> {

            }
            is NetworkRequestState.ErrorResponse -> {

            }
            is NetworkRequestState.SuccessResponse<*> -> {

            }
        }
    })

    override fun onCancelButton() {
    }

    fun setSelectedUser(callData: CallData) {
        this.callData = callData
    }
}
