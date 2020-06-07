package com.nitiaayog.apnesaathi.utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.nitiaayog.apnesaathi.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object ImageProcessor {

    private val TAG = ImageProcessor::class.java.simpleName

    private const val URL_PREFIX: String = "http://"

    private val requestOptions: RequestOptions by lazy {
        RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA)
            .fallback(R.drawable.ic_place_holder).placeholder(R.drawable.ic_place_holder)
            .error(R.drawable.ic_place_holder)
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    private fun validateImageUrl(imageUrl: String): String =
        if (imageUrl.startsWith("http")) imageUrl else "$URL_PREFIX$imageUrl"

    @Throws(Exception::class)
    fun getImageFromUrl(imageUrl: String): Bitmap? {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input = connection.inputStream
        return BitmapFactory.decodeStream(input)
    }

    fun loadProfilePic(imageView: ImageView, imageUrl: String = "") =
        Glide.with(imageView.context).load(validateImageUrl(imageUrl))
            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
            .apply(requestOptions).into(imageView)

    fun loadResizedProfilePic(imageView: ImageView, imageUrl: String = "", size: Int) =
        Glide.with(imageView.context).load(validateImageUrl(imageUrl))
            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
            .apply(requestOptions.override(size, size))
            .into(imageView)

    fun loadAdvertisement(advertisementUrl: String, imageView: ImageView) =
        Glide.with(imageView.context).load(validateImageUrl(advertisementUrl))
            .thumbnail(0.1F).transition(GenericTransitionOptions.with(android.R.anim.fade_in))
            .apply(requestOptions).into(imageView)

    fun loadThumbnailFromUrl(imageUrl: String, imageView: ImageView) =
        Glide.with(imageView.context).load(validateImageUrl(imageUrl)).thumbnail(0.1F)
            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
            .apply(requestOptions).into(imageView)

    fun loadThumbnailFromDrawableResource(@DrawableRes imageResource: Int, imageView: ImageView) =
        Glide.with(imageView.context).load(imageResource)
            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
            .fallback(R.drawable.ic_place_holder)
            .placeholder(R.drawable.ic_place_holder).error(R.drawable.ic_place_holder)
            .into(imageView)

    fun loadThumbnailFromFile(imagePath: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(File(FileIO.SentFolderAbsolutePath.plus(imagePath)))
            .thumbnail(0.1F)
            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
            .apply(requestOptions)
            .into(imageView)
    }

    fun loadThumbnailFromFile(imagePath: File, imageView: ImageView) {
        Glide.with(imageView.context).load(imagePath)
            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
            .apply(requestOptions)
            .into(imageView)
    }

    @Throws(Exception::class)
    fun getBase64String(bitmap: Bitmap?): String {
        if (bitmap != null) {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT) ?: ""
        }
        return ""
    }
}