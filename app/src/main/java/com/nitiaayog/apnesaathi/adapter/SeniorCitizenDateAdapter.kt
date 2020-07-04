package com.nitiaayog.apnesaathi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.model.DateItem
import kotlinx.android.synthetic.main.list_date_items.view.*

class SeniorCitizenDateAdapter() :
    RecyclerView.Adapter<SeniorCitizenDateAdapter.SeniorCitizenDateViewHolder>() {
    var selectedPos = -1
    private val dataList: MutableList<DateItem> = mutableListOf()
    private lateinit var itemClickListener: OnItemClickListener
    fun setData(dataList: List<DateItem>) {
        this.dataList.apply {
            this.clear()
            this.addAll(dataList)
            selectedPos = itemCount -1
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

    interface OnItemClickListener {
        fun onItemClick(position: Int, dateItem: DateItem)
    }

    override fun getItemCount(): Int {
        return dataList?.count() ?: 0
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onBindViewHolder(
        holder: SeniorCitizenDateAdapter.SeniorCitizenDateViewHolder, position: Int
    ) {
        dataList?.get(position)?.let { holder.bindData(it) }
    }

    inner class SeniorCitizenDateViewHolder(itemsView: View) : RecyclerView.ViewHolder(itemsView) {

        private val tvDay: TextView = itemsView.txt_day
        private val tvMonth: TextView = itemsView.txt_mnth
        private val llDateContainer: LinearLayout = itemsView.ll_date_container

        init {
            llDateContainer.setOnClickListener {
                if (::itemClickListener.isInitialized) {
                    selectedPos = adapterPosition
                    dataList?.get(adapterPosition)?.let { it1 ->
                        itemClickListener.onItemClick(
                            adapterPosition,
                            it1
                        )
                    }
                }
            }
        }

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