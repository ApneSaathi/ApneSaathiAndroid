package com.nitiaayog.apnesaathi.ui.adminandstaffmember.dashboard

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.FragmentViewPagerAdapter
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.home.HomeFragment
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteers.VolunteersFragment
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.dashboard.DashBoardViewModel
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.include_toolbar.*

class DashboardActivity : BaseActivity<DashBoardViewModel>() {

    private val homeFragment = HomeFragment()
    private val volunteersFragment = VolunteersFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewPager()
        initBottomNavigationView()
    }

    override fun provideViewModel(): DashBoardViewModel {
        return DashBoardViewModel.getInstance()
    }

    override fun provideLayoutResource(): Int {
        return R.layout.activity_dashboard
    }

    private fun initViewPager() {
        val adapter = FragmentViewPagerAdapter(this)
        adapter.addFragment(homeFragment, getString(R.string.dashboard))
        adapter.addFragment(volunteersFragment, getString(R.string.menu_calls))
        //adapter.addFragment(grievancesFragment, getString(R.string.menu_issues))
        //adapter.addFragment(profileFragment, getString(R.string.menu_profile))

        //callsFragment.setNewCitizenRegisterListener(this)
        //homeFragment.setReloadApiListener(this)
        //grievancesFragment.setReloadApiListener(this)

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

    private fun updateToolbarTittle() {
        when (viewPager.currentItem) {
            0 -> toolBar.setTitle(R.string.dashboard)
            1 -> toolBar.setTitle(R.string.volunteers)
            2 -> toolBar.setTitle(R.string.menu_issues)
            3 -> toolBar.setTitle(R.string.menu_profile)
        }
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.fragments.forEach { _ -> supportFragmentManager.popBackStack() }
    }
}