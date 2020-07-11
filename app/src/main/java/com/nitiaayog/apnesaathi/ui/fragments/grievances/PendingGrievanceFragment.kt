package com.nitiaayog.apnesaathi.ui.fragments.grievances

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.GrievanceStatusAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import kotlinx.android.synthetic.main.include_recyclerview.*

class PendingGrievanceFragment : BaseFragment<HomeViewModel>() {
    private val grievanceAdapter = GrievanceStatusAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataStream()
        rvList.adapter = grievanceAdapter
    }

    private fun getDataStream() {
        viewModel.getPendingGrievances().removeObservers(viewLifecycleOwner)
        viewModel.getPendingGrievances().observe(viewLifecycleOwner, Observer {
            grievanceAdapter.setData(it)
            grievanceAdapter.notifyDataSetChanged()
        })
    }

    override fun provideViewModel(): HomeViewModel =
        getViewModel { HomeViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_grievances_issues

    override fun onCallPermissionGranted() {
    }

    override fun onCallPermissionDenied() {
    }
}