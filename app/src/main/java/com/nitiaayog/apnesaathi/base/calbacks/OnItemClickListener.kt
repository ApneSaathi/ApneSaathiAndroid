package com.nitiaayog.apnesaathi.base.calbacks

interface OnItemClickListener<T> {
    fun onItemClick(position: Int, data: T)
    fun onMoreInfoClick(position: Int, data: T){}
}