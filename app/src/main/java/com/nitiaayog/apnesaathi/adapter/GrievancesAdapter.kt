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
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.model.GrievanceData
import kotlinx.android.synthetic.main.list_item_grievances.view.*
import java.util.*

/**
 *  Adapter for showing the grievance details and also provide an option for updating the grievance status!
 * [RecyclerView.Adapter] is the default adapter from android library
 * [GrievancesAdapter.GrievancesHolder] is the view holder for holding the views that are available in this page
 */
class GrievancesAdapter(private val context: Context) :
    RecyclerView.Adapter<GrievancesAdapter.GrievancesHolder>() {

    companion object {
        const val GRIEVANCE_RESOLVED: String = "RESOLVED"
        const val GRIEVANCE_UNDER_REVIEW: String = "UNDER REVIEW"
        const val GRIEVANCE_RAISED: String = "RAISED"
    }

    private lateinit var itemClickListener: OnItemClickListener<GrievanceData>

    /**
     * Method for setting the item click listener.
     * [OnItemClickListener] is the call back event for listening to the item clicks
     */
    fun setOnItemClickListener(itemClickListener: OnItemClickListener<GrievanceData>) {
        this.itemClickListener = itemClickListener
    }

    private val dataList: MutableList<GrievanceData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrievancesHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_grievances, parent, false)
        val holder = GrievancesHolder(customView)
        customView.ll_grievance_container.setOnClickListener {
            itemClickListener.onItemClick(
                holder.adapterPosition,
                dataList[holder.adapterPosition]
            )
        }
        return holder
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: GrievancesHolder, position: Int) =
        holder.bindData(dataList[position])

    /**
     * Method for setting the data.
     * [dataList] is the list of data that's fetched from the api.
     */
    fun setData(dataList: MutableList<GrievanceData>) {
        this.dataList.apply {
            this.clear()
            this.addAll(dataList)
        }
        notifyDataSetChanged()
    }
    /**
     * View Holder for holding the views associated with this page.
     * [itemView] is the parent item view which holds the individual views
     * [RecyclerView.ViewHolder] is the default android class
     */
    inner class GrievancesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val civGender: CircleImageView = itemView.civGender
        private val tvComplaint: TextView = itemView.tvComplaint
        private val btnComplaintStatus: TextView = itemView.btnComplaintStatus

        init {
            itemView.btnComplaintStatus.setOnClickListener {

            }
        }
        /**
         * Method for binding the data
         * [grievance] is the data which will be providing the values for binding the fields
         */
        fun bindData(grievance: GrievanceData) {
            val complaint = " ".plus(context.getString(R.string.complaint_on)).plus(" ")
            val was = context.getString(R.string.was)
            val dataString =
                grievance.srCitizenName?.plus(complaint)?.plus(grievance.grievanceType).plus(" ")
                    .plus(was).plus(" ").plus(grievance.status?.toLowerCase(Locale.getDefault()))
            val spanGrievance = SpannableString(dataString)
            spanGrievance.setSpan(
                StyleSpan(Typeface.BOLD),
                grievance.srCitizenName!!.length + complaint.length,
                dataString.length - (was.length + grievance.status?.length!! + 1),
                0
            )

            val spanStatus = SpannableString(spanGrievance)
            btnComplaintStatus.visibility = View.GONE
            if (grievance.status == GRIEVANCE_RESOLVED) {
                spanStatus.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_color_5)),
                    spanStatus.length - grievance.status!!.length,
                    spanStatus.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else if (grievance.status == GRIEVANCE_UNDER_REVIEW) {
                spanStatus.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_color_3)),
                    spanStatus.length - grievance.status!!.length,
                    spanStatus.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                spanStatus.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_grey_txt)),
                    spanStatus.length - grievance.status!!.length,
                    spanStatus.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            tvComplaint.text = spanStatus
            if (grievance.gender == "M") {
                civGender.setImageResource(R.drawable.ic_male_user)
            } else {
                civGender.setImageResource(R.drawable.ic_female_user)
            }

        }
    }
}