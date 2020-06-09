package com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenssupporttoday

import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.FragmentViewPagerAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.fragments.TodaysCallsFragment
import kotlinx.android.synthetic.main.activity_senior_citizens_supported_today.*

class SeniorCitizensSupportedToday : BaseActivity<SrCitizensSupportedTodayViewModel>(),
    ViewPager.OnPageChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolBar.setNavigationIcon(R.drawable.ic_back_white)
        setSupportActionBar(toolBar)
        toolBar.setNavigationOnClickListener { finish() }

        prepareViewPager()
        initViews()
    }

    override fun provideViewModel(): SrCitizensSupportedTodayViewModel =
        getViewModel { SrCitizensSupportedTodayViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.activity_senior_citizens_supported_today

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> changeBackgroundColor(btnTodaysCalls, btnConnectedCalls)
            1 -> changeBackgroundColor(btnConnectedCalls, btnTodaysCalls)
        }
    }

    private fun initViews() {
        btnTodaysCalls.throttleClick().subscribe {
            viewPager.currentItem = 0
            changeBackgroundColor(btnTodaysCalls, btnConnectedCalls)
        }.autoDispose(disposables)
        btnConnectedCalls.throttleClick().subscribe {
            viewPager.currentItem = 1
            changeBackgroundColor(btnConnectedCalls, btnTodaysCalls)
        }.autoDispose(disposables)
    }

    private fun prepareViewPager() {
        val fragment1 = TodaysCallsFragment()
        val fragment2 = TodaysCallsFragment()
        val adapter = FragmentViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(fragment1, getString(R.string.todays_call))
        adapter.addFragment(fragment2, getString(R.string.call_connected))
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(this)
    }

    private fun changeBackgroundColor(btnSelected: Button, btnRemoveSelection: Button) {
        btnSelected.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        btnRemoveSelection.setBackgroundColor(
            ContextCompat.getColor(this, android.R.color.darker_gray)
        )
    }
}