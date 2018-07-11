package ru.kabylin.andrey.vocabulator.ext

import android.content.Context
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.support.v4.content.ContextCompat
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.widget.ImageView

@Throws(IOException::class)
fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = context.getExternalFilesDir("captured_images")

    return File.createTempFile(imageFileName, ".jpg", storageDir)
}

fun ImageView.setTintImage(@DrawableRes icon: Int, @ColorRes color: Int) {
    val tintColor = ContextCompat.getColor(context, color)
    this.setImageResource(icon)
    this.setColorFilter(tintColor)
}

fun ImageView.setTint(@ColorRes color: Int) {
    val tintColor = ContextCompat.getColor(context, color)
    this.setColorFilter(tintColor)
}
