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

class CallsAdapter(private val role: String) :
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

    fun setOnItemClickListener(itemClickListener: OnItemClickListener<CallData>) {
        this.itemClickListener = itemClickListener
    }

    fun setData(dataList: List<CallData>) {
        this.dataList.apply {
            this.clear()
            this.addAll(dataList)
        }
        notifyDataSetChanged()
    }

    fun hideDate(isHideDateIsRequired: Boolean = false) {
        this.isHideDateIsRequired = isHideDateIsRequired
    }

    inner class TodaysCallsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val civGender: CircleImageView = itemView.civGender
        private val tvName: TextView = itemView.tvName
        private val tvAddress: TextView = itemView.tvAddress
        private val tvDate: TextView = itemView.tvDate

        init {
            tvAddress.isSelected = true

//            itemView.constraintLayout.setOnClickListener(this)
            if (role == "1") itemView.ivCall.setOnClickListener(this)
            else itemView.ivCall.visibility = View.GONE

            itemView.ivMoreInfo.setOnClickListener(this)
        }

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