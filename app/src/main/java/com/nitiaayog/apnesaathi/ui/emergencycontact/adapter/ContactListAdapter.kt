package com.nitiaayog.apnesaathi.ui.emergencycontact.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import kotlinx.android.synthetic.main.contact_data_adapter.view.*
import java.util.*

class ContactListAdapter(private val context: Context) :
    RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {
    private lateinit var itemClickListener: ItemClickListener

    interface ItemClickListener {
        fun itemClick(data: ContactRealData)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    private var data: List<ContactRealData> = mutableListOf()

    inner class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var TxtName: TextView = itemView.TxtName
        var TxtNumber: TextView = itemView.TxtNumber
        var ic_callImage: ImageView = itemView.ic_callImage

        fun bindContactData(data: ContactRealData) {

            if (data.Name.isNotEmpty()) {
                TxtName.visibility = VISIBLE
                TxtName.text = context.getString(R.string.name) +" : " + data.Name
            } else {
                TxtName.visibility = GONE
            }
            TxtNumber.text = context.getString(R.string.contact_number) +" : "+ data.contactnumber

            ic_callImage.setOnClickListener { itemClickListener.itemClick(data) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_data_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindContactData(data[position])
    }

    fun setData(contactList: ArrayList<ContactRealData>) {
        data = contactList
    }


}