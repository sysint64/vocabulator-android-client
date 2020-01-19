package ru.kabylin.andrey.vocabulator.ui

import android.content.Context
import ru.kabylin.andrey.vocabulator.router.Router
import ru.kabylin.andrey.vocabulator.views.ScreenTransition
import ru.kabylin.andrey.vocabulator.views.ScreenTransitionEnum

enum class Routes : ScreenTransitionEnum {
    LANGUAGES,
    LIST,
    DETAILS,
    TRAIN_MENU,
    TRAIN,
    ADD_WORD,
    SETTINGS,
    ;

    companion object {
        const val RESULT_CODE_TRAIN_MENU = 4001
    }
}

class AppRouter(context: Context) : Router(context) {
    override fun transitionUpdate(screenTransition: ScreenTransition<*>?) {
        super.transitionUpdate(screenTransition)
        val transition = screenTransition?.transition as? Routes
            ?: return

        when (transition) {
            Routes.TRAIN_MENU ->
                screenTransition.startActivityForResult(
                    context,
                    TrainMenuActivity::class.java,
                    Routes.RESULT_CODE_TRAIN_MENU
                )

            Routes.LANGUAGES ->
                screenTransition.startActivity(
                    context,
                    LanguagesListActivity::class.java
                )

            Routes.LIST ->
                screenTransition.startActivity(
                    context,
                    WordListActivity::class.java
                )

            Routes.DETAILS ->
                screenTransition.startActivity(
                    context,
                    WordDetailsActivity::class.java
                )

            Routes.TRAIN ->
                screenTransition.startActivity(
                    context,
                    TrainActivity::class.java
                )

            Routes.ADD_WORD ->
                screenTransition.startActivity(
                    context,
                    AddWordActivity::class.java
                )

            Routes.SETTINGS ->
                screenTransition.startActivity(
                    context,
                    SettingsActivity::class.java
                )
        }
    }
}
