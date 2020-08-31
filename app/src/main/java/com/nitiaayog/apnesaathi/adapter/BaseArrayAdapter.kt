package com.nitiaayog.apnesaathi.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

/**
 *  An adapter for filtering the values
 * [ArrayAdapter] is the default adapter from android library.
 */
class BaseArrayAdapter(context: Context, resource: Int, objects: Array<String?>) :
    ArrayAdapter<String?>(context, resource, objects) {

    /**
     * Method for filtering the values.
     *  [Filter] is a default class from the android library
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? = null

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            }
        }
    }
}