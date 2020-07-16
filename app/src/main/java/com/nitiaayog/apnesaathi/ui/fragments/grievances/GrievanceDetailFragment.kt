package com.nitiaayog.apnesaathi.ui.fragments.grievances

import android.graphics.Paint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.model.GrievanceData
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_grievance_details.*
import kotlinx.android.synthetic.main.list_item_grievance_status.*
import org.threeten.bp.format.DateTimeFormatter

class GrievanceDetailFragment(private val grievanceData: GrievanceData) :
    BaseFragment<HomeViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindData()
    }

    private fun bindData() {
        tv_grievance_type.text = grievanceData.grievanceType
        var priorityText = ""
        if (grievanceData.priority == "High") {
            priorityText = "Emergency"
        } else {
            priorityText = "Regular"
        }
        val priority = context?.getString(R.string.priority).plus(": ").plus(priorityText)
        val spanPriority = SpannableString(priority)
        spanPriority.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.colorAccent)),
            spanPriority.length - priorityText.length,
            spanPriority.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_priority.text = spanPriority
        tv_description.text = grievanceData.description
        tv_issue_id.text = grievanceData.trackingId.toString()
        tv_created_date.text = getFormattedDate(grievanceData.createdDate)
        tv_update_on.text = getFormattedDate(grievanceData.lastUpdateOn)
        tv_name.text = grievanceData.srCitizenName
        tv_name.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        if (grievanceData.gender == "M") {
            img_gender.background = context?.getDrawable(R.drawable.ic_male_user)
        } else {
            img_gender.background = context?.getDrawable(R.drawable.ic_female_user)
        }

        tv_volunteer_name.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        tv_volunteer_name2.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        tv_update_status.setOnClickListener {
            showBottomSheetDialogFragment()
        }
    }

    private fun showBottomSheetDialogFragment() {
        val view = layoutInflater.inflate(R.layout.updated_progress_layout, null)
        val dialog = BottomSheetDialog(activity!!)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun getFormattedDate(date: String?): String {
        if (date.isNullOrEmpty()) return ""
        val input = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val output = DateTimeFormatter.ofPattern("MMM dd,yyyy")
        val fa = input.parse(date)
        return output.format(fa)
    }

    override fun provideViewModel(): HomeViewModel =
        getViewModel { HomeViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.fragment_grievance_details

    override fun onCallPermissionGranted() {
    }

    override fun onCallPermissionDenied() {
    }
}