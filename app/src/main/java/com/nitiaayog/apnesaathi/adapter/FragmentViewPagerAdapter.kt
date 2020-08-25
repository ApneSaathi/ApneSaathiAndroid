package com.nitiaayog.apnesaathi.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragmentsList: MutableList<Fragment> = mutableListOf()
    private val fragmentsTitleList: MutableList<String> = mutableListOf()

    override fun getItemCount(): Int = fragmentsList.size

    override fun createFragment(position: Int): Fragment = fragmentsList[position]

    fun addFragment(fragment: Fragment, pageTitle: String) {
        fragmentsList.add(fragment)
        fragmentsTitleList.add(pageTitle)
    }

}
