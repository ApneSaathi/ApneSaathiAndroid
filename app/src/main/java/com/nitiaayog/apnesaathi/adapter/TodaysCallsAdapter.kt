package com.nitiaayog.apnesaathi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.model.CallsConnected
import kotlinx.android.synthetic.main.list_item_connected_calls.view.*

class TodaysCallsAdapter(private val dataList: MutableList<CallsConnected>) :
    RecyclerView.Adapter<TodaysCallsAdapter.TodaysCallsViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int, callsConnected: CallsConnected)
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

    inner class TodaysCallsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.tvName
        val tvAge: TextView = itemView.tvAge
        val tvPhoneNumber: TextView = itemView.tvPhoneNumber
        val tvPlace: TextView = itemView.tvPlace
        val tvIssues: TextView = itemView.tvIssues

        init {
            itemView.constraintLayout.setOnClickListener {
                if (::itemClickListener.isInitialized)
                    itemClickListener.onItemClick(adapterPosition, dataList[adapterPosition])
            }
        }

        fun bindData(callsConnected: CallsConnected) {
            tvName.text = callsConnected.name
            tvAge.text = callsConnected.age.plus(" yrs")
            tvPhoneNumber.text = callsConnected.phoneNumber
            tvPlace.text = callsConnected.district.plus(", ").plus(callsConnected.state)
            tvIssues.text = callsConnected.issues
        }
    }
}