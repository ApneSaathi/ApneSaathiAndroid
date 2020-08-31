package com.nitiaayog.apnesaathi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.model.DateItem
import kotlinx.android.synthetic.main.list_date_items.view.*

/**
 *  Adapter for showing grievance reported date so that the user can filter the grievances based on that date.!
 * [RecyclerView.Adapter] is the default adapter from android library
 * [SeniorCitizenDateAdapter.SeniorCitizenDateViewHolder] is the view holder for holding the views that are available in this page
 */
class SeniorCitizenDateAdapter :
    RecyclerView.Adapter<SeniorCitizenDateAdapter.SeniorCitizenDateViewHolder>() {

    var selectedPos = -1
    private val dataList: MutableList<DateItem> = mutableListOf()
    private lateinit var itemClickListener: OnItemClickListener<DateItem>
    fun setData(dataList: List<DateItem>) {
        this.dataList.apply {
            this.clear()
            this.addAll(dataList)
            selectedPos = itemCount - 1
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SeniorCitizenDateAdapter.SeniorCitizenDateViewHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_date_items, parent, false)
        return SeniorCitizenDateViewHolder(customView)
    }

    override fun getItemCount(): Int {
        return dataList.count()
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener<DateItem>) {
        this.itemClickListener = itemClickListener
    }

    override fun onBindViewHolder(
        holder: SeniorCitizenDateAdapter.SeniorCitizenDateViewHolder, position: Int
    ) {
        dataList[position].let { holder.bindData(it) }
    }

    /**
     * View Holder for holding the views associated with this page.
     * [itemView] is the parent item view which holds the individual views
     * [RecyclerView.ViewHolder] is the default android class
     */
    inner class SeniorCitizenDateViewHolder(itemsView: View) : RecyclerView.ViewHolder(itemsView) {

        private val tvDay: TextView = itemsView.txt_day
        private val tvMonth: TextView = itemsView.txt_mnth
        private val llDateContainer: LinearLayout = itemsView.ll_date_container

        init {
            llDateContainer.setOnClickListener {
                if (::itemClickListener.isInitialized) {
                    selectedPos = adapterPosition
                    dataList.get(adapterPosition).let { it1 ->
                        itemClickListener.onItemClick(
                            adapterPosition,
                            it1
                        )
                    }
                }
            }
        }

        /**
         * Method for binding the data
         * [dateItem] is the data which will be providing the values for binding the fields
         */
        fun bindData(dateItem: DateItem) {
            tvDay.text = dateItem.day
            tvMonth.text = dateItem.month
            if (selectedPos == adapterPosition) {
                tvDay.isSelected = true
                tvMonth.isSelected = true
                llDateContainer.isSelected = true
            } else {
                tvDay.isSelected = false
                tvMonth.isSelected = false
                llDateContainer.isSelected = false
            }

        }


    }
}