package com.nitiaayog.apnesaathi.ui.fragments.grievances

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.interfaces.PageTitleChangeListener
import com.nitiaayog.apnesaathi.adapter.GrievanceStatusAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.model.GrievanceData
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import kotlinx.android.synthetic.main.include_recyclerview.*

class InProgressGrievanceFragment : BaseFragment<HomeViewModel>(),
    GrievanceStatusAdapter.OnItemClickListener {
    private lateinit var pageTitleChangeListener: PageTitleChangeListener
    private lateinit var itemClickListener: GrievanceStatusAdapter.OnItemClickListener
    private val grievanceAdapter = GrievanceStatusAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataStream()
        rvList.adapter = grievanceAdapter
    }

    private fun getDataStream() {
        grievanceAdapter.setOnItemClickListener(this)
        viewModel.getInProgressGrievances().removeObservers(viewLifecycleOwner)
        viewModel.getInProgressGrievances().observe(viewLifecycleOwner, Observer {
            grievanceAdapter.setData(it)
            grievanceAdapter.notifyDataSetChanged()
            pageTitleChangeListener.onDataLoaded(getString(R.string.inprogress_count),1, it.size)
        })
    }

    fun setOnItemClickListener(itemClickListener: GrievanceStatusAdapter.OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setPageTitleChangeListener(pageTitleChangeListener: PageTitleChangeListener) {
        this.pageTitleChangeListener = pageTitleChangeListener
    }

    override fun provideViewModel(): HomeViewModel =
        getViewModel { HomeViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_grievances_issues

    override fun onCallPermissionGranted() {
    }

    override fun onCallPermissionDenied() {
    }

    override fun onItemClick(position: Int, grievanceData: GrievanceData) {
        itemClickListener.onItemClick(position, grievanceData)
    }
}