package com.nitiaayog.apnesaathi.ui.fragments.calls

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.PendingCallsAdapter
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.BaseCallsTypeFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_calls.*
import kotlinx.android.synthetic.main.include_toolbar.*

class CallsFragment : BaseFragment<HomeViewModel>(), PendingCallsAdapter.OnItemClickListener {

    private var position: Int = -1
    private var lastSelectedItem: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBar.title = getString(R.string.menu_calls)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rvPendingList.isNestedScrollingEnabled = false
            rvFollowupList.isNestedScrollingEnabled = false
            rvAttendedList.isNestedScrollingEnabled = false
        }

        initRecyclerView()
        initViews()
    }

    override fun provideViewModel(): HomeViewModel = HomeViewModel.getInstance(dataManager)

    override fun provideLayoutResource(): Int = R.layout.fragment_calls

    override fun onCallPermissionGranted() {
        lastSelectedItem?.let { placeCall(it) }
    }

    override fun onCallPermissionDenied() =
        Toast.makeText(context, R.string.not_handle_action, Toast.LENGTH_LONG).show()

    override fun onItemClick(position: Int, user: User) {
        this.position = position
        lastSelectedItem = user
        prepareToCallPerson()
    }

    private fun initRecyclerView() {
        val pendingAdapter = PendingCallsAdapter(viewModel.getFewPendingCalls())
        pendingAdapter.setOnItemClickListener(this)

        val rvPendingList = (rvPendingList as RecyclerView)
        rvPendingList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvPendingList.adapter = pendingAdapter

        val followupAdapter = PendingCallsAdapter(viewModel.getFewFollowupCalls())
        followupAdapter.setOnItemClickListener(this)

        val rvFollowupList = (rvFollowupList as RecyclerView)
        rvFollowupList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvFollowupList.adapter = followupAdapter

        val attendedAdapter = PendingCallsAdapter(viewModel.getFewAttendedCalls())
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
        val fragment = BaseCallsTypeFragment()
        fragment.arguments = data
        addFragment(R.id.fragmentCallContainer, fragment, "Show All Data")
    }
}