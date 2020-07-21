package com.nitiaayog.apnesaathi.ui.fragments.grievances

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.interfaces.PageTitleChangeListener
import com.nitiaayog.apnesaathi.adapter.FragmentViewPagerAdapter
import com.nitiaayog.apnesaathi.adapter.GrievanceStatusAdapter
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.interfaces.ReloadApiRequiredListener
import com.nitiaayog.apnesaathi.model.GrievanceData
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import com.nitiaayog.apnesaathi.utility.GRIEVANCE_DETAIL_FRAGMENT
import kotlinx.android.synthetic.main.fragment_calls.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.lang.String.format

class GrievancesFragment : BaseFragment<HomeViewModel>(),
    GrievanceStatusAdapter.OnItemClickListener,
    PageTitleChangeListener {


    private lateinit var reloadApiRequiredListener: ReloadApiRequiredListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBar.title = getString(R.string.menu_issues)

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


        adapter.addFragment(pendingFragment,getString(R.string.pending))
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

    override fun onItemClick(position: Int, grievanceData: GrievanceData) {
        val fragment = GrievanceDetailFragment(grievanceData)
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
}