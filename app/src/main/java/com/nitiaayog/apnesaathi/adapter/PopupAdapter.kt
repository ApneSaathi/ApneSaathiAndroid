package com.nitiaayog.apnesaathi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.model.FormElements
import kotlinx.android.synthetic.main.list_item_multi_auto_complete_textview.view.*

class PopupAdapter(private val dataList: MutableList<FormElements>) :
    RecyclerView.Adapter<PopupAdapter.TempViewHolder>() {

    private lateinit var onIteClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TempViewHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_multi_auto_complete_textview, parent, false)
        return TempViewHolder(customView)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: TempViewHolder, position: Int) =
        holder.bindData(dataList[position])

    interface OnItemClickListener {
        fun onItemClicked(position: Int, formElement: FormElements)
    }

    fun setOnItemClickListener(onIteClickListener: OnItemClickListener) {
        this.onIteClickListener = onIteClickListener
    }

    fun updateItem(name: String) {
        dataList.forEachIndexed { index, formElements ->
            if (formElements.name == name) {
                formElements.isSelected = false
                notifyItemChanged(index)
                return
            }
        }
    }

    fun resetAdapter() {
        dataList.forEach { if (it.isSelected) it.isSelected = false }
        notifyDataSetChanged()
    }

    inner class TempViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cbItemChecked: CheckBox = itemView.cbItemChecked

        fun bindData(formElement: FormElements) {
            cbItemChecked.text = formElement.name
            cbItemChecked.isChecked = formElement.isSelected
            cbItemChecked.setOnClickListener {
                formElement.isSelected = !formElement.isSelected
                if (::onIteClickListener.isInitialized)
                    onIteClickListener.onItemClicked(adapterPosition, formElement)
            }
        }
    }
}