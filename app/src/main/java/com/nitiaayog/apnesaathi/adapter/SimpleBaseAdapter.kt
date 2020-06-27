package com.nitiaayog.apnesaathi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import kotlinx.android.synthetic.main.list_item_multi_auto_complete_recyclerview.view.*

class SimpleBaseAdapter : RecyclerView.Adapter<SimpleBaseAdapter.SimpleViewHolder>() {

    private val dataList: MutableList<String> = mutableListOf()
    private lateinit var onIteClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_multi_auto_complete_recyclerview, parent, false)
        return SimpleViewHolder(customView)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) =
        holder.bindData(dataList[position])

    interface OnItemClickListener {
        fun onItemClick(position: Int, name: String)
        fun showPopup()
    }

    fun setOnItemClickListener(onIteClickListener: OnItemClickListener) {
        this.onIteClickListener = onIteClickListener
    }

    fun resetAdapter() {
        dataList.clear()
        notifyDataSetChanged()
    }

    fun addItem(name: String) {
        val selectedItems = dataList.filter { it == name }
        if (selectedItems.isEmpty()) {
            dataList.add(name)
            notifyDataSetChanged()
        }
    }

    fun removeItem(name: String) {
        val selectedItems = dataList.filter { it == name }
        if (selectedItems.isNotEmpty()) {
            val itemName = selectedItems[0]
            var itemIndex = -1
            dataList.forEachIndexed { index, iName -> if (itemName == iName) itemIndex = index }
            if (itemIndex != -1) {
                dataList.removeAt(itemIndex)
                notifyItemRemoved(itemIndex)
            }
        }
    }

    inner class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvItemName: TextView = itemView.tvItemName
        private val ivClose: ImageView = itemView.ivClose

        fun bindData(name: String) {
            tvItemName.text = name
            tvItemName.setOnClickListener {
                if (::onIteClickListener.isInitialized) onIteClickListener.showPopup()
            }
            ivClose.setOnClickListener {
                val position = adapterPosition
                dataList.removeAt(position)
                notifyItemRemoved(position)
                if (::onIteClickListener.isInitialized)
                    onIteClickListener.onItemClick(position, name)
            }
        }
    }
}