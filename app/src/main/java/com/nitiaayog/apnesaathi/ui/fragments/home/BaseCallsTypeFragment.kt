package com.nitiaayog.apnesaathi.ui.fragments.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.CallsAdapter
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.details.SeniorCitizenDetailsFragment
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.android.synthetic.main.include_toolbar.*

class BaseCallsTypeFragment : BaseFragment<HomeViewModel>(),
    CallsAdapter.OnItemClickListener {

    companion object {
        const val TYPE_OF_DATA: String = "type_of_data"
        const val CONTAINER_ID: String = "container_id"
    }

    private lateinit var typeOfData: String
    private var containerId: Int = -1

    private var position: Int = -1
    private var lastSelectedItem: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.run {
            containerId = this.getInt(CONTAINER_ID, -1)
            toolBar.title = this.getString(TYPE_OF_DATA).also {
                typeOfData = it!!
            }
        }

        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener { activity?.onBackPressed() }

        initViews()
    }

    override fun onItemClick(position: Int, user: User) {
        this.position = position
        lastSelectedItem = user
        prepareToCallPerson()
    }

    override fun onMoreInfoClick(position: Int, user: User) {
        addFragment(
            containerId, SeniorCitizenDetailsFragment(), getString(R.string.edit_fragment)
        )
    }

    override fun provideViewModel(): HomeViewModel = HomeViewModel.getInstance(dataManager)

    override fun provideLayoutResource(): Int = R.layout.fragment_base_calls_type

    private fun initViews() {
        val adapter =
            when (typeOfData) {
                getString(R.string.pending_calls) -> CallsAdapter(viewModel.getPendingCalls())
                getString(R.string.follow_up) -> CallsAdapter(viewModel.getFollowupCalls())
                getString(R.string.attended_calls) -> CallsAdapter(viewModel.getAttendedCalls())
                else -> CallsAdapter(viewModel.getPendingCalls())
            }
        adapter.setOnItemClickListener(this)

        val padding = resources.getDimensionPixelOffset(R.dimen.view_size_66)
        rvList.setPadding(0, 0, 0, padding)
        rvList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            })
        rvList.adapter = adapter
    }

    override fun onCallPermissionGranted() {
        if (containerId != -1) lastSelectedItem?.let { placeCall(it, containerId) }
    }

    override fun onCallPermissionDenied() {

    }
}