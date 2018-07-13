package ru.kabylin.andrey.vocabulator.holders

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.item_single_desc.view.*
import ru.kabylin.andrey.vocabulator.WordDetailsItemVariant
import ru.kabylin.andrey.vocabulator.views.RecyclerItemHolder

class WordDetailsDescHolder(context: Context, view: View) : RecyclerItemHolder<WordDetailsItemVariant>(context, view) {
    override fun bind(data: WordDetailsItemVariant) =
        with(view) {
            descTextView.text = data.desc!!
        }
}
