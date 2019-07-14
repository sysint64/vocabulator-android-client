package ru.kabylin.andrey.vocabulator.views

import android.os.Bundle
import ru.kabylin.andrey.vocabulator.ext.createBundle
import ru.kabylin.andrey.vocabulator.router.Router

interface ViewMediatorAware {
    val viewMediator: ViewMediator?
    val router: Router?

    /**
     * Перерисовка всего интерфейса, используется в основнои только при
     * полном восстановлении состояния экрана.
     */
    fun viewStateRefresh() {
        router?.transitionUpdate(viewMediator?.screenTransition)
    }

    fun unsubscribe() {}

    fun <T : ScreenTransitionEnum> gotoScreen(screen: T) {
        viewMediator?.screenTransition = ScreenTransition(screen)
        router?.transitionUpdate(viewMediator?.screenTransition)
    }

    fun <T : ScreenTransitionEnum> gotoScreen(screen: T, bundle: Bundle) {
        viewMediator?.screenTransition = ScreenTransition(screen, bundle)
        router?.transitionUpdate(viewMediator?.screenTransition)
    }

    fun <T : ScreenTransitionEnum> gotoScreen(screen: T, extras: Map<String, Any>) {
        viewMediator?.screenTransition = ScreenTransition(screen, createBundle(extras))
        router?.transitionUpdate(viewMediator?.screenTransition)
    }
}
