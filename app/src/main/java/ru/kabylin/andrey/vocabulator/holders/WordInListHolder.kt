package ru.kabylin.andrey.vocabulator.holders

import android.content.Context
import android.graphics.Color
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import kotlinx.android.synthetic.main.item_word.view.*
import ru.kabylin.andrey.vocabulator.getScoreColor
import ru.kabylin.andrey.vocabulator.services.WordsService
import ru.kabylin.andrey.vocabulator.views.RecyclerItemHolder

class WordInListHolder(context: Context, view: View) : RecyclerItemHolder<WordsService.Word>(context, view) {
    override fun bind(data: WordsService.Word) =
        with(view) {
            wordTextView.text = data.name

            val drawable = DrawableCompat.wrap(wordTextView.background)
            DrawableCompat.setTint(drawable, getScoreColor(180, 230, data.score))
        }
}
