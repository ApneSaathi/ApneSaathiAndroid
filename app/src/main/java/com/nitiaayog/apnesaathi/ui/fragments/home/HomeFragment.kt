package com.nitiaayog.apnesaathi.ui.fragments.home

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.CallsAdapter
import com.nitiaayog.apnesaathi.base.extensions.addFragment
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.userDetails.SeniorCitizenDetailsFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_toolbar.*

class HomeFragment : BaseFragment<HomeViewModel>(), CallsAdapter.OnItemClickListener {


    private var position: Int = -1
    private var lastSelectedItem: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.title = getString(R.string.menu_home)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rvPendingList.isNestedScrollingEnabled = false
            rvGrievancesList.isNestedScrollingEnabled = false
        }

        initRecyclerView()
        initViews()
    }

    override fun provideViewModel(): HomeViewModel =
        getViewModel { HomeViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_home

    override fun onCallPermissionGranted() {
        lastSelectedItem?.let { placeCall(it) }
    }

    override fun onCallPermissionDenied() =
        Toast.makeText(context, R.string.not_handle_action, Toast.LENGTH_LONG).show()

    override fun onItemClick(position: Int, user: User) {
        this.position = position
        lastSelectedItem = user
        prepareToCallPerson()
    }

    override fun onMoreInfoClick(position: Int, user: User) {
        val fragment = SeniorCitizenDetailsFragment()
        addFragment(
            R.id.fragmentHomeContainer, fragment, getString(R.string.pending_calls)
        )
    }

    private fun initRecyclerView() {
        val adapter = CallsAdapter(viewModel.getFewPendingCalls())
        adapter.setOnItemClickListener(this)

        val rvPendingList = (rvPendingList as RecyclerView)
        rvPendingList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_item_divider)!!)
            }
        )
        rvPendingList.adapter = adapter
    }

    private fun initViews() {
        val dataListSize: Int = viewModel.getPendingCalls().size
        tvPendingCalls.text = getString(R.string.pending_calls_count, dataListSize.toString())
        btnSeeAll.visibility = if (dataListSize > 3) {
            btnSeeAll.setOnClickListener {
                val data = Bundle()
                data.putString(
                    BaseCallsTypeFragment.TYPE_OF_DATA, getString(R.string.pending_calls)
                )
                val fragment = BaseCallsTypeFragment()
                fragment.arguments = data
                addFragment(
                    R.id.fragmentHomeContainer, fragment, getString(R.string.pending_calls)
                )
            }
            View.VISIBLE
        } else View.GONE
    }
}