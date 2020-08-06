package com.nitiaayog.apnesaathi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.CircleImageView
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.model.Volunteer
import kotlinx.android.synthetic.main.list_item_connected_calls.view.*

/*private val diffCallback: DiffUtil.ItemCallback<Volunteer> =
    object : DiffUtil.ItemCallback<Volunteer>() {
        override fun areItemsTheSame(oldItem: Volunteer, newItem: Volunteer): Boolean =
            TextUtils.equals(oldItem.id.toString(), newItem.id.toString())

        override fun areContentsTheSame(oldItem: Volunteer, newItem: Volunteer): Boolean =
            oldItem == newItem
    }

private val config = AsyncDifferConfig.Builder<Volunteer>(diffCallback).build()*/

class VolunteersAdapter : RecyclerView.Adapter<VolunteersAdapter.VolunteerHolder>() {

    companion object {
        private val TAG: String = "TAG -- ${VolunteersAdapter::class.java.simpleName} -->"
    }

    private val volunteers: MutableList<Volunteer> = mutableListOf()

    private lateinit var itemClickListener: OnItemClickListener<Volunteer>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_connected_calls, parent, false)
        return VolunteerHolder(customView)
    }

    override fun onBindViewHolder(holder: VolunteerHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    override fun getItemCount(): Int {
        return volunteers.size
    }

    private fun getItem(position: Int): Volunteer {
        return volunteers[position]
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener<Volunteer>) {
        this.itemClickListener = itemClickListener
    }

    fun setData(volunteers: MutableList<Volunteer>) {
        if (this.volunteers.isNotEmpty()) this.volunteers.clear()
        this.volunteers.addAll(volunteers)
        notifyDataSetChanged()
    }

    inner class VolunteerHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val civGender: CircleImageView = itemView.civGender
        private val tvName: TextView = itemView.tvName
        private val tvAddress: TextView = itemView.tvAddress
        private val tvDate: TextView = itemView.tvDate

        init {
            tvAddress.isSelected = true

            itemView.constraintLayout.setOnClickListener(this)
            itemView.ivCall.setOnClickListener(this)
            itemView.ivMoreInfo.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val data: Volunteer = volunteers[adapterPosition]
            when (view.id) {
                R.id.ivCall -> {
                    if (::itemClickListener.isInitialized)
                        itemClickListener.onItemClick(adapterPosition, data)
                }
                R.id.ivMoreInfo -> {
                    if (::itemClickListener.isInitialized)
                        itemClickListener.onMoreInfoClick(adapterPosition, data)
                }
                R.id.constraintLayout -> {
                }
            }
        }

        private fun clear() {
            tvName.text = ""
            tvAddress.text = ""
            tvDate.text = ""
        }

        fun bindData(volunteer: Volunteer?) {
            if (volunteer == null) clear() else {
                tvName.text = volunteer.firstName!!.plus(" ").plus(volunteer.lastName)
                tvAddress.text = volunteer.address
                civGender.setImageResource(
                    if (volunteer.gender == "M") R.drawable.ic_volunteer_male else R.drawable.ic_volunteer_female
                )
                //tvDate.text = BaseUtility.getFormattedDate(volunteer.loggedDateTime ?: "")
            }
        }
    }
}