package com.nitiaayog.apnesaathi.ui.fragments.calls

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.FragmentViewPagerAdapter
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.interfaces.ReloadApiRequiredListener
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.calls.allcalls.AllCallsFragment
import com.nitiaayog.apnesaathi.ui.fragments.calls.registernewseniorcitizen.RegisterNewSeniorCitizenFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_calls.*
import kotlinx.android.synthetic.main.include_toolbar.*

/**
 * Fragment for showing the call status
 * [BaseFragment] is the base fragment with functions that are common in all the fragments
 * [HomeViewModel] is the view model for performing fetching data from API, caching it in data base and fetching the data back from database
 */
class CallsFragment : BaseFragment<HomeViewModel>() {

    private lateinit var reloadApiRequiredListener: ReloadApiRequiredListener
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.title = getString(R.string.menu_calls)

        setUpViewPager()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.call_status_1)
                1 -> tab.text = getString(R.string.all_calls)
            }
        }.attach()

        fabRegisterNewSrCitizen.throttleClick().subscribe {
            val fragment = RegisterNewSeniorCitizenFragment()
            fragment.setNewCitizenRegisterListener(reloadApiRequiredListener)
            addFragment(
                R.id.fragmentCallContainer, fragment, getString(R.string.register_a_new_citizen)
            )
        }.autoDispose(disposables)
    }

    override fun provideViewModel(): HomeViewModel = getViewModel {
        HomeViewModel.getInstance(dataManager)
    }

    override fun provideLayoutResource(): Int = R.layout.fragment_calls

    override fun onCallPermissionGranted() {
    }

    override fun onCallPermissionDenied() {}

    private fun setUpViewPager() {
        val adapter = FragmentViewPagerAdapter(activity!!)
        adapter.addFragment(CallsStatusFragment(), getString(R.string.call_status_1))
        adapter.addFragment(AllCallsFragment(), getString(R.string.all_calls))
        viewPager.adapter = adapter
    }

    fun setNewCitizenRegisterListener(reloadApiRequiredListener: ReloadApiRequiredListener) {
        this.reloadApiRequiredListener = reloadApiRequiredListener
    }
}