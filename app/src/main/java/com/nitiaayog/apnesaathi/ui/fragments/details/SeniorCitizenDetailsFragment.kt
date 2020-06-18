package com.nitiaayog.apnesaathi.ui.fragments.details

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.SeniorCitizenDateAdapter
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.replaceFragment
import com.nitiaayog.apnesaathi.model.DateItem
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_senior_citizen_details.*


class SeniorCitizenDetailsFragment : BaseFragment<SeniorCitizenDetailsViewModel>(),
    SeniorCitizenDateAdapter.OnItemClickListener,SeniorCitizenEditFragment.OnItemClickListener {

    private lateinit var adapter: SeniorCitizenDateAdapter
    override fun provideViewModel(): SeniorCitizenDetailsViewModel =
        getViewModel {
            SeniorCitizenDetailsViewModel.getInstance(
                dataManager
            )
        }

    override fun provideLayoutResource(): Int = R.layout.fragment_senior_citizen_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClicks()
        initRecyclerView()
        initAdapter()
    }

    private fun initClicks() {
        txt_edit.setOnClickListener{
            val fragment = SeniorCitizenEditFragment()
            fragment.setItemClickListener(this)
            replaceFragment(
                R.id.fragment_edit_container, fragment,getString(R.string.edit_fragment)
            )}
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
            cl_uneditable_container.visibility = View.VISIBLE
            ll_status_container.visibility = View.GONE
            txt_edit.visibility = View.GONE

        } else {
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

    override fun onSaveButton(status: String) {
        val index = viewModel.getDataList().size -1
        viewModel.getDataList().get(index).status = status
        if(status.equals("Attended")){
            cl_uneditable_container.visibility = View.VISIBLE
            ll_status_container.visibility = View.GONE
            txt_edit.visibility = View.GONE
        }

    }

    override fun onCancelButton() {
        TODO("Not yet implemented")
    }

}
