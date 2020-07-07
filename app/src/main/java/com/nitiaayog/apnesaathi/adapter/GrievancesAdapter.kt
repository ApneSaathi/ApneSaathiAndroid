package com.nitiaayog.apnesaathi.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.CircleImageView
import com.nitiaayog.apnesaathi.model.Grievances
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance
import kotlinx.android.synthetic.main.fragment_senior_citizen_details.*
import kotlinx.android.synthetic.main.list_item_grievances.view.*

class GrievancesAdapter(private val context:Context) : RecyclerView.Adapter<GrievancesAdapter.GrievancesHolder>() {

    companion object {
        const val GRIEVANCE_RESOLVED: String = "resolved"
        const val GRIEVANCE_PENDING: String = "pending"
    }

    private val dataList: MutableList<SrCitizenGrievance> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrievancesHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_grievances, parent, false)
        return GrievancesHolder(customView)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: GrievancesHolder, position: Int) =
        holder.bindData(dataList[position])

    fun setData(dataList: MutableList<SrCitizenGrievance>) {
        this.dataList.apply {
            this.clear()
            this.addAll(dataList)
        }
        notifyDataSetChanged()
    }

    inner class GrievancesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val civGender: CircleImageView = itemView.civGender
        private val tvComplaint: TextView = itemView.tvComplaint
        private val btnComplaintStatus: TextView = itemView.btnComplaintStatus

        init {
            itemView.btnComplaintStatus.setOnClickListener {

            }
        }

        fun bindData(grievance: SrCitizenGrievance) {
            val complaint = " ".plus(context.getString(R.string.complaint_on)).plus(" ")
            val dataString = grievance.srCitizenName?.plus(complaint)?.plus(getGrievanceList(grievance))?.plus("was ").plus(grievance.status)
            val spanGrievance= SpannableString(dataString)
            spanGrievance.setSpan(StyleSpan(Typeface.BOLD), grievance.srCitizenName!!.length+complaint.length, dataString.length, 0)

            val spanStatus= SpannableString(spanGrievance)
            btnComplaintStatus.visibility = View.GONE
            if (grievance.status == GRIEVANCE_RESOLVED) {
                spanStatus.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_color_5)),spanStatus.length - grievance.status.length,spanStatus.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                spanStatus.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_color_3)),spanStatus.length - grievance.status.length,spanStatus.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            tvComplaint.text =spanStatus
            if(grievance.gender == "M"){
                civGender.setImageResource(R.drawable.ic_male_user)
            }else{
                civGender.setImageResource(R.drawable.ic_female_user)
            }

        }
    }

    private fun getGrievanceList(grievance: SrCitizenGrievance): String {
        var grievanceCategory = ""
        if (grievance.foodShortage != "4") {
            grievanceCategory = context.getString(R.string.lack_of_food).plus(", ")
        }
        if (grievance.medicineShortage != "4") {
            grievanceCategory =
                grievanceCategory.plus(context.getString(R.string.lack_of_medicine)).plus(", ")
        }
        if (grievance.accessToBankingIssue != "4") {
            grievanceCategory =
                grievanceCategory.plus(context.getString(R.string.lack_of_banking_service)).plus(", ")
        }
        if (grievance.utilitySupplyIssue != "4") {
            grievanceCategory =
                grievanceCategory.plus(context.getString(R.string.lack_of_utilities)).plus(", ")
        }
        if (grievance.hygieneIssue != "4") {
            grievanceCategory =
                grievanceCategory.plus(context.getString(R.string.lack_of_hygine)).plus(", ")
        }
        if (grievance.safetyIssue != "4") {
            grievanceCategory =
                grievanceCategory.plus(context.getString(R.string.lack_of_safety)).plus(", ")
        }
        if (grievance.emergencyServiceIssue != "4") {
            grievanceCategory =
                grievanceCategory.plus(context.getString(R.string.lack_of_access_emergency)).plus(", ")
        }
        if (grievance.phoneAndInternetIssue != "4") {
            grievanceCategory =
                grievanceCategory.plus(context.getString(R.string.phone_and_service)).plus(", ")
        }
        return  grievanceCategory
    }
}