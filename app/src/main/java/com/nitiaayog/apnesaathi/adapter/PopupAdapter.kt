package com.nitiaayog.apnesaathi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.model.FormElements
import kotlinx.android.synthetic.main.list_item_multi_auto_complete_textview.view.*

/**
 *  Adapter for showing the popups in the app.!
 * [RecyclerView.Adapter] is the default adapter from android library
 * [PopupAdapter.PopupViewHolder] is the view holder for holding the views that are available in this page
 */
class PopupAdapter(private val dataList: MutableList<FormElements>) :
    RecyclerView.Adapter<PopupAdapter.PopupViewHolder>() {

    private lateinit var onIteClickListener: OnItemClickListener<FormElements>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopupViewHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_multi_auto_complete_textview, parent, false)
        return PopupViewHolder(customView)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: PopupViewHolder, position: Int) =
        holder.bindData(dataList[position])

    /**
     * Method for setting the item click listener.
     * [OnItemClickListener] is the call back event for listening to the item clicks
     */
    fun setOnItemClickListener(onIteClickListener: OnItemClickListener<FormElements>) {
        this.onIteClickListener = onIteClickListener
    }

    /**
     * Method for updating the bind data
     * [name] is the data to be updated
     */
    fun updateItem(name: String) {
        dataList.forEachIndexed { index, formElements ->
            if (formElements.name == name) {
                formElements.isSelected = false
                notifyItemChanged(index)
                return
            }
        }
    }

    /**
     * Method for resetting the adapter
     */
    fun resetAdapter() {
        dataList.forEach { if (it.isSelected) it.isSelected = false }
        notifyDataSetChanged()
    }

    /**
     * View Holder for holding the views associated with this page.
     * [itemView] is the parent item view which holds the individual views
     * [RecyclerView.ViewHolder] is the default android class
     */
    inner class PopupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cbItemChecked: CheckBox = itemView.cbItemChecked

        /**
         * Method for binding the data
         * [element] is the data which will be providing the values for binding the fields
         */
        fun bindData(element: FormElements) {
            cbItemChecked.text = element.name
            cbItemChecked.isChecked = element.isSelected
            cbItemChecked.setOnClickListener {
                element.isSelected = !element.isSelected
                if (::onIteClickListener.isInitialized)
                    onIteClickListener.onItemClick(adapterPosition, element)
            }
        }
    }
}