package com.nitiaayog.apnesaathi.ui.fragments.calls.allcalls

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.AllCallsAdapter
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.details.SeniorCitizenDetailsFragment
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import com.nitiaayog.apnesaathi.utility.SR_CITIZEN_DETAIL_FRAGMENT
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.include_recyclerview.*
import java.util.concurrent.TimeUnit

/**
 * Fragment for showing all the call status's
 * [BaseFragment] is the base fragment with functions that are common in all the fragments
 * [AllCallsViewModel] is the view model for performing fetching data from API, caching it in data base and fetching the data back from database
 */
class AllCallsFragment : BaseFragment<AllCallsViewModel>(), OnItemClickListener<CallData> {

    companion object {
        private val TAG: String by lazy { "TAG -- ${AllCallsFragment::class.java.simpleName} -->" }
    }

    private var lastCallPosition: Int = -1
    private var lastCallData: CallData? = null

    private val callsAdapter: AllCallsAdapter by lazy {
        AllCallsAdapter().apply { setOnItemClickListener(this@AllCallsFragment) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe { getObservableStream() }
            .autoDispose(disposables)

        rvList.setPadding(0, 0, 0, resources.getDimensionPixelSize(R.dimen.view_size_62).toInt())
        rvList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        rvList.adapter = callsAdapter
    }

    override fun provideViewModel(): AllCallsViewModel = getViewModel {
        AllCallsViewModel.getInstance(requireContext(), dataManager)
    }

    override fun provideLayoutResource(): Int = R.layout.fragment_all_calls

    override fun onCallPermissionGranted() {
        lastCallData?.let { placeCall(it) }// , R.id.fragmentCallContainer
    }

    override fun onCallPermissionDenied() =
        Toast.makeText(context, R.string.not_handle_action, Toast.LENGTH_LONG).show()

    override fun onItemClick(position: Int, data: CallData) {
        lastCallPosition = position
        lastCallData = data
        prepareToCallPerson()
    }

    override fun onMoreInfoClick(position: Int, data: CallData) {
        val fragment = SeniorCitizenDetailsFragment()
        fragment.setSelectedUser(data)
        addFragment(R.id.fragmentCallContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT)
    }

    private fun getObservableStream() {
        viewModel.getDataStream().removeObservers(viewLifecycleOwner)
        viewModel.getDataStream().observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkRequestState.LoadingData ->
                    println("$TAG Loading Data")
                is NetworkRequestState.SuccessResponse<*> ->
                    println("$TAG Got response successfully")
                is NetworkRequestState.Error ->
                    println("$TAG Error")
                is NetworkRequestState.ErrorResponse ->
                    println("$TAG ErrorResponse")
            }
        })

        viewModel.getCallsStream().removeObservers(viewLifecycleOwner)
        viewModel.getCallsStream().observe(viewLifecycleOwner, Observer {
            callsAdapter.submitList(it)
        })
    }
}
