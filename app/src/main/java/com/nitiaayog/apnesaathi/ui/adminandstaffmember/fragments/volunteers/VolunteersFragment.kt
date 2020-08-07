package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteers

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.VolunteersAdapter
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.model.Volunteer
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_base_calls_type.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class VolunteersFragment : BaseFragment<VolunteersViewModel>() {

    private var lastSelectedItemId: Int = -1

    private val volunteersAdapter: VolunteersAdapter by lazy { setupVolunteersAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.title = getString(R.string.volunteers)

        getObservableStreams()

        Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                lifecycleScope.launch(Dispatchers.IO) { viewModel.getVolunteersList(requireContext()) }
            }
            .autoDispose(disposables)

        initViews()
    }

    override fun provideViewModel(): VolunteersViewModel {
        return VolunteersViewModel.getInstance(dataManager)
    }

    override fun provideLayoutResource(): Int {
        return R.layout.fragment_base_calls_type
    }

    override fun onCallPermissionGranted() {
        lifecycleScope.launch(Dispatchers.IO) {
            val volunteer: Volunteer? = viewModel.getVolunteer(lastSelectedItemId)
            volunteer?.run { initiateCall(this.phoneNumber!!) }
        }
    }

    override fun onCallPermissionDenied() {
        Snackbar.make(constraintLayout, R.string.not_handle_action, Snackbar.LENGTH_LONG).show()
    }

    private fun initViews() {
        val paddingBottom = resources.getDimensionPixelOffset(R.dimen.view_size_66)
        val paddingTop = resources.getDimensionPixelOffset(R.dimen.dimen_10)
        rvList.apply {
            setPadding(0, paddingTop, 0, paddingBottom)
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
                    lastSelectedItemId = data.id!!
                    prepareToCallPerson()
                }

                override fun onMoreInfoClick(position: Int, data: Volunteer) {

                }
            })
        }
    }

    private fun handleNetwork(state: NetworkRequestState) {
        when (state) {
            is NetworkRequestState.LoadingData -> progressBar.visibility = View.VISIBLE
            is NetworkRequestState.ErrorResponse -> progressBar.visibility = View.GONE
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
            volunteersAdapter.submitList(it)
        })
    }
}