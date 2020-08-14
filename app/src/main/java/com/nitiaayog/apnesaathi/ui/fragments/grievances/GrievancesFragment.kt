package com.nitiaayog.apnesaathi.ui.fragments.grievances

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayoutMediator
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.FragmentViewPagerAdapter
import com.nitiaayog.apnesaathi.base.ProgressDialog
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.interfaces.PageTitleChangeListener
import com.nitiaayog.apnesaathi.interfaces.ReloadApiRequiredListener
import com.nitiaayog.apnesaathi.model.DistrictDetails
import com.nitiaayog.apnesaathi.model.GrievanceData
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.GRIEVANCE_DETAIL_FRAGMENT
import com.nitiaayog.apnesaathi.utility.ROLE_MASTER_ADMIN
import kotlinx.android.synthetic.main.fragment_calls.tabLayout
import kotlinx.android.synthetic.main.fragment_calls.viewPager
import kotlinx.android.synthetic.main.fragment_grievances.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.lang.String.format

class GrievancesFragment : BaseFragment<HomeViewModel>(), OnItemClickListener<GrievanceData>,
    PageTitleChangeListener {

    private lateinit var reloadApiRequiredListener: ReloadApiRequiredListener
    private val progressDialog: ProgressDialog.Builder by lazy {
        ProgressDialog.Builder(context!!).setMessage("Please wait, fetching data..")
    }

    var districtList: MutableList<DistrictDetails> = mutableListOf()

    val menu: PopupMenu by lazy {
        PopupMenu(context!!, anchor_menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBar.title = getString(R.string.menu_issues)
        if (dataManager.getRole() == ROLE_MASTER_ADMIN) {
            toolBar.inflateMenu(R.menu.menu_filter_district)
            getDataStream()
        }
        setUpViewPager()

        TabLayoutMediator(
            tabLayout, viewPager, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.pending)
                    1 -> tab.text = getString(R.string.in_progress)
                    2 -> tab.text = getString(R.string.resolved)
                }
            }).attach()
    }

    private fun getDataStream() {
        viewModel.getDistrictList().removeObservers(viewLifecycleOwner)
        viewModel.getDistrictList().observe(viewLifecycleOwner, Observer {
            districtList = it
            for ((i, item) in it.withIndex()) {
                menu.menu.add(Menu.NONE, i, i, item.districtName)
            }
        })

        toolBar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
        viewModel.getGrievanceTrackingList(this.context!!)
        viewModel.getDataStream().removeObservers(viewLifecycleOwner)
        viewModel.getDataStream().observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable ->
                    BaseUtility.showAlertMessage(
                        context!!, R.string.error, R.string.api_connection_error
                    )
                is NetworkRequestState.LoadingData -> {
                    progressDialog.show()

                }
                is NetworkRequestState.ErrorResponse -> {
                    progressDialog.dismiss()

                    BaseUtility.showAlertMessage(
                        context!!, R.string.error, R.string.api_connection_error
                    )
                }
                is NetworkRequestState.SuccessResponse<*> -> progressDialog.dismiss()
            }
        })
    }

    fun setReloadApiListener(reloadApiRequiredListener: ReloadApiRequiredListener) {
        this.reloadApiRequiredListener = reloadApiRequiredListener
    }

    private fun setUpViewPager() {
        val adapter = FragmentViewPagerAdapter(activity!!)
        val pendingFragment = PendingGrievanceFragment()
        val inProgressFragment = InProgressGrievanceFragment()
        val resolvedFragment = ResolvedGrievanceFragment()

        pendingFragment.setOnItemClickListener(this)
        resolvedFragment.setOnItemClickListener(this)
        inProgressFragment.setOnItemClickListener(this)

        pendingFragment.setPageTitleChangeListener(this)
        resolvedFragment.setPageTitleChangeListener(this)
        inProgressFragment.setPageTitleChangeListener(this)

        adapter.addFragment(pendingFragment, getString(R.string.pending))
        adapter.addFragment(inProgressFragment, getString(R.string.in_progress))
        adapter.addFragment(resolvedFragment, getString(R.string.resolved))
        viewPager.adapter = adapter
    }

    override fun provideViewModel(): HomeViewModel =
        getViewModel { HomeViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_grievances

    override fun onCallPermissionGranted() {
    }

    override fun onCallPermissionDenied() {
    }

    override fun onItemClick(position: Int, data: GrievanceData) {
        val fragment = GrievanceDetailFragment(data)
        fragment.setReloadApiListener(reloadApiRequiredListener)
        addFragment(
            R.id.fl_detailed_container,
            fragment,
            GRIEVANCE_DETAIL_FRAGMENT
        )
    }

    override fun onDataLoaded(title: String, pos: Int, size: Int) {
        tabLayout.getTabAt(pos)?.text =
            format(title, size.toString())
    }

    fun reloadApi() {
        viewModel.getGrievanceTrackingList(this.context!!)
    }

    override
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when (item.itemId) {
            R.id.district_filter -> {
                menu.show()
                menu.setOnMenuItemClickListener { selectedDistrict ->
                    dataManager.setSelectedDistrictId(districtList[selectedDistrict.itemId].districtId.toString())
                    reloadApi()
                    true
                }
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }
}