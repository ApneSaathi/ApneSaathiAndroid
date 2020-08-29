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

/**
 * Fragment for showing the in pending grievances
 * [BaseFragment] is the base fragment with functions that are common in all the fragments
 * [HomeViewModel] is the view model for performing fetching data from API, caching it in data base and fetching the data back from database
 * [OnItemClickListener] is used for listening to the item click
 */
class PendingGrievanceFragment : BaseFragment<HomeViewModel>(), OnItemClickListener<GrievanceData> {
    private lateinit var pageTitleChangeListener: PageTitleChangeListener
    private lateinit var itemClickListener: OnItemClickListener<GrievanceData>
    private lateinit var callButtonClickListener: GrievanceStatusAdapter.CallButtonClickListener
    private val grievanceAdapter = GrievanceStatusAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataStream()
        rvList.adapter = grievanceAdapter
    }

    /**
     * Method for listening to the data stream and set the data to the adapter when the data is available.
     */
    private fun getDataStream() {
        grievanceAdapter.setOnItemClickListener(this)
        if (::callButtonClickListener.isInitialized)
            grievanceAdapter.setOnCallButtonClickListener(callButtonClickListener)
        viewModel.getPendingGrievances().removeObservers(viewLifecycleOwner)
        viewModel.getPendingGrievances().observe(viewLifecycleOwner, Observer {
            grievanceAdapter.setData(it)
            grievanceAdapter.notifyDataSetChanged()
            //pageTitleChangeListener.onDataLoaded(getString(R.string.pending_count), 0, it.size)
        })
    }

    /**
     * Method for setting the grievance item click listener
     * [OnItemClickListener] is the listener for listening to the item click
     */
    fun setOnItemClickListener(itemClickListener: OnItemClickListener<GrievanceData>) {
        this.itemClickListener = itemClickListener
    }

    /**
     * Method for setting the grievance call button click listener
     * [GrievanceStatusAdapter.CallButtonClickListener] is the listener for listening to the call button click
     */
    fun setOnCallButtonListener(callButtonClickListener: GrievanceStatusAdapter.CallButtonClickListener) {
        this.callButtonClickListener = callButtonClickListener
    }
    /**
     * Method for setting the title change listener
     * [PageTitleChangeListener] is the listener for listening to the title change
     */
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