package com.nitiaayog.apnesaathi.ui.fragments.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.CallsAdapter
import com.nitiaayog.apnesaathi.adapter.GrievancesAdapter
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.details.SeniorCitizenDetailsFragment
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.util.concurrent.TimeUnit

class HomeFragment : BaseFragment<HomeViewModel>() {

    private var lastSelectedPosition: Int = -1
    private var lastSelectedItem: User? = null

    private val pendingAdapter by lazy {
        CallsAdapter(viewModel.getFewPendingCalls()).apply {
            this.setOnItemClickListener(object : CallsAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, user: User) {
                    lastSelectedPosition = position
                    lastSelectedItem = user
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, user: User) {
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(user)
                    addFragment(
                        R.id.fragmentHomeContainer, fragment, getString(R.string.details_fragment)
                    )
                }
            })
        }
    }

    private val grievancesAdapter by lazy { GrievancesAdapter(viewModel.getFewGrievancesList()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.title = getString(R.string.menu_home)

        getObservableDataStream()
        initRecyclerView()

        Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                viewModel.getCallDetails(context!!)
            }
            .autoDispose(disposables)
    }

    override fun provideViewModel(): HomeViewModel =
        getViewModel { HomeViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_home

    override fun onCallPermissionGranted() {
        lastSelectedItem?.let { placeCall(it, R.id.fragmentHomeContainer) }
    }

    override fun onCallPermissionDenied() =
        Toast.makeText(context, R.string.not_handle_action, Toast.LENGTH_LONG).show()

    private fun initRecyclerView() {
        val rvPendingList = (rvPendingList as RecyclerView)
        rvPendingList.apply {
            this.isNestedScrollingEnabled = false
            this.addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                    setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
                }
            )
            this.adapter = pendingAdapter
        }

        val rvGrievancesList = (rvGrievancesList as RecyclerView)
        rvGrievancesList.apply {
            this.isNestedScrollingEnabled = false
            this.addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                    setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
                }
            )
            this.adapter = grievancesAdapter
        }
    }

    private fun managePendingCalls(dataList: MutableList<CallData>) {
        val size: Int = dataList.size
        tvPendingCalls.text = getString(R.string.pending_calls_count, size.toString())
        btnSeeAll.visibility = if (size > 3) {
            btnSeeAll.setOnClickListener {
                val data = Bundle()
                data.putString(
                    BaseCallsTypeFragment.TYPE_OF_DATA, getString(R.string.pending_calls)
                )
                data.putInt(BaseCallsTypeFragment.CONTAINER_ID, R.id.fragmentHomeContainer)
                val fragment = BaseCallsTypeFragment()
                fragment.arguments = data
                addFragment(
                    R.id.fragmentHomeContainer, fragment, getString(R.string.pending_calls)
                )
            }
            View.VISIBLE
        } else View.GONE
    }

    private fun manageGrievances(dataList: MutableList<SrCitizenGrievance>) {
        val size = dataList.size
        tvGrievances.text = getString(R.string.grievances_count, size.toString())
        btnSeeAllGrievances.visibility = if (size > 3) {
            btnSeeAllGrievances.setOnClickListener {

            }
            View.VISIBLE
        } else View.GONE
    }

    private fun manageProgressBar(visibility: Int) {
        progressBarCalls.visibility = visibility
        progressBarGrienvances.visibility = visibility
    }

    private fun getObservableDataStream() {
        viewModel.getCallsList().removeObservers(viewLifecycleOwner)
        viewModel.getCallsList().observe(viewLifecycleOwner, Observer {
            // TODO : Change List type in Adapter
            pendingAdapter.notifyDataSetChanged()
            managePendingCalls(it)
        })

        viewModel.getGrievanceList().removeObservers(viewLifecycleOwner)
        viewModel.getGrievanceList().observe(viewLifecycleOwner, Observer {
            // TODO : Change List type in Adapter
            grievancesAdapter.notifyDataSetChanged()
            manageGrievances(it)
        })

        viewModel.getDataStream().removeObservers(viewLifecycleOwner)
        viewModel.getDataStream().observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable -> {
                }
                is NetworkRequestState.LoadingData -> manageProgressBar(View.VISIBLE)
                is NetworkRequestState.ErrorResponse -> {
                    manageProgressBar(View.GONE)
                }
                is NetworkRequestState.SuccessResponse<*> -> manageProgressBar(View.GONE)
            }
        })
    }
}