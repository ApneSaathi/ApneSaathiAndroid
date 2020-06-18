package com.nitiaayog.apnesaathi.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.SeniorCitizenDateAdapter
import com.nitiaayog.apnesaathi.base.extensions.rx.getViewModel
import com.nitiaayog.apnesaathi.model.DateItem
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import kotlinx.android.synthetic.main.senior_citizen_details_fragment.*


class SeniorCitizenDetailsFragment : BaseFragment<SeniorCitizenDetailsViewModel>(),
    SeniorCitizenDateAdapter.OnItemClickListener {
    override fun provideViewModel(): SeniorCitizenDetailsViewModel =
        getViewModel { SeniorCitizenDetailsViewModel.getInstance(dataManager) }

    private lateinit var adapter: SeniorCitizenDateAdapter

    override fun provideLayoutResource(): Int = R.layout.senior_citizen_details_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initAdapter()
    }

    private fun initRecyclerView() {
        val layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rcl_call_dates.layoutManager = layoutManager
    }

    private fun initAdapter() {

        adapter = SeniorCitizenDateAdapter(viewModel.prepareData())
        adapter.setOnItemClickListener(this)
        rcl_call_dates.adapter = adapter
    }

    override fun onItemClick(position: Int, dateItem: DateItem) {
        adapter.notifyDataSetChanged()
    }
}
