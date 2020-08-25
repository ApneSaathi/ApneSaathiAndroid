package com.nitiaayog.apnesaathi.ui.fragments.grievances

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.GrievanceStatusAdapter
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.interfaces.PageTitleChangeListener
import com.nitiaayog.apnesaathi.model.GrievanceData
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import kotlinx.android.synthetic.main.include_recyclerview.*

class ResolvedGrievanceFragment : BaseFragment<HomeViewModel>(),
    OnItemClickListener<GrievanceData> {
    private lateinit var pageTitleChangeListener: PageTitleChangeListener
    private val grievanceAdapter = GrievanceStatusAdapter()
    private lateinit var itemClickListener: OnItemClickListener<GrievanceData>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataStream()
        rvList.adapter = grievanceAdapter
    }

    private fun getDataStream() {
        grievanceAdapter.setOnItemClickListener(this)
        viewModel.getResolvedGrievances().removeObservers(viewLifecycleOwner)
        viewModel.getResolvedGrievances().observe(viewLifecycleOwner, Observer {
            grievanceAdapter.setData(it)
            grievanceAdapter.notifyDataSetChanged()
            //pageTitleChangeListener.onDataLoaded(getString(R.string.resolved_count), 2, it.size)
        })
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener<GrievanceData>) {
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

    override fun onItemClick(position: Int, data: GrievanceData) {
        itemClickListener.onItemClick(position, data)
    }
}