package com.nitiaayog.apnesaathi.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class BaseArrayAdapter : ArrayAdapter<String?> {

    constructor(context: Context, resource: Int, objects: Array<String?>) :
            super(context, resource, objects)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? = null

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            }
        }
    }
}