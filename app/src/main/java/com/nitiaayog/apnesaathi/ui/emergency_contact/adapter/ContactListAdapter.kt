package com.nitiaayog.apnesaathi.ui.emergency_contact.adapter

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
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import kotlinx.android.synthetic.main.contact_data_adapter.view.*

open class ContactListAdapter(
    var mContext: Context,
    var list: List<ContactDummyData>,
    pageTitle: String?, var itemClickListener: ItemClickListener
) :
    RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {
    interface ItemClickListener {
        fun itemClick(data: ContactDummyData)
    }
    var titlenew=pageTitle
    inner class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = itemView.TxtTitle
        var TxtNumber: TextView = itemView.TxtNumber
        var ic_callImage: ImageView = itemView.ic_callImage

        fun bindContact_Data(data: ContactDummyData) {

            if (titlenew.equals(ApiConstants.titleApneSathiConsulatant)) {
                title.visibility = VISIBLE
                title.setText(data.title)

            } else {
                title.visibility = GONE
            }
            TxtNumber.setText(data.contactnumber)

            ic_callImage.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    itemClickListener.itemClick(data)
                }

            })

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_data_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindContact_Data(list[position])
    }
}