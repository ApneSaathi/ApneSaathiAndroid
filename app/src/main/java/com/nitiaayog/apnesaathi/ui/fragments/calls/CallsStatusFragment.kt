package com.nitiaayog.apnesaathi.ui.fragments.calls

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.CallsAdapter
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.details.SeniorCitizenDetailsFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.BaseCallsTypeFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_calls_status.*

class CallsStatusFragment : BaseFragment<HomeViewModel>(), CallsAdapter.OnItemClickListener {

    private var position: Int = -1
    private var lastSelectedItem: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rvPendingList.isNestedScrollingEnabled = false
            rvFollowupList.isNestedScrollingEnabled = false
            rvAttendedList.isNestedScrollingEnabled = false
        }

        initRecyclerView()
        initViews()
    }

    override fun provideViewModel(): HomeViewModel = getViewModel {
        HomeViewModel.getInstance(dataManager)
    }

    override fun provideLayoutResource(): Int = R.layout.fragment_calls_status

    override fun onCallPermissionGranted() {
        lastSelectedItem?.let { placeCall(it, R.id.fragmentCallContainer) }
    }

    override fun onCallPermissionDenied() {

    }

    override fun onItemClick(position: Int, user: User) {
        this.position = position
        lastSelectedItem = user
        prepareToCallPerson()
    }

    override fun onMoreInfoClick(position: Int, user: User) {
        val fragment = SeniorCitizenDetailsFragment()
        fragment.setSelectedUser(user)
        addFragment(
            R.id.fragmentCallContainer, fragment,getString(R.string.details_fragment)
        )
    }

    private fun initRecyclerView() {
        val pendingAdapter = CallsAdapter(viewModel.getFewPendingCalls())
        pendingAdapter.setOnItemClickListener(this)

        val rvPendingList = (rvPendingList as RecyclerView)
        rvPendingList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvPendingList.adapter = pendingAdapter

        val followupAdapter = CallsAdapter(viewModel.getFewFollowupCalls())
        followupAdapter.setOnItemClickListener(this)

        val rvFollowupList = (rvFollowupList as RecyclerView)
        rvFollowupList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvFollowupList.adapter = followupAdapter

        val attendedAdapter = CallsAdapter(viewModel.getFewAttendedCalls())
        attendedAdapter.setOnItemClickListener(this)

        val rvAttendedList = (rvAttendedList as RecyclerView)
        rvAttendedList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvAttendedList.adapter = attendedAdapter
    }

    private fun initViews() {
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
    }
}