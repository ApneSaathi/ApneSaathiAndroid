package com.nitiaayog.apnesaathi.ui.fragments.calls

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.CallsAdapter
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.details.SeniorCitizenDetailsFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.BaseCallsTypeFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import com.nitiaayog.apnesaathi.utility.SR_CITIZEN_DETAIL_FRAGMENT
import kotlinx.android.synthetic.main.fragment_calls_status.*

class CallsStatusFragment : BaseFragment<HomeViewModel>(), OnItemClickListener<CallData> {

    private var lastCallPosition: Int = -1
    private var lastCallData: CallData? = null

    /*private var lastFollowupCallPosition: Int = -1
    private var lastFollowupCallData: CallData? = null
    private var lastCompletedCallPosition: Int = -1
    private var lastCompletedCallData: CallData? = null*/

    private val pendingAdapter: CallsAdapter by lazy {
        CallsAdapter().apply {
            this.setOnItemClickListener(object : OnItemClickListener<CallData> {
                override fun onItemClick(position: Int, data: CallData) {
                    lastCallPosition = position
                    lastCallData = data
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, data: CallData) {
                    lastCallPosition = position
                    lastCallData = data
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(data)
                    viewModel.setLastSelectedUser(data.callId.toString())
                    addFragment(
                        R.id.fragmentCallContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT
                    )
                }
            })
        }
    }
    private val invalidAdapter: CallsAdapter by lazy {
        CallsAdapter().apply {
            this.setOnItemClickListener(object : OnItemClickListener<CallData> {
                override fun onItemClick(position: Int, data: CallData) {
                    lastCallPosition = position
                    lastCallData = data
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, data: CallData) {
                    lastCallPosition = position
                    lastCallData = data
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(data)
                    viewModel.setLastSelectedUser(data.callId.toString())
                    addFragment(
                        R.id.fragmentCallContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT
                    )
                }
            })
        }
    }

    private val followupAdapter: CallsAdapter by lazy {
        CallsAdapter().apply {
            this.setOnItemClickListener(object : OnItemClickListener<CallData> {
                override fun onItemClick(position: Int, data: CallData) {
                    lastCallPosition = position
                    lastCallData = data
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, data: CallData) {
                    lastCallPosition = position
                    lastCallData = data
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(data)
                    viewModel.setLastSelectedUser(data.callId.toString())
                    addFragment(
                        R.id.fragmentCallContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT
                    )
                }
            })
        }
    }

    private val completedAdapter: CallsAdapter by lazy {
        CallsAdapter().apply {
            this.setOnItemClickListener(object : OnItemClickListener<CallData> {
                override fun onItemClick(position: Int, data: CallData) {
                    lastCallPosition = position
                    lastCallData = data
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, data: CallData) {
                    lastCallPosition = position
                    lastCallData = data
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(data)
                    viewModel.setLastSelectedUser(data.callId.toString())
                    addFragment(
                        R.id.fragmentCallContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT
                    )
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        getObservableStreams()
    }

    override fun provideViewModel(): HomeViewModel = getViewModel {
        HomeViewModel.getInstance(dataManager)
    }

    override fun provideLayoutResource(): Int = R.layout.fragment_calls_status

    override fun onCallPermissionGranted() {
        lastCallData?.let { placeCall(it) }//, R.id.fragmentCallContainer
    }

    override fun onCallPermissionDenied() =
        Toast.makeText(context, R.string.not_handle_action, Toast.LENGTH_LONG).show()

    override fun onItemClick(position: Int, data: CallData) {
        this.lastCallPosition = position
        lastCallData = data
        prepareToCallPerson()
    }

    override fun onMoreInfoClick(position: Int, data: CallData) {
        val fragment = SeniorCitizenDetailsFragment()
        fragment.setSelectedUser(data)
        viewModel.setLastSelectedUser(data.callId.toString())
        addFragment(
            R.id.fragmentCallContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT
        )
    }

    private fun initRecyclerView() {
        val rvPendingList = (rvPendingList as RecyclerView)
        rvPendingList.isNestedScrollingEnabled = false
        rvPendingList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvPendingList.adapter = pendingAdapter

        val rvInvalidCallsList = (rvInvalidCallList as RecyclerView)
        rvInvalidCallsList.isNestedScrollingEnabled = false
        rvInvalidCallsList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvInvalidCallsList.adapter = invalidAdapter

        val rvFollowupList = (rvFollowupList as RecyclerView)
        rvFollowupList.isNestedScrollingEnabled = false
        rvFollowupList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvFollowupList.adapter = followupAdapter

        val rvAttendedList = (rvAttendedList as RecyclerView)
        rvAttendedList.isNestedScrollingEnabled = false
        rvAttendedList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvAttendedList.adapter = completedAdapter
    }

    private fun manageCalls(dataList: MutableList<CallData>, @StringRes callsCountString: Int) =
        when (callsCountString) {
            R.string.pending_calls_count ->
                manageViews(callsCountString, tvPendingCalls, btnSeeAllPending, dataList.size)
            R.string.follow_up_count ->
                manageViews(callsCountString, tvFollowup, btnSeeAllFollowup, dataList.size)
            R.string.attended_calls_count ->
                manageViews(callsCountString, tvAttended, btnSeeAllAttended, dataList.size)
            R.string.invalid_calls_count ->
                manageViews(callsCountString, tvInvalidCalls, btnSeeAllInvalid, dataList.size)
            else -> {
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
                addFragment(R.id.fragmentAdminStaffHomeContainer, fragment, type)
            }
            View.VISIBLE
        } else View.GONE
    }

    private fun getObservableStreams() {
        viewModel.getPendingCalls().removeObservers(viewLifecycleOwner)
        viewModel.getPendingCalls().observe(viewLifecycleOwner, Observer {
            pendingAdapter.setData(if (it.size > 3) it.subList(0, 3) else it)
            pendingAdapter.hideDate(true)
            pendingAdapter.notifyDataSetChanged()
            manageCalls(it, R.string.pending_calls_count)
        })

        viewModel.getFollowupCalls().removeObservers(viewLifecycleOwner)
        viewModel.getFollowupCalls().observe(viewLifecycleOwner, Observer {
            followupAdapter.setData(if (it.size > 3) it.subList(0, 3) else it)
            followupAdapter.notifyDataSetChanged()
            manageCalls(it, R.string.follow_up_count)
        })

        viewModel.getCompletedCalls().removeObservers(viewLifecycleOwner)
        viewModel.getCompletedCalls().observe(viewLifecycleOwner, Observer {
            completedAdapter.setData(if (it.size > 3) it.subList(0, 3) else it)
            completedAdapter.notifyDataSetChanged()
            manageCalls(it, R.string.attended_calls_count)
        })

        viewModel.getInvalidCallsList().removeObservers(viewLifecycleOwner)
        viewModel.getInvalidCallsList().observe(viewLifecycleOwner, Observer {
            invalidAdapter.setData(if (it.size > 3) it.subList(0, 3) else it)
            invalidAdapter.notifyDataSetChanged()
            manageCalls(it, R.string.invalid_calls_count)
        })
    }
}