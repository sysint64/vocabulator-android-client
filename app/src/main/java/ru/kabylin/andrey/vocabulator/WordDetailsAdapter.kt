package ru.kabylin.andrey.vocabulator

import android.content.Context
import android.view.View
import ru.kabylin.andrey.vocabulator.holders.WordDetailsDefinitionHolder
import ru.kabylin.andrey.vocabulator.holders.WordDetailsDescHolder
import ru.kabylin.andrey.vocabulator.holders.WordDetailsListTextHolder
import ru.kabylin.andrey.vocabulator.holders.WordDetailsTitleHolder
import ru.kabylin.andrey.vocabulator.views.DummyRecyclerItemHolder
import ru.kabylin.andrey.vocabulator.views.ItemRecyclerAdapter
import ru.kabylin.andrey.vocabulator.views.RecyclerItemHolder

open class WordDetailsAdapter(
    context: Context,
    items: List<WordDetailsItemVariant>
) : ItemRecyclerAdapter<WordDetailsItemVariant>(context, items) {

    companion object {
        const val VIEW_TYPE_TITLE = 0
        const val VIEW_TYPE_SEPARATOR = 1
        const val VIEW_TYPE_LIST_TEXT_ITEM = 2
        const val VIEW_TYPE_DEFINITION = 3
        const val VIEW_TYPE_DESC = 4
    }

    override fun obtainLayout(viewType: Int): Int =
        when (viewType) {
            VIEW_TYPE_TITLE -> R.layout.item_title
            VIEW_TYPE_SEPARATOR -> R.layout.item_separator
            VIEW_TYPE_LIST_TEXT_ITEM -> R.layout.item_list_text_item
            VIEW_TYPE_DEFINITION -> R.layout.item_definition
            VIEW_TYPE_DESC -> R.layout.item_single_desc
            else -> throw AssertionError("Unspecified view type")
        }

    override fun obtainHolder(viewType: Int, view: View): RecyclerItemHolder<WordDetailsItemVariant> =
        when (viewType) {
            VIEW_TYPE_TITLE -> WordDetailsTitleHolder(context, view)
            VIEW_TYPE_SEPARATOR -> DummyRecyclerItemHolder(context, view)
            VIEW_TYPE_LIST_TEXT_ITEM -> WordDetailsListTextHolder(context, view)
            VIEW_TYPE_DEFINITION -> WordDetailsDefinitionHolder(context, view)
            VIEW_TYPE_DESC -> WordDetailsDescHolder(context, view)
            else -> throw AssertionError("Unspecified view type")
        }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]

        return when {
            item.title != null -> VIEW_TYPE_TITLE
            item.separator != null -> VIEW_TYPE_SEPARATOR
            item.listItem != null -> VIEW_TYPE_LIST_TEXT_ITEM
            item.definition != null -> VIEW_TYPE_DEFINITION
            item.desc != null -> VIEW_TYPE_DESC
            else -> throw AssertionError("Unspecified view type")
        }
    }
}
