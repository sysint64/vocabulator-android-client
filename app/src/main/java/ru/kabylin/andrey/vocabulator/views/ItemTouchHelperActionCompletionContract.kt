package ru.kabylin.andrey.vocabulator.views

interface ItemTouchHelperActionCompletionContract {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    fun onItemDismiss(position: Int)
}
