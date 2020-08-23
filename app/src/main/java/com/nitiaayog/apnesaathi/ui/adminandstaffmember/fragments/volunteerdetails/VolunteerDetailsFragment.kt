package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteerdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.FragmentViewPagerAdapter
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.interfaces.MoreButtonClickedListener
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.Volunteer
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.about.AboutVolunteerFragment
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.reviewrating.FragmentRatingReviews
import com.nitiaayog.apnesaathi.ui.fragments.details.SeniorCitizenDetailsFragment
import com.nitiaayog.apnesaathi.utility.ID
import com.nitiaayog.apnesaathi.utility.SR_CITIZEN_DETAIL_FRAGMENT
import kotlinx.android.synthetic.main.fragment_calls.*
import kotlinx.android.synthetic.main.include_toolbar.*

class VolunteerDetailsFragment : Fragment(), MoreButtonClickedListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calls, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.title = getString(R.string.volunteer_details)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener { fragmentManager?.popBackStack() }

        fabRegisterNewSrCitizen.visibility = View.GONE

        setUpViewPager()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.about)
                1 -> tab.text = getString(R.string.reviews)
            }
        }.attach()
    }

    override fun onMoreButtonClick(callData: CallData) {
        val fragment = SeniorCitizenDetailsFragment()
        fragment.setSelectedUser(callData)
        addFragment(R.id.fragmentCallContainer, fragment, SR_CITIZEN_DETAIL_FRAGMENT)
    }

    private fun getData(): Volunteer {
        val data = arguments
        return if (data != null) {
            Volunteer().apply {
                id = if (data.containsKey(ID)) data.getInt(ID, 0) else 0
                firstName = if (data.containsKey(ApiConstants.FirstName))
                    data.getString(ApiConstants.FirstName, "") else ""
                lastName = if (data.containsKey(ApiConstants.LastName))
                    data.getString(ApiConstants.LastName, "") else ""
                phoneNumber = if (data.containsKey(ApiConstants.PhoneNumber))
                    data.getString(ApiConstants.PhoneNumber, "") else ""
                address = if (data.containsKey(ApiConstants.Address))
                    data.getString(ApiConstants.Address, "") else ""
                gender = if (data.containsKey(ApiConstants.Gender))
                    data.getString(ApiConstants.Gender, "") else ""
                assessmentScore = if (data.containsKey(ApiConstants.AssessmentScore))
                    data.getString(ApiConstants.AssessmentScore, "") else ""
                joiningDate = if (data.containsKey(ApiConstants.JoiningDate))
                    data.getString(ApiConstants.JoiningDate, "") else ""
            }
        } else Volunteer()
    }

    private fun setUpViewPager() {
        val volunteer = getData()

        val adapter = FragmentViewPagerAdapter(requireActivity())
        val aboutFragment = AboutVolunteerFragment.getInstance(volunteer)
        aboutFragment.setOnMoreItemClickListener(this)
        adapter.addFragment(aboutFragment, getString(R.string.about))
        adapter.addFragment(
            FragmentRatingReviews.getInstance(
                volunteer.id!!, volunteer.firstName!!.plus(" ").plus(volunteer.lastName!!)
            ), getString(R.string.reviews)
        )
        viewPager.adapter = adapter
    }
}