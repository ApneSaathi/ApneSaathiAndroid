package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteers

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.VolunteersAdapter
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.model.Volunteer
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.home.HomeViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_base_calls_type.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class VolunteersFragment : BaseFragment<HomeViewModel>() {

    private val volunteersAdapter: VolunteersAdapter by lazy { setupVolunteersAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                getObservableStreams()
                lifecycleScope.launch(Dispatchers.IO) { viewModel.getVolunteers(requireContext()) }
            }
            .autoDispose(disposables)

        initViews()
    }

    override fun provideViewModel(): HomeViewModel {
        return HomeViewModel.getInstance(requireContext(), dataManager)
    }

    override fun provideLayoutResource(): Int {
        return R.layout.fragment_base_calls_type
    }

    override fun onCallPermissionGranted() {
    }

    override fun onCallPermissionDenied() {
    }

    private fun initViews() {
        val padding = resources.getDimensionPixelOffset(R.dimen.view_size_66)
        rvList.apply {
            setPadding(0, 0, 0, padding)
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                    setDrawable(
                        ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!
                    )
                }
            )
            adapter = volunteersAdapter
        }
    }

    private fun setupVolunteersAdapter(): VolunteersAdapter {
        return VolunteersAdapter().apply {
            setOnItemClickListener(object : OnItemClickListener<Volunteer> {
                override fun onItemClick(position: Int, data: Volunteer) {

                }

                override fun onMoreInfoClick(position: Int, data: Volunteer) {

                }
            })
        }
    }

    private fun handleNetwork(state: NetworkRequestState) {
        when (state) {
            /*is NetworkRequestState.NetworkNotAvailable -> {
                if (state.apiName == ApiProvider.ApiGrievanceTracking)
                    BaseUtility.showAlertMessage(
                        requireContext(), R.string.error, R.string.check_internet
                    )
            }*/
            is NetworkRequestState.LoadingData -> progressBar.visibility = View.VISIBLE
            is NetworkRequestState.ErrorResponse -> {
                progressBar.visibility = View.GONE
                /*BaseUtility.showAlertMessage(
                    requireContext(), getString(R.string.error), state.throwable?.message
                        ?: getString(R.string.cannt_connect_to_server_try_later),
                    getString(R.string.okay)
                )*/
            }
            is NetworkRequestState.Error -> {
                progressBar.visibility = View.GONE
                BaseUtility.showAlertMessage(
                    requireContext(), R.string.error, R.string.volunteers_not_assigned
                )
            }
            is NetworkRequestState.SuccessResponse<*> -> progressBar.visibility = View.GONE
        }
    }

    private fun getObservableStreams() {
        viewModel.getNetworkStream().removeObservers(viewLifecycleOwner)
        viewModel.getNetworkStream().observe(viewLifecycleOwner, Observer { handleNetwork(it) })

        viewModel.getVolunteersStream().removeObservers(viewLifecycleOwner)
        viewModel.getVolunteersStream().observe(viewLifecycleOwner, Observer {
            volunteersAdapter.setData(it)
        })
    }
}