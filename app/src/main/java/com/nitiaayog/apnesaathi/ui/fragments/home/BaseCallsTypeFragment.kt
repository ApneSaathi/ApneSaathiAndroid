package com.nitiaayog.apnesaathi.ui.fragments.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.PendingCallsAdapter
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.android.synthetic.main.include_toolbar.*

class BaseCallsTypeFragment : BaseFragment<HomeViewModel>(),
    PendingCallsAdapter.OnItemClickListener {

    companion object {
        const val TYPE_OF_DATA = "type_of_data"
    }

    private lateinit var typeOfData: String

    private var position: Int = -1
    private var lastSelectedItem: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.run {
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

    override fun provideViewModel(): HomeViewModel = HomeViewModel.getInstance(dataManager)

    override fun provideLayoutResource(): Int = R.layout.fragment_base_calls_type

    private fun initViews() {
        val adapter =
            when (typeOfData) {
                getString(R.string.pending_calls) -> PendingCallsAdapter(viewModel.getPendingCalls())
                getString(R.string.follow_up) -> PendingCallsAdapter(viewModel.getFollowupCalls())
                getString(R.string.attended_calls) -> PendingCallsAdapter(viewModel.getAttendedCalls())
                else -> PendingCallsAdapter(viewModel.getPendingCalls())
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
        lastSelectedItem?.let { placeCall(it) }
    }

    override fun onCallPermissionDenied() {

    }
}