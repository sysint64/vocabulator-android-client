package ru.kabylin.andrey.vocabulator.views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class ItemRecyclerAdapter<T>(
    protected val context: Context,
    val items: List<T>,
    var onItemClick: ((item: T) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerItemHolder<T>>() {

    abstract fun obtainLayout(viewType: Int): Int

    abstract fun obtainHolder(viewType: Int, view: View): RecyclerItemHolder<T>

    protected fun inflateItemView(parent: ViewGroup?, viewType: Int): View {
        val inflater = LayoutInflater.from(parent?.context)
        return inflater.inflate(obtainLayout(viewType), parent, false)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerItemHolder<T>, position: Int) {
        val item = items[position]
        holder.bind(item)
        onItemClick?.let { holder.setOnItemClick(item, it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemHolder<T> {
        val view = inflateItemView(parent, viewType)
        return obtainHolder(viewType, view)
    }

    fun performClick(position: Int) {
        onItemClick?.invoke(items[position])
    }
}
