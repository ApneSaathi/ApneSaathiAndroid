package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.home

import android.os.Bundle
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
import com.nitiaayog.apnesaathi.adapter.GrievancesAdapter
import com.nitiaayog.apnesaathi.adapter.VolunteersAdapter
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.GrievanceData
import com.nitiaayog.apnesaathi.model.Volunteer
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.BaseCallsTypeFragment
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_admin_staff_home.*
import kotlinx.android.synthetic.main.fragment_calls_status.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class HomeFragment : BaseFragment<HomeViewModel>() {

    private var lastSelectedItemId: Int = 0

    private val volunteerAdapter: VolunteersAdapter by lazy { setupVolunteersAdapter() }
    private val pendingAdapter: CallsAdapter by lazy { setupPendingCallsAdapter() }
    private val followupAdapter: CallsAdapter by lazy { setupFollowupCallsAdapter() }
    private val completedAdapter: CallsAdapter by lazy { setupCompletedCallsAdapter() }
    private val invalidAdapter: CallsAdapter by lazy { setupInvalidCallsAdapter() }
    private val grievancesAdapter: GrievancesAdapter by lazy { setGrievanceAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.title = getString(R.string.dashboard)

        Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                getObservableStreams()
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.getGrievanceTrackingList(requireContext())
                    if (dataManager.getRole() != "3")
                        viewModel.getCallDetails(requireContext())
                }
            }
            .autoDispose(disposables)

        if (dataManager.getRole() != "3") {
            setupVolunteersList()
            setupPendingCalls()
            setupFollowupCalls()
            setupCompletedCalls()
            setupInvalidCalls()
        }

        setupGrievances()
    }

    override fun provideViewModel(): HomeViewModel {
        return HomeViewModel.getInstance(requireContext(), dataManager)
    }

    override fun provideLayoutResource(): Int {
        return R.layout.fragment_admin_staff_home
    }

    override fun onCallPermissionGranted() {
        //if (lastItemId > 0){ // Place call to Volunteer}
    }

    override fun onCallPermissionDenied() {
        //Snackbar.make(bottomNavigationView, R.string.not_handle_action, Snackbar.LENGTH_LONG).show()
        Toast.makeText(requireContext(), R.string.not_handle_action, Toast.LENGTH_LONG).show()
    }

    private fun setupVolunteersList() {
        val rvVolunteers = (rvVolunteers as RecyclerView)
        rvVolunteers.apply {
            this.isNestedScrollingEnabled = false
            this.addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                    setDrawable(
                        ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!
                    )
                }
            )
            this.adapter = volunteerAdapter
        }
    }

    private fun setupVolunteersAdapter(): VolunteersAdapter {
        return VolunteersAdapter().apply {
            setOnItemClickListener(object : OnItemClickListener<Volunteer> {
                override fun onItemClick(position: Int, data: Volunteer) {
                    lastSelectedItemId = data.id!!
                }

                override fun onMoreInfoClick(position: Int, data: Volunteer) {

                }
            })
        }
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
        return CallsAdapter().apply {
            this.setOnItemClickListener(object : OnItemClickListener<CallData> {
                override fun onItemClick(position: Int, data: CallData) {
                    lastSelectedItemId = position
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, data: CallData) {
                    /*lastCallPosition = position
                    lastCallData = data
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(data)
                    viewModel.setLastSelectedUser(data.callId.toString())
                    addFragment(
                        R.id.fragmentAdminStaffHomeContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT
                    )*/
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
        return CallsAdapter().apply {
            this.setOnItemClickListener(object : OnItemClickListener<CallData> {
                override fun onItemClick(position: Int, data: CallData) {
                    lastSelectedItemId = data.callId!!
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, data: CallData) {
                    /*lastCallPosition = position
                    lastCallData = data
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(data)
                    viewModel.setLastSelectedUser(data.callId.toString())
                    addFragment(
                        R.id.fragmentAdminStaffHomeContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT
                    )*/
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
        return CallsAdapter().apply {
            this.setOnItemClickListener(object : OnItemClickListener<CallData> {
                override fun onItemClick(position: Int, data: CallData) {
                    lastSelectedItemId = data.callId!!
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, data: CallData) {
                    /*lastCallPosition = position
                    lastCallData = data
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(data)
                    viewModel.setLastSelectedUser(data.callId.toString())
                    addFragment(
                        R.id.fragmentAdminStaffHomeContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT
                    )*/
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
        return CallsAdapter().apply {
            this.setOnItemClickListener(object : OnItemClickListener<CallData> {
                override fun onItemClick(position: Int, data: CallData) {
                    lastSelectedItemId = data.callId!!
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, data: CallData) {
                    /*lastCallPosition = position
                    lastCallData = data
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(data)
                    viewModel.setLastSelectedUser(data.callId.toString())
                    addFragment(
                        R.id.fragmentAdminStaffHomeContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT
                    )*/
                }
            })
        }
    }

    private fun setupGrievances() {
        val rvGrievancesList = (rvGrievancesList as RecyclerView)
        rvGrievancesList.apply {
            isNestedScrollingEnabled = false
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                    setDrawable(
                        ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!
                    )
                }
            )
            adapter = grievancesAdapter
        }
    }

    private fun setGrievanceAdapter(): GrievancesAdapter {
        return GrievancesAdapter(context!!).apply {
            setOnItemClickListener(object : OnItemClickListener<GrievanceData> {
                override fun onItemClick(position: Int, data: GrievanceData) {
                }

                override fun onMoreInfoClick(position: Int, data: GrievanceData) {
                }
            })
        }
    }

    private fun manageVolunteersList(size: Int) {
        lifecycleScope.launch {
            tvVolunteers.text = getString(R.string.volunteers_count, size.toString())
            btnSeeAllVolunteers.visibility = if (size > 3) {
                btnSeeAllVolunteers.throttleClick().subscribe { showAllVolunteers() }
                    .autoDispose(disposables)
                View.VISIBLE
            } else View.GONE
        }
    }

    private fun showAllVolunteers() {
        val data = Bundle().apply {
            putString(BaseCallsTypeFragment.TYPE_OF_DATA, getString(R.string.menu_volunteers))
            putInt(BaseCallsTypeFragment.CONTAINER_ID, R.id.fragmentAdminStaffHomeContainer)
        }
        val fragment = BaseCallsTypeFragment()
        fragment.arguments = data
        addFragment(
            R.id.fragmentAdminStaffHomeContainer, fragment, getString(R.string.pending_calls)
        )
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
                data.putInt(BaseCallsTypeFragment.CONTAINER_ID, R.id.fragmentCallContainer)
                val fragment = BaseCallsTypeFragment()
                fragment.arguments = data
                addFragment(R.id.fragmentCallContainer, fragment, type)
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
        viewModel.getNetworkStream().observe(viewLifecycleOwner, Observer { handleNetwork(it) })
    }

    private fun observeGrievancesStream() {
        viewModel.getGrievancesStream().removeObservers(viewLifecycleOwner)
        viewModel.getGrievancesStream().observe(viewLifecycleOwner, Observer {

        })
    }

    private fun observeVolunteersStream() {
        viewModel.getVolunteersStream().removeObservers(viewLifecycleOwner)
        viewModel.getVolunteersStream().observe(viewLifecycleOwner, Observer {
            volunteerAdapter.setData(it)
            manageVolunteersList(it.size)
        })
    }

    private fun observePendingCalls() {
        viewModel.getPendingCalls().removeObservers(viewLifecycleOwner)
        viewModel.getPendingCalls().observe(viewLifecycleOwner, Observer {
            pendingAdapter.setData(if (it.size > 3) it.subList(0, 3) else it)
            pendingAdapter.hideDate(true)
            pendingAdapter.notifyDataSetChanged()
            manageCalls(it.size, R.string.pending_calls_count)
        })
    }

    private fun observeFollowupCalls() {
        viewModel.getFollowupCalls().removeObservers(viewLifecycleOwner)
        viewModel.getFollowupCalls().observe(viewLifecycleOwner, Observer {
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
        viewModel.getInvalidCalls().observe(viewLifecycleOwner, Observer {
            invalidAdapter.setData(if (it.size > 3) it.subList(0, 3) else it)
            invalidAdapter.notifyDataSetChanged()
            manageCalls(it.size, R.string.invalid_calls_count)
        })
    }

    private fun getObservableStreams() {
        lifecycleScope.launch {
            observeNetwork()
            observeGrievancesStream()
            if (dataManager.getRole() != "3") {
                observeVolunteersStream()
                observePendingCalls()
                observeFollowupCalls()
                observeCompletedCalls()
                observeInvalidCalls()
            }
        }
    }
}