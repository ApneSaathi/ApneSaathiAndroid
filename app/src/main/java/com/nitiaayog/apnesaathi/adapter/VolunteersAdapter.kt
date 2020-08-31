package com.nitiaayog.apnesaathi.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.CircleImageView
import com.nitiaayog.apnesaathi.base.calbacks.OnItemClickListener
import com.nitiaayog.apnesaathi.model.Volunteer
import kotlinx.android.synthetic.main.list_item_connected_calls.view.*

private val diffCallback: DiffUtil.ItemCallback<Volunteer> =
    object : DiffUtil.ItemCallback<Volunteer>() {
        override fun areItemsTheSame(oldItem: Volunteer, newItem: Volunteer): Boolean =
            TextUtils.equals(oldItem.id.toString(), newItem.id.toString())

        override fun areContentsTheSame(oldItem: Volunteer, newItem: Volunteer): Boolean =
            oldItem == newItem
    }

private val config = AsyncDifferConfig.Builder<Volunteer>(diffCallback).build()

/**
 *  Adapter for showing the details of volunteers
 * [RecyclerView.Adapter] is the default adapter from android library
 * [VolunteersAdapter.VolunteerHolder] is the view holder for holding the views that are available in this page
 * [View.OnClickListener] is the default class from android library for handling the item clicks
 */
class VolunteersAdapter : PagedListAdapter<Volunteer, VolunteersAdapter.VolunteerHolder>(config) {

    companion object {
        private val TAG: String = "TAG -- ${VolunteersAdapter::class.java.simpleName} -->"
    }

    //private val volunteers: MutableList<Volunteer> = mutableListOf()

    private lateinit var itemClickListener: OnItemClickListener<Volunteer>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerHolder {
        val customView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_connected_calls, parent, false)
        return VolunteerHolder(customView)
    }

    override fun onBindViewHolder(holder: VolunteerHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    /**
     * Method for setting the item click listener.
     * [OnItemClickListener] is the call back event for listening to the item clicks
     */
    fun setOnItemClickListener(itemClickListener: OnItemClickListener<Volunteer>) {
        this.itemClickListener = itemClickListener
    }

    /**
     * View Holder for holding the views associated with this page.
     * [itemView] is the parent item view which holds the individual views
     * [RecyclerView.ViewHolder] is the default android class
     */
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

            val padding: Int = itemView.context.resources.getDimensionPixelOffset(R.dimen.dimen_4)
            itemView.setPadding(padding, 0, padding, 0)
        }

        override fun onClick(view: View) {
            val data: Volunteer? = getItem(adapterPosition)
            data?.run {
                when (view.id) {
                    R.id.ivCall -> {
                        if (::itemClickListener.isInitialized)
                            itemClickListener.onItemClick(adapterPosition, this)
                    }
                    R.id.ivMoreInfo -> {
                        if (::itemClickListener.isInitialized)
                            itemClickListener.onMoreInfoClick(adapterPosition, this)
                    }
                    R.id.constraintLayout -> {
                    }
                }
            }
        }

        /**
         * Method for clearing the text fields
         */
        private fun clear() {
            tvName.text = ""
            tvAddress.text = ""
            tvDate.text = ""
        }

        /**
         * Method for binding the data
         * [volunteer] is the data which will be providing the values for binding the fields
         */
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