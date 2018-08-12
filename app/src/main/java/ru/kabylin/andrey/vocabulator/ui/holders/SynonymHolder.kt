package ru.kabylin.andrey.vocabulator.ui.holders

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.item_synonym.view.*
import ru.kabylin.andrey.vocabulator.views.RecyclerItemHolder

class SynonymHolder(context: Context, view: View) : RecyclerItemHolder<String>(context, view) {
    override fun bind(data: String) =
        with(view) {
            synonymTextView.text = data
        }
}
