package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.CallsAdapter
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.emergency_contact.MainEmergency_Contact_Activity
import com.nitiaayog.apnesaathi.ui.fragments.details.SeniorCitizenDetailsFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.BaseCallsTypeFragment
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import com.nitiaayog.apnesaathi.utility.SR_CITIZEN_DETAIL_FRAGMENT
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_calls_status.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class HomeFragment : BaseFragment<HomeViewModel>() {

    private var lastSelectedItemPosition: Int = 0
    private var lastSelectedItem: CallData? = null

    private val pendingAdapter: CallsAdapter by lazy { setupPendingCallsAdapter() }
    private val followupAdapter: CallsAdapter by lazy { setupFollowupCallsAdapter() }
    private val completedAdapter: CallsAdapter by lazy { setupCompletedCallsAdapter() }
    private val invalidAdapter: CallsAdapter by lazy { setupInvalidCallsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.title = getString(R.string.dashboard)
        toolBar.inflateMenu(R.menu.emergency_contact_menu)
        toolBar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
        Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                getObservableStreams()
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.getCallDetails(requireContext())
                    viewModel.getGrievanceTrackingList(requireContext())
                }
            }
            .autoDispose(disposables)

        if (dataManager.getRole() != "3") {
            setupPendingCalls()
            setupFollowupCalls()
            setupCompletedCalls()
            setupInvalidCalls()
        }
    }

    override fun provideViewModel(): HomeViewModel {
        return HomeViewModel.getInstance(requireContext(), dataManager)
    }

    override fun provideLayoutResource(): Int {
        return R.layout.fragment_admin_staff_home
    }

    override fun onCallPermissionGranted() {
        lastSelectedItem?.run {
            placeCall(this)
        }
    }

    override fun onCallPermissionDenied() {
        //Snackbar.make(bottomNavigationView, R.string.not_handle_action, Snackbar.LENGTH_LONG).show()
        Toast.makeText(requireContext(), R.string.not_handle_action, Toast.LENGTH_LONG).show()
    }

    private fun setupPendingCalls() {
        val rvPendingList = (rvPendingList as RecyclerView)
        rvPendingList.isNestedScrollingEnabled = false
        rvPendingList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvPendingList.adapter = pendingAdapter
    }

    private fun setupPendingCallsAdapter(): CallsAdapter {
        return CallsAdapter(true).apply {
            this.setOnItemClickListener(object : OnItemClickListener<CallData> {
                override fun onItemClick(position: Int, data: CallData) {
                    lastSelectedItemPosition = position
                    lastSelectedItem = data
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, data: CallData) {
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(data, true)
                    addFragment(
                        R.id.fragmentAdminStaffHomeContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT
                    )
                }
            })
        }
    }

    private fun setupFollowupCalls() {
        val rvFollowupList = (rvFollowupList as RecyclerView)
        rvFollowupList.isNestedScrollingEnabled = false
        rvFollowupList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvFollowupList.adapter = followupAdapter
    }

    private fun setupFollowupCallsAdapter(): CallsAdapter {
        return CallsAdapter(true).apply {
            this.setOnItemClickListener(object : OnItemClickListener<CallData> {
                override fun onItemClick(position: Int, data: CallData) {
                    //lastSelectedItemId = data.callId!!
                    lastSelectedItemPosition = position
                    lastSelectedItem = data
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, data: CallData) {
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(data, true)
                    addFragment(
                        R.id.fragmentAdminStaffHomeContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT
                    )
                }
            })
        }
    }

    private fun setupCompletedCalls() {
        val rvAttendedList = (rvAttendedList as RecyclerView)
        rvAttendedList.isNestedScrollingEnabled = false
        rvAttendedList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvAttendedList.adapter = completedAdapter
    }

    private fun setupCompletedCallsAdapter(): CallsAdapter {
        return CallsAdapter(true).apply {
            this.setOnItemClickListener(object : OnItemClickListener<CallData> {
                override fun onItemClick(position: Int, data: CallData) {
                    // lastSelectedItemId = data.callId!!
                    lastSelectedItemPosition = position
                    lastSelectedItem = data
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, data: CallData) {
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(data, true)
                    addFragment(
                        R.id.fragmentAdminStaffHomeContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT
                    )
                }
            })
        }
    }

    private fun setupInvalidCalls() {
        val rvInvalidCallsList = (rvInvalidCallList as RecyclerView)
        rvInvalidCallsList.isNestedScrollingEnabled = false
        rvInvalidCallsList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvInvalidCallsList.adapter = invalidAdapter
    }

    private fun setupInvalidCallsAdapter(): CallsAdapter {
        return CallsAdapter(true).apply {
            this.setOnItemClickListener(object : OnItemClickListener<CallData> {
                override fun onItemClick(position: Int, data: CallData) {
                    //lastSelectedItemId = data.callId!!
                    lastSelectedItemPosition = position
                    lastSelectedItem = data
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, data: CallData) {
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(data, true)
                    addFragment(
                        R.id.fragmentAdminStaffHomeContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT
                    )
                }
            })
        }
    }

    private fun manageCalls(itemCount: Int, @StringRes callsCountString: Int) {
        when (callsCountString) {
            R.string.pending_calls_count ->
                manageViews(callsCountString, tvPendingCalls, btnSeeAllPending, itemCount)
            R.string.follow_up_count ->
                manageViews(callsCountString, tvFollowup, btnSeeAllFollowup, itemCount)
            R.string.attended_calls_count ->
                manageViews(callsCountString, tvAttended, btnSeeAllAttended, itemCount)
            R.string.invalid_calls_count ->
                manageViews(callsCountString, tvInvalidCalls, btnSeeAllInvalid, itemCount)
            else -> {
            }
        }
    }

    private fun manageViews(
        @StringRes callsCount: Int, textView: TextView, button: Button, size: Int
    ) {
        textView.text = getString(callsCount, size.toString())
        button.visibility = if (size > 3) {
            button.setOnClickListener {
                val data = Bundle()
                val type = getString(
                    when (callsCount) {
                        R.string.pending_calls_count -> R.string.pending_calls
                        R.string.follow_up_count -> R.string.follow_up
                        R.string.attended_calls_count -> R.string.attended_calls
                        R.string.invalid_calls_count -> R.string.invalid_calls
                        else -> 0
                    }
                )
                data.putString(BaseCallsTypeFragment.TYPE_OF_DATA, type)
                data.putInt(
                    BaseCallsTypeFragment.CONTAINER_ID, R.id.fragmentAdminStaffHomeContainer
                )
                val fragment = BaseCallsTypeFragment()
                fragment.arguments = data
                addFragment(R.id.fragmentAdminStaffHomeContainer, fragment, type)
            }
            View.VISIBLE
        } else View.GONE
    }

    private fun handleNetwork(state: NetworkRequestState) {
        when (state) {
            is NetworkRequestState.NetworkNotAvailable -> {
                if (state.apiName == ApiProvider.ApiGrievanceTracking)
                    BaseUtility.showAlertMessage(
                        requireContext(), R.string.error, R.string.check_internet
                    )
            }
            is NetworkRequestState.LoadingData -> {
                when (state.apiName) {
                    ApiProvider.ApiLoadDashboard -> {

                    }
                }
                // progressBarVolunteers.visibility = View.VISIBLE
            }
            is NetworkRequestState.ErrorResponse -> {
                //progressBarVolunteers.visibility = View.GONE
                if (state.apiName == ApiProvider.ApiLoadDashboard)
                    BaseUtility.showAlertMessage(
                        requireContext(), getString(R.string.error), state.throwable?.message
                            ?: getString(R.string.cannt_connect_to_server_try_later),
                        getString(R.string.okay)
                    )
            }
            is NetworkRequestState.Error -> {
                //progressBarVolunteers.visibility = View.GONE
                BaseUtility.showAlertMessage(
                    requireContext(), R.string.error, R.string.something_went_wrong
                )
            }
            is NetworkRequestState.SuccessResponse<*> -> {/*progressBarVolunteers.visibility =
                View.GONE*/
            }
        }
    }

    private fun observeNetwork() {
        viewModel.getNetworkStream().removeObservers(viewLifecycleOwner)
        viewModel.getNetworkStream().observe(viewLifecycleOwner, { handleNetwork(it) })
    }

    private fun observePendingCalls() {
        viewModel.getPendingCalls().removeObservers(viewLifecycleOwner)
        viewModel.getPendingCalls().observe(viewLifecycleOwner, {
            pendingAdapter.setData(if (it.size > 3) it.subList(0, 3) else it)
            pendingAdapter.hideDate(true)
            pendingAdapter.notifyDataSetChanged()
            manageCalls(it.size, R.string.pending_calls_count)
        })
    }

    private fun observeFollowupCalls() {
        viewModel.getFollowupCalls().removeObservers(viewLifecycleOwner)
        viewModel.getFollowupCalls().observe(viewLifecycleOwner, {
            followupAdapter.setData(if (it.size > 3) it.subList(0, 3) else it)
            followupAdapter.notifyDataSetChanged()
            manageCalls(it.size, R.string.follow_up_count)
        })
    }

    private fun observeCompletedCalls() {
        viewModel.getCompletedCalls().removeObservers(viewLifecycleOwner)
        viewModel.getCompletedCalls().observe(viewLifecycleOwner, Observer {
            completedAdapter.setData(if (it.size > 3) it.subList(0, 3) else it)
            completedAdapter.notifyDataSetChanged()
            manageCalls(it.size, R.string.attended_calls_count)
        })
    }

    private fun observeInvalidCalls() {
        viewModel.getInvalidCalls().removeObservers(viewLifecycleOwner)
        viewModel.getInvalidCalls().observe(viewLifecycleOwner, {
            invalidAdapter.setData(if (it.size > 3) it.subList(0, 3) else it)
            invalidAdapter.notifyDataSetChanged()
            manageCalls(it.size, R.string.invalid_calls_count)
        })
    }

    private fun getObservableStreams() {
        lifecycleScope.launch {
            observeNetwork()
            if (dataManager.getRole() != "3") {
                observePendingCalls()
                observeFollowupCalls()
                observeCompletedCalls()
                observeInvalidCalls()
            }
        }
    }
    override
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when (item.itemId) {
            R.id.emergency_contact -> {
                val intent = Intent(activity, MainEmergency_Contact_Activity::class.java)
                startActivity(intent)
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }
}