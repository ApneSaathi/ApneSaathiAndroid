package com.nitiaayog.apnesaathi.ui.fragments.grievances

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayoutMediator
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.FragmentViewPagerAdapter
import com.nitiaayog.apnesaathi.adapter.GrievanceStatusAdapter
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
import com.nitiaayog.apnesaathi.utility.ROLE_DISTRICT_ADMIN
import com.nitiaayog.apnesaathi.utility.ROLE_MASTER_ADMIN
import kotlinx.android.synthetic.main.fragment_calls.tabLayout
import kotlinx.android.synthetic.main.fragment_calls.viewPager
import kotlinx.android.synthetic.main.fragment_grievances.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.lang.String.format

class GrievancesFragment : BaseFragment<HomeViewModel>(), OnItemClickListener<GrievanceData>,
    PageTitleChangeListener, GrievanceStatusAdapter.CallButtonClickListener {

    private lateinit var reloadApiRequiredListener: ReloadApiRequiredListener
    private var phoneNumber = ""
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
        }
        getDataStream()
        setUpViewPager()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.pending)
                1 -> tab.text = getString(R.string.in_progress)
                2 -> tab.text = getString(R.string.resolved)
            }
        }.attach()
    }


    private fun getDataStream() {
        if (dataManager.getRole() == ROLE_MASTER_ADMIN) {
            viewModel.getDistrictList().removeObservers(viewLifecycleOwner)
            viewModel.getDistrictList().observe(viewLifecycleOwner, Observer {
                districtList = it
                if (menu.menu.size() > 0) {
                    menu.menu.clear()
                }
                menu.menu.add(Menu.NONE, 0, 0, getString(R.string.raised_by_me))
                for ((i, item) in it.withIndex()) {
                    menu.menu.add(Menu.NONE, i + 1, i + 1, item.districtName)
                }
            })

            toolBar.setOnMenuItemClickListener {
                onOptionsItemSelected(it)
            }
        }
        if (dataManager.getRole() == ROLE_DISTRICT_ADMIN)
            viewModel.getGrievanceTrackingList(this.context!!)

        viewModel.getPendingGrievances().removeObservers(viewLifecycleOwner)
        viewModel.getPendingGrievances().observe(viewLifecycleOwner, Observer {
            tabLayout.getTabAt(0)!!.text =
                format(getString(R.string.pending_count), it.size.toString())
        })
        viewModel.getInProgressGrievances().removeObservers(viewLifecycleOwner)
        viewModel.getInProgressGrievances().observe(viewLifecycleOwner, Observer {
            tabLayout.getTabAt(1)!!.text =
                format(getString(R.string.inprogress_count), it.size.toString())
        })
        viewModel.getResolvedGrievances().removeObservers(viewLifecycleOwner)
        viewModel.getResolvedGrievances().observe(viewLifecycleOwner, Observer {
            tabLayout.getTabAt(2)!!.text =
                format(getString(R.string.resolved_count), it.size.toString())
        })

        viewModel.getDataStream().removeObservers(viewLifecycleOwner)
        viewModel.getDataStream().observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable ->
                    BaseUtility.showAlertMessage(
                        context!!, R.string.error, R.string.api_connection_error
                    )
                is NetworkRequestState.LoadingData -> {
                    if (this.isResumed)
                        progressDialog.show()

                }
                is NetworkRequestState.ErrorResponse -> {
                    progressDialog.dismiss()
                    if (this.isResumed) {
                        if (it.errorCode != -1 && it.errorCode == 409) {
                            BaseUtility.showAlertMessage(
                                context!!, R.string.error, R.string.no_issues_available
                            )
                        } else {
                            BaseUtility.showAlertMessage(
                                context!!, R.string.error, R.string.api_connection_error
                            )
                        }
                    }
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

        pendingFragment.setOnCallButtonListener(this)
        inProgressFragment.setOnCallButtonListener(this)
        resolvedFragment.setOnCallButtonListener(this)

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
        if (phoneNumber.isNotEmpty())
            initiateCall(phoneNumber)
    }

    override fun onCallPermissionDenied() {
        Toast.makeText(requireContext(), R.string.not_handle_action, Toast.LENGTH_LONG).show()
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
                    val selectedDistrictId: Int = if (selectedDistrict.itemId == 0) {
                        -1
                    } else {
                        districtList[selectedDistrict.itemId - 1].districtId ?: -1
                    }

                    dataManager.setSelectedDistrictId(selectedDistrictId.toString())
                    reloadApi()
                    true
                }
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }

    override fun onCallButtonClicked(grievanceData: GrievanceData) {
        phoneNumber = grievanceData.srPhoneNumber ?: ""
        prepareToCallPerson()
    }
}