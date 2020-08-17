package com.nitiaayog.apnesaathi.ui.emergency_contact.hospital

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import kotlinx.android.synthetic.main.contact_data_adapter.view.*

open class ContactListAdapter(var mContext: Context, var list: List<ContactData>) :
    RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {
    inner class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = itemView.TxtTitle
        var TxtNumber: TextView = itemView.TxtNumber
        var ic_callImage: ImageView = itemView.ic_callImage

        fun bindContact_Data(data: ContactData) {
            title.setText(data.title)
            TxtNumber.setText(data.contactnumber)
            ic_callImage.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Toast.makeText(mContext, "In Progress..", Toast.LENGTH_LONG).show()
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