package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.about

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.CallsAdapter
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.CallDetails
import com.nitiaayog.apnesaathi.model.Volunteer
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_admin_staff_about_volunteer.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class AboutVolunteerFragment : BaseFragment<AboutVolunteerViewModel>() {

    companion object {
        fun getInstance(volunteer: Volunteer): AboutVolunteerFragment {
            val fragment = AboutVolunteerFragment()
            fragment.setVolunteer(volunteer)
            return fragment
        }
    }

    private var volunteer: Volunteer? = null

    private val srCitizensAdapter: CallsAdapter by lazy { setAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        volunteer?.run {
            Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe { manageDataStream() }
                .autoDispose(disposables)

            ivCall.throttleClick().subscribe { prepareToCallPerson() }.autoDispose(disposables)

            setupRecyclerView()
            setVolunteerData()
        }
    }

    override fun provideViewModel(): AboutVolunteerViewModel {
        return AboutVolunteerViewModel.getInstance(dataManager, volunteer!!.id!!)
    }

    override fun provideLayoutResource(): Int {
        return R.layout.fragment_admin_staff_about_volunteer
    }

    override fun onCallPermissionGranted() {
        initiateCall(volunteer!!.phoneNumber!!)
    }

    override fun onCallPermissionDenied() {
        Snackbar.make(constraintLayout, R.string.not_handle_action, Snackbar.LENGTH_LONG).show()
    }

    private fun setVolunteerData() {
        tvVolunteerName.text = volunteer!!.firstName.plus(" ").plus(volunteer!!.lastName)
        tvVolunteerPhoneNumber.text = volunteer!!.phoneNumber
        setAddress()
        setJoinDate()
        civGender.setImageResource(
            if (volunteer!!.gender!!.toLowerCase(Locale.ENGLISH) == "m") R.drawable.ic_volunteer_male
            else R.drawable.ic_volunteer_female
        )
    }

    private fun setAddress() {
        val address = getString(R.string.address).plus(" : ")
        val dataString = address.plus(volunteer!!.address).plus(", ")
            .plus(volunteer!!.block).plus(", ").plus(volunteer!!.district).plus(", ")
            .plus(volunteer!!.state)
        tvVolunteerAddress.text = getBoldItalicText(
            dataString, 0, address.length, 0, address.length
        )
    }

    private fun setJoinDate() {
        val joiningDate = getString(R.string.joining_date).plus(" : ")
        val dataString = joiningDate.plus(volunteer!!.joiningDate)
        tvAssignmentScore.text = getBoldItalicText(
            dataString, 0, joiningDate.length, 0, joiningDate.length
        )
    }

    private fun getBoldItalicText(
        dataText: String, boldStartIndex: Int, boldEndIndex: Int, italicStartIndex: Int,
        italicEndIndex: Int
    ): SpannableString {
        val spanText = SpannableString(dataText)
        spanText.setSpan(StyleSpan(Typeface.BOLD), boldStartIndex, boldEndIndex, 0)
        spanText.setSpan(StyleSpan(Typeface.ITALIC), italicStartIndex, italicEndIndex, 0)
        return spanText
    }

    private fun setupRecyclerView() {
        rvList.isNestedScrollingEnabled = false
        rvList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvList.adapter = srCitizensAdapter
        val padding = resources.getDimensionPixelOffset(R.dimen.dimen_5)
        rvList.setPadding(padding, 0, padding, 0)
    }

    private fun setAdapter(): CallsAdapter {
        return CallsAdapter(dataManager.getRole()).apply {
            setOnItemClickListener(object : OnItemClickListener<CallData> {
                override fun onItemClick(position: Int, data: CallData) {

                }

                override fun onMoreInfoClick(position: Int, data: CallData) {

                }
            })
        }
    }

    private fun manageProgressBarData() {
        val pendingCalls = viewModel.getPendingCalls().size
        val followUpCalls = viewModel.getFollowupCalls().size
        val completedCalls = viewModel.getCompletedCalls().size
        val invalidCalls = viewModel.getInvalidCalls().size
        val totalCalls: Int = pendingCalls + followUpCalls + completedCalls + invalidCalls
        tvTotal.text = getString(R.string.total).plus(" ").plus(totalCalls)

        val completedPer: Int = ((completedCalls.toDouble() / totalCalls.toDouble()) * 100).toInt()
        val followUpPer: Int =
            ((followUpCalls.toDouble() / totalCalls.toDouble()) * 100).toInt() + completedPer

        progressSummery.progress = completedPer
        progressSummery.secondaryProgress = followUpPer

        tv_completed.text = getString(R.string.completed_count).plus(completedCalls)
        tv_need_followup.text = getString(R.string.need_follow_up_count).plus(followUpCalls)
        tv_pending.text = getString(R.string.pending_g_count).plus(pendingCalls)
        tvInvalidCalls.text = getString(R.string.invalid_count).plus(invalidCalls)
    }

    private fun manageDataStream() {
        lifecycleScope.launch {
            getObservableDataStream()
            viewModel.getVolunteerDetails(requireContext())
        }
    }

    private fun manageNetworkState(state: NetworkRequestState) {
        when (state) {
            is NetworkRequestState.NetworkNotAvailable -> BaseUtility.showAlertMessage(
                requireContext(), R.string.error, R.string.check_internet
            )
            is NetworkRequestState.LoadingData -> progressBarSrCitizens.visibility = View.VISIBLE
            is NetworkRequestState.Error -> {
                progressBarSrCitizens.visibility = View.GONE
                BaseUtility.showAlertMessage(
                    requireContext(), R.string.error, R.string.something_went_wrong
                )
            }
            is NetworkRequestState.ErrorResponse -> {
                progressBarSrCitizens.visibility = View.GONE
                BaseUtility.showAlertMessage(
                    requireContext(), getString(R.string.error),
                    getString(R.string.cannt_connect_to_server_try_later), getString(R.string.okay)
                )
            }
            is NetworkRequestState.SuccessResponse<*> -> {
                progressBarSrCitizens.visibility = View.GONE
                if (state.data is CallDetails) {
                    manageProgressBarData()
                    tvSrCitizens.text = getString(
                        R.string.senior_citizen_list_count, viewModel.getCalls().size.toString()
                    )
                    srCitizensAdapter.setData(state.data.callsList)
                }
            }
        }
    }

    private fun getObservableDataStream() {
        viewModel.getNetworkStream().removeObservers(viewLifecycleOwner)
        viewModel.getNetworkStream().observe(viewLifecycleOwner, Observer {
            manageNetworkState(it)
        })
    }

    private fun setVolunteer(volunteer: Volunteer) {
        this.volunteer = volunteer
    }
}