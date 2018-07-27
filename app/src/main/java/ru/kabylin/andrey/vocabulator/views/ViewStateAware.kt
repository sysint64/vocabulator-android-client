package ru.kabylin.andrey.vocabulator.views

import android.os.Bundle
import ru.kabylin.andrey.vocabulator.ext.createBundle
import ru.kabylin.andrey.vocabulator.router.Router

interface ViewStateAware {
    val viewState: ViewState?
    val router: Router?

    /**
     * Перерисовка всего интерфейса, используется в основнои только при
     * полном восстановлении состояния экрана.
     */
    fun viewStateRefresh() {
        router?.transitionUpdate(viewState?.screenTransition)
    }

    fun unsubscribe() {}

    fun <T : ScreenTransitionEnum> gotoScreen(screen: T) {
        viewState?.screenTransition = ScreenTransition(screen)
        router?.transitionUpdate(viewState?.screenTransition)
    }

    fun <T : ScreenTransitionEnum> gotoScreen(screen: T, bundle: Bundle) {
        viewState?.screenTransition = ScreenTransition(screen, bundle)
        router?.transitionUpdate(viewState?.screenTransition)
    }

    fun <T : ScreenTransitionEnum> gotoScreen(screen: T, extras: Map<String, Any>) {
        viewState?.screenTransition = ScreenTransition(screen, createBundle(extras))
        router?.transitionUpdate(viewState?.screenTransition)
    }
}
