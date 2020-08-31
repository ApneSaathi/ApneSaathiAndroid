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

/**
 *  Adapter for showing calls including all the status!
 * [PagedListAdapter] is the default adapter from android library. Used for pagination
 * [AllCallsAdapter.CallHolder] is the view holder for holding the views that are available in this page
 */
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

    /**
     * Method for setting the item click listener.
     * [OnItemClickListener] is the call back event for listening to the item clicks
     */
    fun setOnItemClickListener(itemClickListener: OnItemClickListener<CallData>) {
        this.itemClickListener = itemClickListener
    }

    /**
     * View Holder for holding the views associated with this page.
     * [itemView] is the parent item view which holds the individual views
     * [RecyclerView.ViewHolder] is the default android class
     */
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

        /**
         * Method for clearing the text fields
         */
        private fun clear() {
            tvName.text = ""
            tvAddress.text = ""
            tvDate.text = ""
        }

        /**
         * Method for binding the data
         * [callData] is the data which will be providing the values for binding the fields
         */
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