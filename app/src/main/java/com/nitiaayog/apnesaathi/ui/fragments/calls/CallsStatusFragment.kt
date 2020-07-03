package com.nitiaayog.apnesaathi.ui.fragments.calls

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.CallsAdapter
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.details.SeniorCitizenDetailsFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_calls_status.*

class CallsStatusFragment : BaseFragment<HomeViewModel>(), CallsAdapter.OnItemClickListener {

    private var position: Int = -1
    private var lastSelectedCallData: CallData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        //initViews()
    }

    override fun provideViewModel(): HomeViewModel = getViewModel {
        HomeViewModel.getInstance(dataManager)
    }

    override fun provideLayoutResource(): Int = R.layout.fragment_calls_status

    override fun onCallPermissionGranted() {
        lastSelectedCallData?.let { placeCall(it, R.id.fragmentCallContainer) }
    }

    override fun onCallPermissionDenied() {

    }

    override fun onItemClick(position: Int, callData: CallData) {
        this.position = position
        lastSelectedCallData = callData
        prepareToCallPerson()
    }

    override fun onMoreInfoClick(position: Int, callData: CallData) {
        callData.callId?.let { viewModel.getUniqueGrievanceList(it).removeObservers(viewLifecycleOwner) }
        callData.callId?.let { it ->
            viewModel.getUniqueGrievanceList(it).observe(viewLifecycleOwner, Observer {
                val fragment = SeniorCitizenDetailsFragment()
                fragment.setSelectedUser(
                    callData,
                    it
                )
                addFragment(
                    R.id.fragmentCallContainer, fragment, getString(R.string.details_fragment)
                )
            })
        }
    }

    private fun initRecyclerView() {
        val pendingAdapter = CallsAdapter()
        pendingAdapter.setOnItemClickListener(this)

        val rvPendingList = (rvPendingList as RecyclerView)
        rvPendingList.isNestedScrollingEnabled = false
        rvPendingList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvPendingList.adapter = pendingAdapter

        val followupAdapter = CallsAdapter()
        followupAdapter.setOnItemClickListener(this)

        val rvFollowupList = (rvFollowupList as RecyclerView)
        rvFollowupList.isNestedScrollingEnabled = false
        rvFollowupList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvFollowupList.adapter = followupAdapter

        val attendedAdapter = CallsAdapter()
        attendedAdapter.setOnItemClickListener(this)

        val rvAttendedList = (rvAttendedList as RecyclerView)
        rvAttendedList.isNestedScrollingEnabled = false
        rvAttendedList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvAttendedList.adapter = attendedAdapter
    }

    /*private fun initViews() {
        var dataListSize: Int = viewModel.getPendingCalls().size
        tvPendingCalls.text = getString(R.string.pending_calls_count, dataListSize.toString())
        btnSeeAllPending.visibility = if (dataListSize > 3) {
            btnSeeAllPending.setOnClickListener {
                showAllData(getString(R.string.pending_calls))
            }
            View.VISIBLE
        } else View.GONE

        dataListSize = viewModel.getFollowupCalls().size
        tvFollowup.text = getString(R.string.follow_up_count, dataListSize.toString())
        btnSeeAllFollowup.visibility = if (dataListSize > 3) {
            btnSeeAllFollowup.setOnClickListener {
                showAllData(getString(R.string.follow_up))
            }
            View.VISIBLE
        } else View.GONE

        dataListSize = viewModel.getAttendedCalls().size
        tvAttended.text = getString(R.string.attended_calls_count, dataListSize.toString())
        btnSeeAllAttended.visibility = if (dataListSize > 3) {
            btnSeeAllAttended.setOnClickListener {
                showAllData(getString(R.string.attended_calls))
            }
            View.VISIBLE
        } else View.GONE
    }

    private fun showAllData(type: String) {
        val data = Bundle()
        data.putString(BaseCallsTypeFragment.TYPE_OF_DATA, type)
        data.putInt(BaseCallsTypeFragment.CONTAINER_ID, R.id.fragmentCallContainer)
        val fragment = BaseCallsTypeFragment()
        fragment.arguments = data
        addFragment(R.id.fragmentCallContainer, fragment, "Show All Data")
    }*/
}