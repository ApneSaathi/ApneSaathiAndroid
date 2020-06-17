package com.nitiaayog.apnesaathi.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.model.DateItem
import kotlinx.android.synthetic.main.list_date_items.view.*

class SeniorCitizenDateAdapter(private val dataList: MutableList<DateItem>) :
    RecyclerView.Adapter<SeniorCitizenDateAdapter.SeniorCitizenDateViewHolder>() {
    private lateinit var itemClickListener: OnItemClickListener
    var selectedPos: Int = dataList.size-1
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
        return dataList.count()
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onBindViewHolder(
        holder: SeniorCitizenDateAdapter.SeniorCitizenDateViewHolder, position: Int
    ) {
        holder.bindData(dataList[position])
    }

    inner class SeniorCitizenDateViewHolder(itemsView: View) : RecyclerView.ViewHolder(itemsView) {

        val tvDay: TextView = itemsView.txt_day
        val tvMonth: TextView = itemsView.txt_mnth
        val llDateContainer: LinearLayout = itemsView.ll_date_container

        init {
            llDateContainer.setOnClickListener {
                if (::itemClickListener.isInitialized) {
                    selectedPos = adapterPosition
                    itemClickListener.onItemClick(adapterPosition, dataList[adapterPosition])
                }
            }
        }

        fun bindData(dateItem: DateItem) {
            tvDay.text = dateItem.day
            tvMonth.text = dateItem.month
            if (selectedPos == adapterPosition) {
                tvDay.setTextColor(Color.WHITE)
                tvMonth.setTextColor(Color.WHITE)
                llDateContainer.setBackgroundColor(Color.parseColor("#253746"))
            } else {
                tvDay.setTextColor(Color.parseColor("#878cac"))
                tvMonth.setTextColor(Color.parseColor("#878cac"))
                llDateContainer.setBackgroundColor(Color.WHITE)
            }

        }


    }
}