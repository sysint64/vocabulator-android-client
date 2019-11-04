package ru.kabylin.andrey.vocabulator.ui.holders

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.item_language.view.*
import ru.kabylin.andrey.vocabulator.services.LanguagesService
import ru.kabylin.andrey.vocabulator.views.RecyclerItemHolder

class LanguageHolder(context: Context, view: View) : RecyclerItemHolder<LanguagesService.Language>(context, view) {
    override fun bind(data: LanguagesService.Language) {
        with(view) {
            languageNameTextView.text = data.name
        }
    }
}
