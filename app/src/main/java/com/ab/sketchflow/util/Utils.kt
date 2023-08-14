package com.ab.sketchflow.util

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import com.jakewharton.rxbinding.BuildConfig
import java.io.ByteArrayOutputStream

object Utils {

    fun isBuildVariantDebug(): Boolean = BuildConfig.BUILD_TYPE == Constants.BUILD_TYPE_DEBUG

    fun View.showVisibility(condition: Boolean) {
        this.visibility = if (condition) View.VISIBLE else View.GONE
    }

    fun List<View>.showVisibility(condition: Boolean) {
        forEach {
            it.showVisibility(condition)
        }
    }

    fun Int.backgroundTintValue() : ColorStateList{
        return ColorStateList.valueOf(this)
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun byteArrayToBitmap(byteArray: ByteArray) : Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}