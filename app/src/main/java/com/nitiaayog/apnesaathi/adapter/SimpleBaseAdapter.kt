package com.nitiaayog.apnesaathi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import kotlinx.android.synthetic.main.list_item_multi_auto_complete_recyclerview.view.*

/**
 *  Adapter for showing the details in the feed back form!
 * [RecyclerView.Adapter] is the default adapter from android library
 * [SimpleBaseAdapter.SimpleViewHolder] is the view holder for holding the views that are available in this page
 */
class SimpleBaseAdapter : RecyclerView.Adapter<SimpleBaseAdapter.SimpleViewHolder>() {

    private val dataList: MutableList<String> = mutableListOf()
    private lateinit var onIteClickListener: OnItemClickListener<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_multi_auto_complete_recyclerview, parent, false)
        return SimpleViewHolder(customView)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) =
        holder.bindData(dataList[position])

    /**
     * Method for setting the item click listener.
     * [OnItemClickListener] is the call back event for listening to the item clicks
     */
    fun setOnItemClickListener(onIteClickListener: OnItemClickListener<String>) {
        this.onIteClickListener = onIteClickListener
    }

    /**
     * Method for resetting the adapter values
     */
    fun resetAdapter() {
        dataList.clear()
        notifyDataSetChanged()
    }

    /**
     * Method for adding a new item
     * [name] is the new item to be added
     */
    fun addItem(name: String) {
        val selectedItems = dataList.filter { it == name }
        if (selectedItems.isEmpty()) {
            dataList.add(name)
            notifyDataSetChanged()
        }
    }

    /**
     * Method for removing an item
     * [name] is the item to be removed
     */
    fun removeItem(name: String) {
        val selectedItems = dataList.filter { it == name }
        if (selectedItems.isNotEmpty()) {
            val itemIndex = dataList.indexOf(name)
            dataList.removeAt(itemIndex)
            notifyItemRemoved(itemIndex)
        }
    }

    /**
     * View Holder for holding the views associated with this page.
     * [itemView] is the parent item view which holds the individual views
     * [RecyclerView.ViewHolder] is the default android class
     */
    inner class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvItemName: TextView = itemView.tvItemName
        private val ivClose: ImageView = itemView.ivClose

        /**
         * Method for binding the data
         * [name] is the data which will be providing the values for binding the fields
         */
        fun bindData(name: String) {
            tvItemName.text = name
            tvItemName.setOnClickListener {
                if (::onIteClickListener.isInitialized)
                    onIteClickListener.onMoreInfoClick(0, "")
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