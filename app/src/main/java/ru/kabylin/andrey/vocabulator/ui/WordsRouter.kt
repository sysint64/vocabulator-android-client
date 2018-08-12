package ru.kabylin.andrey.vocabulator.ui

import android.content.Context
import ru.kabylin.andrey.vocabulator.router.Router
import ru.kabylin.andrey.vocabulator.views.ScreenTransition
import ru.kabylin.andrey.vocabulator.views.ScreenTransitionEnum

enum class WordsScreens : ScreenTransitionEnum {
    LIST,
    DETAILS,
    TRAIN,
    ADD_WORD
    ;
}

class WordsRouter(context: Context) : Router(context) {
    override fun transitionUpdate(screenTransition: ScreenTransition<*>?) {
        super.transitionUpdate(screenTransition)
        val transition = screenTransition?.transition as? WordsScreens
            ?: return

        when (transition) {
            WordsScreens.LIST ->
                screenTransition.startActivity(
                    context,
                    WordListActivity::class.java
                )

            WordsScreens.DETAILS ->
                screenTransition.startActivity(
                    context,
                    WordDetailsActivity::class.java
                )

            WordsScreens.TRAIN ->
                screenTransition.startActivity(
                    context,
                    TrainActivity::class.java
                )

            WordsScreens.ADD_WORD ->
                screenTransition.startActivity(
                    context,
                    AddWordActivity::class.java
                )
        }
    }
}
