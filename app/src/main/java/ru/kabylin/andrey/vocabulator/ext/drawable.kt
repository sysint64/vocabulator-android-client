package ru.kabylin.andrey.vocabulator.ext

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat

fun Drawable.tint(context: Context, @ColorRes colorRes: Int) {
    val color = ContextCompat.getColor(context, colorRes)
    setColorFilter(color, PorterDuff.Mode.SRC_IN)
}
