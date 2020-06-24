package com.nitiaayog.apnesaathi.ui.fragments.details

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.SeniorCitizenDateAdapter
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.replaceFragment
import com.nitiaayog.apnesaathi.model.DateItem
import com.nitiaayog.apnesaathi.model.User
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_senior_citizen_details.*


class SeniorCitizenDetailsFragment : BaseFragment<SeniorCitizenDetailsViewModel>(),
    SeniorCitizenDateAdapter.OnItemClickListener, SeniorCitizenEditFragment.OnItemClickListener {

    private lateinit var adapter: SeniorCitizenDateAdapter
    lateinit var user: User
    override fun provideViewModel(): SeniorCitizenDetailsViewModel =
        getViewModel {
            SeniorCitizenDetailsViewModel.getInstance(
                dataManager
            )
        }

    override fun provideLayoutResource(): Int = R.layout.fragment_senior_citizen_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener { activity?.onBackPressed() }

        initClicks()
        initRecyclerView()
        initAdapter()
        bindData()

    }

    private fun bindData() {
        txt_user_name.text = user.userName
        txt_user_phone_number.text = user.phoneNumber
        val s = SpannableStringBuilder()
            .bold { append(resources.getString(R.string.address_bold)) }
        txt_address.text =
            s.append(" "+user.block + ", ").append(user.district + ", ").append(user.state)
        if (user.gender == "M") {
            img_user_icon.background =
                activity?.let { ContextCompat.getDrawable(it, R.drawable.ic_male_user) }
        } else {
            img_user_icon.background =
                activity?.let { ContextCompat.getDrawable(it, R.drawable.ic_female_user) }
        }
    }

    private fun initClicks() {
        img_call_button.setOnClickListener { prepareToCallPerson() }
        txt_edit.setOnClickListener {
            val fragment = SeniorCitizenEditFragment()
            fragment.setItemClickListener(this)
            replaceFragment(
                R.id.fragment_edit_container, fragment, getString(R.string.edit_fragment)
            )
        }
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
        if (dateItem.status == "Attended") {
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
        placeCall(user, R.id.fragment_edit_container)
    }

    override fun onCallPermissionDenied() =
        Toast.makeText(context, R.string.not_handle_action, Toast.LENGTH_LONG).show()

    override fun onSaveButton(status: String) {
        val index = viewModel.getDataList().size - 1
        viewModel.getDataList().get(index).status = status
        if (status == "Attended") {
            cl_uneditable_container.visibility = View.VISIBLE
            ll_status_container.visibility = View.GONE
            txt_edit.visibility = View.GONE
        }

    }

    override fun onCancelButton() {
    }

    fun setSelectedUser(selectedUser: User) {
        user = selectedUser
    }
}
