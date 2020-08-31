package com.nitiaayog.apnesaathi.base.calbacks

/**
 * Interface for listening to the item clicks.
 */
interface OnItemClickListener<T> {
    /**
     * Method which listens for the item click.
     * [position] is the position of the clicked item
     * [data] is the item which was clicked
     */
    fun onItemClick(position: Int, data: T)

    /**
     * Method which listens for the i button click.
     * [position] is the position of the clicked item
     * [data] is the item which was clicked
     */
    fun onMoreInfoClick(position: Int, data: T) {}
}