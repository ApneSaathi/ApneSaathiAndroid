package com.nitiaayog.apnesaathi.ui.adminandstaffmember.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.annotation.MenuRes
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.FragmentViewPagerAdapter
import com.nitiaayog.apnesaathi.interfaces.ReloadApiRequiredListener
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.home.HomeFragment
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteers.VolunteersFragment
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.dashboard.DashBoardViewModel
import com.nitiaayog.apnesaathi.ui.fragments.grievances.GrievancesFragment
import com.nitiaayog.apnesaathi.ui.fragments.profile.ProfileFragment
import com.nitiaayog.apnesaathi.utility.REQUEST_CODE
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.include_toolbar.*

/**
 * This page will only be visible when Master/District Admin and Staff Member logs in.
 * If Volunteer logs in then
 * @see com.nitiaayog.apnesaathi.ui.dashboard.DashBoardActivity page will visible
 * */
class DashboardActivity : BaseActivity<DashBoardViewModel>(), ReloadApiRequiredListener {

    private val homeFragment = HomeFragment()
    private val volunteersFragment = VolunteersFragment()
    private val grievancesFragment = GrievancesFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.startSyncingData(this)

        initViewPager()
        initBottomNavigationView()
        if (viewPager.adapter!!.itemCount == 2) manageBottomNavigationViewForRoles()
        else hideNavigationViewWithRoles()
    }

    override fun onBackPressed() {
        val backStackCount: Int = supportFragmentManager.backStackEntryCount
        if (backStackCount > 0) supportFragmentManager.popBackStack()
        else {
            if (viewPager.currentItem == 0) super.onBackPressed()
            else viewPager.currentItem = 0
        }
    }

    override fun provideViewModel(): DashBoardViewModel {
        return DashBoardViewModel.getInstance()
    }

    override fun provideLayoutResource(): Int {
        return R.layout.activity_dashboard
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            grievancesFragment.reloadApi()
        }
    }

    private fun initViewPager() {
        val offscreenPageLimit = viewModel.getOffScreenPageLimit(dataManager)
        val adapter = FragmentViewPagerAdapter(this)
        if (offscreenPageLimit != 2) {
            adapter.addFragment(homeFragment, getString(R.string.dashboard))
            adapter.addFragment(volunteersFragment, getString(R.string.menu_volunteers))
        }
        adapter.addFragment(grievancesFragment, getString(R.string.menu_issues))
        adapter.addFragment(profileFragment, getString(R.string.menu_profile))

        //callsFragment.setNewCitizenRegisterListener(this)
        //homeFragment.setReloadApiListener(this)
        grievancesFragment.setReloadApiListener(this)

        setupViewPager(offscreenPageLimit, adapter)
    }

    /**
     * Prepare setup for ViewPager
     **/
    private fun setupViewPager(offscreenPageLimit: Int, adapter: FragmentStateAdapter) {
        viewPager.isUserInputEnabled = false
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = offscreenPageLimit
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigationView.selectedItemId =
                    getNavigationSelectedItem(offscreenPageLimit, position)
                updateToolbarTittle()
            }
        })
    }

    /**
     * Instantiate Bottom Navigation Item click
     * */
    private fun initBottomNavigationView() {
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setOnNavigationItemSelectedListener {
            getPagerSelectedItem(it.itemId)
            updateToolbarTittle()
            true
        }
    }

    private fun hideNavigationViewWithRoles() {
        val menu = bottomNavigationView.menu
        val menuItem = menu.findItem(R.id.menuCalls)
        val menuItem1 = menu.findItem(R.id.menuVolunteers)
        menuItem.isVisible = false
        menuItem1.isVisible = true
    }

    private fun updateToolbarTittle() {
        if (viewModel.getOffScreenPageLimit(dataManager) == 2) {
            when (viewPager.currentItem) {
                0 -> toolBar.setTitle(R.string.dashboard)
                1 -> toolBar.setTitle(R.string.menu_profile)
            }
        } else {
            when (viewPager.currentItem) {
                0 -> toolBar.setTitle(R.string.dashboard)
                1 -> toolBar.setTitle(R.string.volunteers)
                2 -> toolBar.setTitle(R.string.menu_issues)
                3 -> toolBar.setTitle(R.string.menu_profile)
            }
        }
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.fragments.forEach { _ -> supportFragmentManager.popBackStack() }
    }

    private fun manageBottomNavigationViewForRoles() {
        val menu = bottomNavigationView.menu
        val menuItem = menu.findItem(R.id.menuCalls)
        menuItem.setTitle(R.string.menu_volunteers)

        menu.removeItem(R.id.menuHome)
        menu.removeItem(R.id.menuCalls)
    }

    private fun getPagerSelectedItem(@MenuRes itemId: Int): Int {
        return if (viewPager.offscreenPageLimit == 2) {
            when (itemId) {
                R.id.menuGrievances -> viewPager.currentItem = 0
                R.id.menuProfile -> viewPager.currentItem = 1
                else -> viewPager.currentItem = 0
            }
            viewPager.currentItem
        } else {
            when (itemId) {
                R.id.menuHome -> viewPager.currentItem = 0
                R.id.menuVolunteers -> viewPager.currentItem = 1
                R.id.menuGrievances -> viewPager.currentItem = 2
                R.id.menuProfile -> viewPager.currentItem = 3
                else -> viewPager.currentItem = 0
            }
            viewPager.currentItem
        }
    }

    private fun getNavigationSelectedItem(totalItems: Int, position: Int): Int {
        return if (totalItems == 2) {
            when (position) {
                0 -> R.id.menuGrievances
                1 -> R.id.menuProfile
                else -> R.id.menuHome
            }
        } else {
            when (position) {
                0 -> R.id.menuHome
                1 -> R.id.menuVolunteers
                2 -> R.id.menuGrievances
                3 -> R.id.menuProfile
                else -> R.id.menuHome
            }
        }
    }

    override fun onReloadRequired() {
        grievancesFragment.reloadApi()
    }
}