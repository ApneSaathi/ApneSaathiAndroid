package com.nitiaayog.apnesaathi.ui.fragments.userDetails

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.SeniorCitizenDateAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.model.DateItem
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import kotlinx.android.synthetic.main.senior_citizen_details_fragment.*


class SeniorCitizenDetailsFragment : BaseFragment<SeniorCitizenDetailsViewModel>(),
    SeniorCitizenDateAdapter.OnItemClickListener {

    private lateinit var adapter: SeniorCitizenDateAdapter
    override fun provideViewModel(): SeniorCitizenDetailsViewModel =
        getViewModel {
            SeniorCitizenDetailsViewModel.getInstance(
                dataManager
            )
        }

    override fun provideLayoutResource(): Int = R.layout.senior_citizen_details_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClicks()
        initRecyclerView()
        initAdapter()
    }

    private fun initClicks() {
        txt_edit.setOnClickListener { changeLayoutViews() }
        img_cough.setOnClickListener {
            val status = !img_cough.isSelected
            img_cough.isSelected = status
            txt_cough.isSelected = status
            view_border_cough.isSelected = status
        }
        img_fever.setOnClickListener {
            val status = !img_fever.isSelected
            img_fever.isSelected = status
            txt_fever.isSelected = status
            view_border_fever.isSelected = status
        }
        img_shortness.setOnClickListener {
            val status = !img_shortness.isSelected
            img_shortness.isSelected = status
            txt_shortness.isSelected = status
            view_border_shortness.isSelected = status
        }

    }

    private fun changeLayoutViews() {
        cl_editable_container.visibility = View.VISIBLE
        ll_save_container.visibility = View.VISIBLE
        txt_save.visibility = View.VISIBLE
        txt_cancel.visibility = View.VISIBLE
        txt_edit.visibility = View.GONE
        cl_uneditable_container.visibility = View.GONE
        ll_status_container.visibility = View.GONE
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
        if (dateItem.status.equals("Attended")) {
            cl_editable_container.visibility = View.GONE
            ll_save_container.visibility = View.GONE
            cl_uneditable_container.visibility = View.VISIBLE
            ll_status_container.visibility = View.GONE

        } else {
            cl_editable_container.visibility = View.GONE
            ll_save_container.visibility = View.VISIBLE
            txt_save.visibility = View.GONE
            txt_cancel.visibility = View.GONE
            txt_edit.visibility = View.VISIBLE
            cl_uneditable_container.visibility = View.GONE
            ll_status_container.visibility = View.VISIBLE

        }

    }

    override fun onCallPermissionGranted() {
        TODO("Not yet implemented")
    }

    override fun onCallPermissionDenied() {
        TODO("Not yet implemented")
    }

}
