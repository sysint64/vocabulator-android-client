package ru.kabylin.andrey.vocabulator.views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

abstract class RecyclerItemHolder<T>(
    protected val context: Context,
    protected val view: View
) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: T)

    open fun setOnItemClick(data: T, onItemClick: (data: T) -> Unit) {
        view.setOnClickListener { onItemClick.invoke(data) }
    }
}
