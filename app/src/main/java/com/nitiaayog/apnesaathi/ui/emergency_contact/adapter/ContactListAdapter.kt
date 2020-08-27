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
import java.util.*

open class ContactListAdapter(private val context: Context) :
    RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {
    private lateinit var itemClickListener: ItemClickListener

    interface ItemClickListener {
        fun itemClick(data: ContactRealData)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    private var data: List<ContactRealData> = mutableListOf()
    var titlenew = ""

    inner class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = itemView.TxtTitle
        var TxtNumber: TextView = itemView.TxtNumber
        var ic_callImage: ImageView = itemView.ic_callImage

        fun bindContact_Data(data: ContactRealData) {

            if (titlenew == ApiConstants.titleApneSathiConsulatant) {
                title.visibility = VISIBLE
                title.text = data.title

            } else {
                title.visibility = GONE
            }
            TxtNumber.text = data.contactnumber

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
        holder.bindContact_Data(data[position])
    }

    fun setData(contactList: ArrayList<ContactRealData>) {
        data = contactList
    }

    fun setNewTitle(title: String?) {
        titlenew = title ?: ""
    }
}