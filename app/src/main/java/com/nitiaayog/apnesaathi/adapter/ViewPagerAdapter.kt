package com.nitiaayog.apnesaathi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.utility.ImageProcessor
import kotlinx.android.synthetic.main.view_pager_item_layout.view.*

class ViewPagerAdapter : PagerAdapter() {

    private val imageResources =
        intArrayOf(0, 1, 2)

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val customView = LayoutInflater.from(container.context)
            .inflate(R.layout.view_pager_item_layout, container, false)
        ImageProcessor.loadThumbnailFromDrawableResource(
            imageResources[position], customView.ivImage
        )
        container.addView(customView)
        return customView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int = imageResources.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) =
        container.removeView(`object` as View)
}
