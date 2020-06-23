package com.nitiaayog.apnesaathi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.CircleImageView
import com.nitiaayog.apnesaathi.model.User
import kotlinx.android.synthetic.main.list_item_connected_calls.view.*

class CallsAdapter(private val dataList: MutableList<User>) :
    RecyclerView.Adapter<CallsAdapter.TodaysCallsViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int, user: User)
        fun onMoreInfoClick(position: Int, user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodaysCallsViewHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_connected_calls, parent, false)
        return TodaysCallsViewHolder(customView)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: TodaysCallsViewHolder, position: Int) =
        holder.bindData(dataList[position])

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    inner class TodaysCallsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val civGender: CircleImageView = itemView.civGender
        val tvName: TextView = itemView.tvName
        val tvAddress: TextView = itemView.tvAddress

        init {
            tvAddress.isSelected = true

            itemView.constraintLayout.setOnClickListener(this)
            itemView.ivCall.setOnClickListener(this)
            itemView.ivMoreInfo.setOnClickListener(this)
        }

        fun bindData(user: User) {
            tvName.text = user.userName
            tvAddress.text = user.block.plus(", ").plus(user.district).plus(", ").plus(user.state)
            civGender.setImageResource(R.drawable.ic_male_user)
        }

        override fun onClick(view: View) {
            when (view.id) {
                R.id.constraintLayout, R.id.ivCall -> {
                    if (::itemClickListener.isInitialized)
                        itemClickListener.onItemClick(adapterPosition, dataList[adapterPosition])
                }
                R.id.ivMoreInfo -> {
                    if (::itemClickListener.isInitialized)
                        itemClickListener.onMoreInfoClick(
                            adapterPosition,
                            dataList[adapterPosition]
                        )
                }
            }
        }
    }
}