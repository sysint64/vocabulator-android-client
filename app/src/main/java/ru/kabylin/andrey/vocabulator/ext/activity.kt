package ru.kabylin.andrey.vocabulator.ext

import android.app.Activity
import android.content.Context
import android.support.annotation.LayoutRes
import android.view.inputmethod.InputMethodManager
import ru.kabylin.andrey.vocabulator.views.ActivityDelegate

@Suppress("UNCHECKED_CAST")
fun <T : Activity> Activity.setContentView(@LayoutRes layout: Int, delegate: ActivityDelegate<T>? = null) {
    delegate?.setContentView(layout, this as T) ?: setContentView(layout)
}

fun Activity.closeKeyboard() {
    val view = currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
