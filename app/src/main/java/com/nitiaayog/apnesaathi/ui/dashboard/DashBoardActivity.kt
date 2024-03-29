package com.nitiaayog.apnesaathi.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.FragmentViewPagerAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.interfaces.ReloadApiRequiredListener
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.fragments.calls.CallsFragment
import com.nitiaayog.apnesaathi.ui.fragments.grievances.GrievancesFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeFragment
import com.nitiaayog.apnesaathi.ui.fragments.profile.ProfileFragment
import com.nitiaayog.apnesaathi.utility.REQUEST_CODE
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.include_toolbar.*

/**
 * This page will only be visible when Volunteer logs in. If other person except Volunteer role
 * logs in
 * @see com.nitiaayog.apnesaathi.ui.adminandstaffmember.dashboard.DashboardActivity
 * page will visible
 * */
class DashBoardActivity : BaseActivity<DashBoardViewModel>(), ReloadApiRequiredListener {

    private val homeFragment = HomeFragment()
    private val callsFragment = CallsFragment()
    private val grievancesFragment = GrievancesFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.startSyncingData(this)

        initViewPager()
        initBottomNavigationView()
    }

    override fun onBackPressed() {
        val backStackCount: Int = supportFragmentManager.backStackEntryCount
        if (backStackCount > 0) supportFragmentManager.popBackStack()
        else {
            if (viewPager.currentItem == 0) super.onBackPressed()
            else viewPager.currentItem = 0
        }
    }

    override fun provideViewModel(): DashBoardViewModel = getViewModel {
        DashBoardViewModel.getInstance()
    }

    override fun provideLayoutResource(): Int = R.layout.activity_dashboard

    /**
     * Initialize view pager and adapter with Page Change callback and update Ui accordingly
     **/
    private fun initViewPager() {
        val adapter = FragmentViewPagerAdapter(this)
        adapter.addFragment(homeFragment, getString(R.string.menu_home))
        adapter.addFragment(callsFragment, getString(R.string.menu_calls))
        adapter.addFragment(grievancesFragment, getString(R.string.menu_issues))
        adapter.addFragment(profileFragment, getString(R.string.menu_profile))

        callsFragment.setNewCitizenRegisterListener(this)
        homeFragment.setReloadApiListener(this)
        grievancesFragment.setReloadApiListener(this)

        viewPager.isUserInputEnabled = false
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 4
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigationView.selectedItemId = when (position) {
                    0 -> R.id.menuHome
                    1 -> R.id.menuCalls
                    2 -> R.id.menuGrievances
                    3 -> R.id.menuProfile
                    else -> R.id.menuHome
                }
                updateToolbarTittle()
            }
        })
    }

    /**
     * Initialize Bottom Navigation View with menu item click listener and update Ui accordingly
     **/
    private fun initBottomNavigationView() {
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuHome -> viewPager.currentItem = 0
                R.id.menuCalls -> viewPager.currentItem = 1
                R.id.menuGrievances -> viewPager.currentItem = 2
                R.id.menuProfile -> viewPager.currentItem = 3
            }
            updateToolbarTittle()
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            homeFragment.reloadApi()
        }
    }

    /**
     * Update toolbar title when user changes tabs and manage back-stack entries.
     * */
    private fun updateToolbarTittle() {
        when (viewPager.currentItem) {
            0 -> toolBar.setTitle(R.string.menu_home)
            1 -> toolBar.setTitle(R.string.menu_calls)
            2 -> toolBar.setTitle(R.string.menu_issues)
            3 -> toolBar.setTitle(R.string.menu_profile)
        }
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.fragments.forEach { _ -> supportFragmentManager.popBackStack() }
    }

    override fun onReloadRequired() {
        homeFragment.reloadApi()
    }
}