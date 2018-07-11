package ru.kabylin.andrey.vocabulator.router

import android.content.Context
import ru.kabylin.andrey.vocabulator.views.CommonScreens
import ru.kabylin.andrey.vocabulator.views.ErrorScreenTransition
import ru.kabylin.andrey.vocabulator.views.ScreenTransition

open class Router(protected val context: Context) : RouterHolder {
    val errorsRouter = ErrorsRouter(context)

    override fun transitionUpdate(screenTransition: ScreenTransition<*>?) {
        val transition = screenTransition?.transition

        when (transition) {
            is CommonScreens -> commonScreenTransition(screenTransition, transition)
            is ErrorScreenTransition -> errorsRouter.errorScreenTransition(screenTransition, transition)
        }
    }

    private fun commonScreenTransition(screenTransition: ScreenTransition<*>, transition: CommonScreens) {
    }
}
