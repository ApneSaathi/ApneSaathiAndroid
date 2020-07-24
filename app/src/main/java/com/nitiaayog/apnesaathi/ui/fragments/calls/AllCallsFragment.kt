package com.nitiaayog.apnesaathi.ui.fragments.calls

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.CallsAdapter
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.details.SeniorCitizenDetailsFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import com.nitiaayog.apnesaathi.utility.SR_CITIZEN_DETAIL_FRAGMENT
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.include_recyclerview.*
import java.util.concurrent.TimeUnit

class AllCallsFragment : BaseFragment<HomeViewModel>(), CallsAdapter.OnItemClickListener {

    private var lastCallPosition: Int = -1
    private var lastCallData: CallData? = null

    private val callsAdapter: CallsAdapter by lazy {
        CallsAdapter().apply {
            this.setOnItemClickListener(object : CallsAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, callData: CallData) {
                    lastCallPosition = position
                    lastCallData = callData
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, callData: CallData) {
                    lastCallPosition = position
                    lastCallData = callData
                    val fragment = SeniorCitizenDetailsFragment()
                    fragment.setSelectedUser(callData)
                    viewModel.setLastSelectedUser(callData.callId.toString())
                    addFragment(R.id.fragmentCallContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT)
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe { getObservableStream() }
            .autoDispose(disposables)
        getObservableStream()

        rvList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        rvList.adapter = callsAdapter
    }

    override fun provideViewModel(): HomeViewModel =
        getViewModel { HomeViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_all_calls

    override fun onCallPermissionGranted() {
        lastCallData?.let { placeCall(it, R.id.fragmentCallContainer) }
    }

    override fun onCallPermissionDenied() =
        Toast.makeText(context, R.string.not_handle_action, Toast.LENGTH_LONG).show()

    override fun onItemClick(position: Int, callData: CallData) {
        lastCallPosition = position
        lastCallData = callData
        prepareToCallPerson()
    }

    override fun onMoreInfoClick(position: Int, callData: CallData) {
        val fragment = SeniorCitizenDetailsFragment()
        fragment.setSelectedUser(callData)
        viewModel.setLastSelectedUser(callData.callId.toString())
        addFragment(R.id.fragmentCallContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT)
    }

    private fun getObservableStream() {
        viewModel.getCallsList().removeObservers(viewLifecycleOwner)
        viewModel.getCallsList().observe(viewLifecycleOwner, Observer {
            callsAdapter.setData(it)
            callsAdapter.notifyDataSetChanged()
        })
    }
}