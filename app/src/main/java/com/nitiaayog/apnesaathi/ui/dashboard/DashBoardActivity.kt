package com.nitiaayog.apnesaathi.ui.dashboard

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.FragmentViewPagerAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.fragments.calls.CallsFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeFragment
import com.nitiaayog.apnesaathi.ui.fragments.notifications.NotificationsFragment
import com.nitiaayog.apnesaathi.ui.fragments.profile.ProfileFragment
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.include_toolbar.*

class DashBoardActivity : BaseActivity<DashBoardViewModel>() {

    private val homeFragment = HomeFragment()
    private val callsFragment = CallsFragment()
    private val notificationFragment = NotificationsFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.startSyncingData(this)

        initViewPager()
        initBottomNavigationView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun provideViewModel(): DashBoardViewModel =
        getViewModel { DashBoardViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.activity_dashboard

    private fun initViewPager() {
        val adapter = FragmentViewPagerAdapter(this)
        adapter.addFragment(homeFragment, getString(R.string.menu_home))
        adapter.addFragment(callsFragment, getString(R.string.menu_calls))
        adapter.addFragment(notificationFragment, getString(R.string.menu_notification))
        adapter.addFragment(profileFragment, getString(R.string.menu_profile))

        viewPager.isUserInputEnabled = false
        viewPager.adapter = adapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigationView.selectedItemId = when (position) {
                    0 -> R.id.menuHome
                    1 -> R.id.menuCalls
                    2 -> R.id.menuNotification
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
                R.id.menuNotification -> viewPager.currentItem = 2
                R.id.menuProfile -> viewPager.currentItem = 3
            }
            updateToolbarTittle()
            true
        }
    }

    private fun updateToolbarTittle() {
        when (viewPager.currentItem) {
            0 -> toolBar.setTitle(R.string.menu_home)
            1 -> toolBar.setTitle(R.string.menu_calls)
            2 -> toolBar.setTitle(R.string.menu_notification)
            3 -> toolBar.setTitle(R.string.menu_profile)
        }
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.fragments.forEach { _ -> supportFragmentManager.popBackStack() }
    }
}