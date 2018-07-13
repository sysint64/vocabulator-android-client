package ru.kabylin.andrey.vocabulator.views

import android.content.Context
import android.view.View

class SingleItemRecyclerAdapter<T>(
    context: Context,
    items: List<T>,
    val layout: Int,
    private val holderFactory: (context: Context, view: View) -> RecyclerItemHolder<T>,
    onItemClick: ((item: T) -> Unit)? = null
) : ItemRecyclerAdapter<T>(context, items, onItemClick) {

    override fun obtainLayout(viewType: Int): Int {
        return layout
    }

    override fun obtainHolder(viewType: Int, view: View): RecyclerItemHolder<T> {
        return holderFactory.invoke(context, view)
    }
}
