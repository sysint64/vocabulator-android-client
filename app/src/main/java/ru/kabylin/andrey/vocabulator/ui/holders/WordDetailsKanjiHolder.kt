package ru.kabylin.andrey.vocabulator.ui.holders

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.item_kanji.view.*
import ru.kabylin.andrey.vocabulator.ext.hideView
import ru.kabylin.andrey.vocabulator.ext.showView
import ru.kabylin.andrey.vocabulator.ui.models.WordDetailsItemVariant
import ru.kabylin.andrey.vocabulator.views.RecyclerItemHolder

class WordDetailsKanjiHolder(context: Context, view: View) : RecyclerItemHolder<WordDetailsItemVariant>(context, view) {
    override fun bind(data: WordDetailsItemVariant) =
        with(view) {
            val kanji = data.kanji!!

            hieroglyphTextView.text = kanji.hieroglyph

            if (kanji.reading.isNotBlank()) {
                readingTextView.text = kanji.reading
            } else {
                readingTextView.text = "???"
            }

            if (kanji.meaning.isNotBlank()) {
                meaningTextView.text = kanji.meaning
                meaningTextView.showView()
            } else {
                meaningTextView.hideView()
            }
        }
}
