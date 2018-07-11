package ru.kabylin.andrey.vocabulator.views

import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import ru.kabylin.andrey.vocabulator.R

fun Toolbar.attachToActivity(
    activity: AppCompatActivity,
    withSideMenuIcon: Boolean = false,
    displayHomeButton: Boolean = false
) {
    activity.setSupportActionBar(this)
    val actionBar = activity.supportActionBar

    if (actionBar != null) {
        actionBar.setDisplayShowTitleEnabled(true)
        actionBar.title = activity.title

        if (displayHomeButton)
            actionBar.setDisplayHomeAsUpEnabled(true)

        if (withSideMenuIcon)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
    }
}

fun Toolbar.setTitle(activity: AppCompatActivity, title: String) {
    activity.supportActionBar?.title = title
    this.title = title
}

fun Toolbar.setTitle(activity: AppCompatActivity, @StringRes title: Int) {
    activity.supportActionBar?.setTitle(title)
    this.setTitle(title)
}
