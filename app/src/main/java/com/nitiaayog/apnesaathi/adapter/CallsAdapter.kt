package com.nitiaayog.apnesaathi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.CircleImageView
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.utility.BaseUtility
import kotlinx.android.synthetic.main.list_item_connected_calls.view.*
import java.util.*

/**
 *  Adapter for showing calls based on its status!
 * [CallsAdapter.TodaysCallsViewHolder] is the view holder for holding the views that are available in this page
 */
class CallsAdapter(private val isCalling: Boolean) :
    RecyclerView.Adapter<CallsAdapter.TodaysCallsViewHolder>() {

    private var isHideDateIsRequired: Boolean = false
    private val dataList: MutableList<CallData> = mutableListOf()
    private lateinit var itemClickListener: OnItemClickListener<CallData>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodaysCallsViewHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_connected_calls, parent, false)
        return TodaysCallsViewHolder(customView)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: TodaysCallsViewHolder, position: Int) =
        holder.bindData(dataList[position])

    /**
     * Method for setting the item click listener.
     * [OnItemClickListener] is the call back event for listening to the item clicks
     */
    fun setOnItemClickListener(itemClickListener: OnItemClickListener<CallData>) {
        this.itemClickListener = itemClickListener
    }

    /**
     * Method for setting the data.
     * [dataList] is the list of data that's fetched from the api.
     */
    fun setData(dataList: List<CallData>) {
        this.dataList.apply {
            this.clear()
            this.addAll(dataList)
        }
        notifyDataSetChanged()
    }

    /**
     * Method for hiding the date
     * [isHideDateIsRequired] is the boolean determining if hiding is required or not
     */
    fun hideDate(isHideDateIsRequired: Boolean = false) {
        this.isHideDateIsRequired = isHideDateIsRequired
    }

    /**
     * View Holder for holding the views associated with this page.
     * [itemView] is the parent item view which holds the individual views
     * [RecyclerView.ViewHolder] is the default android class
     * [View.OnClickListener] is the default class from android library for handling the item clicks
     */
    inner class TodaysCallsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val civGender: CircleImageView = itemView.civGender
        private val tvName: TextView = itemView.tvName
        private val tvAddress: TextView = itemView.tvAddress
        private val tvDate: TextView = itemView.tvDate

        init {
            tvAddress.isSelected = true

            if (isCalling) itemView.ivCall.setOnClickListener(this)
            else itemView.ivCall.visibility = View.GONE

            itemView.ivMoreInfo.setOnClickListener(this)
        }

        /**
         * Method for binding the data
         * [callData] is the data which will be providing the values for binding the fields
         */
        fun bindData(callData: CallData) {
            tvName.text = callData.srCitizenName
            tvAddress.text = callData.block.plus(", ").plus(callData.district).plus(", ")
                .plus(callData.state)
            civGender.setImageResource(
                if (callData.gender!!.toLowerCase(Locale.ENGLISH) == "m") R.drawable.ic_male_user
                else R.drawable.ic_female_user
            )
            if (isHideDateIsRequired) {
                tvDate.visibility = View.GONE
            } else {
                tvDate.visibility = View.VISIBLE
                tvDate.text = BaseUtility.getFormattedDate(callData.loggedDateTime ?: "")
            }
        }

        override fun onClick(view: View) {
            when (view.id) {
                R.id.ivCall -> {
                    if (::itemClickListener.isInitialized)
                        itemClickListener.onItemClick(adapterPosition, dataList[adapterPosition])
                }
                R.id.ivMoreInfo -> {
                    if (::itemClickListener.isInitialized)
                        itemClickListener.onMoreInfoClick(
                            adapterPosition, dataList[adapterPosition]
                        )
                }
            }
        }
    }
}