package com.nitiaayog.apnesaathi.adapter

import android.content.Context
import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.model.GrievanceData
import kotlinx.android.synthetic.main.list_item_grievance_status.view.*
import org.threeten.bp.format.DateTimeFormatter

class GrievanceStatusAdapter :
    RecyclerView.Adapter<GrievanceStatusAdapter.GrievanceViewHolder>() {

    private val dataList: MutableList<GrievanceData> = mutableListOf()
    private lateinit var itemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrievanceViewHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_grievance_status, parent, false)
        return GrievanceViewHolder(customView, parent.context)
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, grievanceData: GrievanceData)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: GrievanceViewHolder, position: Int) {
        holder.bindData(dataList[position])
    }

    fun setData(data: MutableList<GrievanceData>) {
        dataList.clear()
        dataList.addAll(data)
    }

    inner class GrievanceViewHolder(itemView: View, private val context: Context) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        fun bindData(grievanceData: GrievanceData) {
            itemView.tv_grievance_type.text = grievanceData.grievanceType
            val priorityText = grievanceData.priority
            val priority =
                context.getString(R.string.priority).plus(": ").plus(priorityText)
            val spanPriority = SpannableString(priority)
            spanPriority.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorAccent)),
                spanPriority.length - priorityText!!.length,
                spanPriority.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            itemView.tv_priority.text = spanPriority

            itemView.tv_description.text = grievanceData.description
            itemView.tv_issue_id.text = grievanceData.trackingId.toString()
            itemView.tv_created_date.text = getFormattedDate(grievanceData.createdDate)
            itemView.tv_update_on.text = getFormattedDate(grievanceData.lastUpdateOn)
            itemView.tv_name.text = grievanceData.srCitizenName
            itemView.tv_name.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            if (grievanceData.gender == "M") {
                itemView.img_gender.background = context.getDrawable(R.drawable.ic_male_user)
            } else {
                itemView.img_gender.background = context.getDrawable(R.drawable.ic_female_user)
            }
            itemView.constraintLayout.setOnClickListener(this)
        }

        private fun getFormattedDate(date: String?): String {
            if (date.isNullOrEmpty()) return ""
            val input = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val output = DateTimeFormatter.ofPattern("MMM dd,yyyy")
            val fa = input.parse(date)
            return output.format(fa)
        }

        override fun onClick(view: View) {
            if (::itemClickListener.isInitialized)
                itemClickListener.onItemClick(adapterPosition, dataList[adapterPosition])
        }
    }

}