package com.nitiaayog.apnesaathi.ui.fragments.calls

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.CallsAdapter
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.details.SeniorCitizenDetailsFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import kotlinx.android.synthetic.main.include_recyclerview.*

class AllCallsFragment : BaseFragment<HomeViewModel>(), CallsAdapter.OnItemClickListener {

    private var position: Int = -1
    private var lastSelectedItem: CallData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CallsAdapter()
        adapter.setOnItemClickListener(this)

        rvList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        rvList.adapter = adapter
    }

    override fun provideViewModel(): HomeViewModel =
        getViewModel { HomeViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_all_calls

    override fun onCallPermissionGranted() {
        lastSelectedItem?.let { placeCall(it, R.id.fragmentCallContainer) }
    }

    override fun onCallPermissionDenied() {
    }

    override fun onItemClick(position: Int, callData: CallData) {
        this.position = position
        lastSelectedItem = callData
        prepareToCallPerson()
    }

    override fun onMoreInfoClick(position: Int, callData: CallData) {
        val fragment = SeniorCitizenDetailsFragment()
        fragment.setSelectedUser(callData)
        addFragment(
            R.id.fragmentCallContainer, fragment, getString(R.string.details_fragment)
        )
    }
}