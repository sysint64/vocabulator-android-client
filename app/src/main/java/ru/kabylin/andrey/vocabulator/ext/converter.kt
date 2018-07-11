package ru.kabylin.andrey.vocabulator.ext

import android.content.Context
import android.util.TypedValue

fun Int.getInPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}
