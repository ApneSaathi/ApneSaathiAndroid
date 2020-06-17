package com.nitiaayog.apnesaathi.ui.fragments.calls

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.CallsAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import kotlinx.android.synthetic.main.include_recyclerview.*

class AllCallsFragment : BaseFragment<HomeViewModel>(), CallsAdapter.OnItemClickListener {

    private var position: Int = -1
    private var lastSelectedItem: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CallsAdapter(viewModel.getAllCalls())
        adapter.setOnItemClickListener(this)

        rvList.setPadding(0, 0, 0, resources.getDimensionPixelSize(R.dimen.view_size_66))
        rvList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        rvList.adapter = adapter
    }

    override fun provideViewModel(): HomeViewModel =
        getViewModel { HomeViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_all_calls

    override fun onCallPermissionGranted() {
    }

    override fun onCallPermissionDenied() {
    }

    override fun onItemClick(position: Int, user: User) {
        this.position = position
        lastSelectedItem = user
        prepareToCallPerson()
    }
}