package com.nitiaayog.apnesaathi.adapter

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
import kotlinx.android.synthetic.main.list_item_grievances.view.*

class GrievancesAdapter : RecyclerView.Adapter<GrievancesAdapter.GrievancesHolder>() {

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
            tvComplaint.text = grievance.impRemarkInfo
            if (grievance.status == GRIEVANCE_RESOLVED) {
                btnComplaintStatus.setTextColor(
                    ContextCompat.getColor(btnComplaintStatus.context, R.color.text_color_5)
                )
                btnComplaintStatus.setText(R.string.resolved)
            } else {
                btnComplaintStatus.setTextColor(
                    ContextCompat.getColor(btnComplaintStatus.context, R.color.text_color_3)
                )
                btnComplaintStatus.setText(R.string.pending)
            }
            civGender.setImageResource(R.drawable.ic_male_user)
        }
    }
}