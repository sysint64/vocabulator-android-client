package ru.kabylin.andrey.vocabulator.ext

import android.support.design.widget.TabLayout
import android.view.View
import android.widget.LinearLayout

fun View.disable() {
    isEnabled = false
}

fun View.enable() {
    isEnabled = true
}

fun TabLayout.disable() {
    val tabStrip = getChildAt(0) as LinearLayout

    for (i in 0 until tabStrip.childCount)
        tabStrip.getChildAt(i).setOnTouchListener { _, _ -> true }
}

fun TabLayout.enable() {
    val tabStrip = getChildAt(0) as LinearLayout

    for (i in 0 until tabStrip.childCount)
        tabStrip.getChildAt(i).setOnTouchListener(null)
}

fun View.showView() {
    visibility = View.VISIBLE
}

fun View.hideView() {
    visibility = View.GONE
}

fun View.invisibleView() {
    visibility = View.INVISIBLE
}

fun View.isVisible(): Boolean =
    visibility == View.VISIBLE

fun View.isHidden(): Boolean =
    visibility != View.VISIBLE

fun View.setVisibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}
