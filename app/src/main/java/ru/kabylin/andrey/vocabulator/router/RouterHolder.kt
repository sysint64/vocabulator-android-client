package ru.kabylin.andrey.vocabulator.router

import ru.kabylin.andrey.vocabulator.views.ScreenTransition

interface RouterHolder {
    fun transitionUpdate(screenTransition: ScreenTransition<*>?) {}
}
