package com.nitiaayog.apnesaathi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.model.GrievanceData

class GrievanceStatusAdapter : RecyclerView.Adapter<GrievanceStatusAdapter.GrievanceViewHolder>() {

    private val dataList: MutableList<GrievanceData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrievanceViewHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_grievance_status, parent, false)
        return GrievanceViewHolder(customView)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: GrievanceViewHolder, position: Int) {
//        holder.bindData(dataList[position])
    }

    fun setData(data: MutableList<GrievanceData>) {
        dataList.clear()
        dataList.addAll(data)
    }

    class GrievanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(grievanceData: GrievanceData) {

        }

    }
}