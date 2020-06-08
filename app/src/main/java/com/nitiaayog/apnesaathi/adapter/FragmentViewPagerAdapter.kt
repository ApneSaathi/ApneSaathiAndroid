package com.nitiaayog.apnesaathi.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

open class FragmentViewPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentsList: MutableList<Fragment> = mutableListOf()
    private val fragmentTitleList: MutableList<String> = mutableListOf()

    override fun getItem(position: Int): Fragment = fragmentsList[position]

    override fun getCount(): Int = fragmentsList.size

    override fun getPageTitle(position: Int): CharSequence = fragmentTitleList[position]

    fun addFragment(fragment: Fragment, pageTitle: String) {
        fragmentsList.add(fragment)
        fragmentTitleList.add(pageTitle)
    }
}