package com.nitiaayog.apnesaathi.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.CircleImageView
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.utility.BaseUtility
import kotlinx.android.synthetic.main.list_item_connected_calls.view.*

private val diffCallback: DiffUtil.ItemCallback<CallData> =
    object : DiffUtil.ItemCallback<CallData>() {
        override fun areItemsTheSame(oldItem: CallData, newItem: CallData): Boolean =
            TextUtils.equals(oldItem.callId!!.toString(), newItem.callId!!.toString())

        override fun areContentsTheSame(oldItem: CallData, newItem: CallData): Boolean =
            oldItem == newItem
    }

private val config = AsyncDifferConfig.Builder<CallData>(diffCallback).build()

class AllCallsAdapter : PagedListAdapter<CallData, AllCallsAdapter.CallHolder>(config) {

    companion object {
        private const val TAG: String = "TAG -- AllCallsAdapter -->"
    }

    private lateinit var itemClickListener: OnItemClickListener<CallData>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_connected_calls, parent, false)
        return CallHolder(customView)
    }

    override fun onBindViewHolder(holder: CallHolder, position: Int) =
        holder.bindData(getItem(position))

    fun setOnItemClickListener(itemClickListener: OnItemClickListener<CallData>) {
        this.itemClickListener = itemClickListener
    }

    inner class CallHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val civGender: CircleImageView = itemView.civGender
        private val tvName: TextView = itemView.tvName
        private val tvAddress: TextView = itemView.tvAddress
        private val tvDate: TextView = itemView.tvDate

        init {
            tvAddress.isSelected = true

            itemView.ivCall.setOnClickListener(this)
            itemView.ivMoreInfo.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val data: CallData? = currentList?.get(adapterPosition)
            when (view.id) {
                R.id.ivCall -> {
                    data?.let {
                        if (::itemClickListener.isInitialized)
                            itemClickListener.onItemClick(adapterPosition, it)
                    }
                }
                R.id.ivMoreInfo -> {
                    data?.let {
                        if (::itemClickListener.isInitialized)
                            itemClickListener.onMoreInfoClick(adapterPosition, it)
                    }
                }
            }
        }

        private fun clear() {
            tvName.text = ""
            tvAddress.text = ""
            tvDate.text = ""
        }

        fun bindData(callData: CallData?) {
            if (callData == null) clear() else {
                tvName.text = callData.srCitizenName ?: ""
                tvAddress.text = callData.block.plus(", ").plus(callData.district).plus(", ")
                    .plus(callData.state)
                civGender.setImageResource(
                    if (callData.gender == "M") R.drawable.ic_male_user else R.drawable.ic_female_user
                )
                tvDate.text = BaseUtility.getFormattedDate(callData.loggedDateTime ?: "")
            }
        }
    }
}