package ru.kabylin.andrey.vocabulator.views

import android.content.Context
import android.view.View
import java.util.*

class SingleSwipableItemRecyclerAdapter<T>(
    context: Context,
    private val mutableItems: MutableList<T>,
    val layout: Int,
    private val holderFactory: (context: Context, view: View) -> RecyclerItemHolder<T>,
    onItemClick: ((item: T) -> Unit)? = null
) : ItemRecyclerAdapter<T>(context, mutableItems, onItemClick), ItemTouchHelperActionCompletionContract {

    override fun obtainLayout(viewType: Int): Int {
        return layout
    }

    override fun obtainHolder(viewType: Int, view: View): RecyclerItemHolder<T> {
        return holderFactory.invoke(context, view)
    }

    override fun onItemDismiss(position: Int) {
        mutableItems.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mutableItems, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mutableItems, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }
}
