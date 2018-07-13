package ru.kabylin.andrey.vocabulator.views

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_DRAG

class GridDragAndDropItemTouchHelperCallback(
    private val actionCompletionContract: ItemTouchHelperActionCompletionContract
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

        return makeFlag(ACTION_STATE_DRAG, dragFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return actionCompletionContract.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
}
