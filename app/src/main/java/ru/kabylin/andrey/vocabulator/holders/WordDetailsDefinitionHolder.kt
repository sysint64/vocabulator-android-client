package ru.kabylin.andrey.vocabulator.holders

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.item_definition.view.*
import ru.kabylin.andrey.vocabulator.R
import ru.kabylin.andrey.vocabulator.WordDetailsItemVariant
import ru.kabylin.andrey.vocabulator.ext.hideView
import ru.kabylin.andrey.vocabulator.ext.showView
import ru.kabylin.andrey.vocabulator.views.RecyclerItemHolder
import ru.kabylin.andrey.vocabulator.views.SingleItemRecyclerAdapter

class WordDetailsDefinitionHolder(context: Context, view: View) : RecyclerItemHolder<WordDetailsItemVariant>(context, view) {
    private val recyclerAdapter by lazy {
        SingleItemRecyclerAdapter(context, items, R.layout.item_synonym, ::SynonymHolder)
    }

    private val items = ArrayList<String>()

    override fun bind(data: WordDetailsItemVariant) =
        with(view) {
            val definition = data.definition!!

            titleTextView.text = definition.title
            descTextView.text = definition.desc

            if (!definition.example.isBlank()) {
                exampleTextView.showView()
                exampleLabelTextView.showView()
                exampleTextView.text = definition.example
            }
            else {
                exampleTextView.hideView()
                exampleLabelTextView.hideView()
            }

            items.clear()
            items.addAll(definition.synonyms)

            if (items.isEmpty()) {
                synonymsLabelTextView.hideView()
                synonymsRecyclerView.hideView()
            }
            else {
                synonymsLabelTextView.showView()
                synonymsRecyclerView.showView()
            }

            synonymsRecyclerView.adapter = recyclerAdapter
            recyclerAdapter.notifyDataSetChanged()
        }
}
