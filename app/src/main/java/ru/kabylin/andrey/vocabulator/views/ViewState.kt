package ru.kabylin.andrey.vocabulator.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

interface ScreenTransitionEnum

enum class ErrorScreenTransition : ScreenTransitionEnum {
    CRITICAL_ERROR,
    VERSION_ERROR
}

enum class CommonScreens : ScreenTransitionEnum {
}

/**
 * Примитив для перехода на другой экран, данный примитив может сработать только один раз
 * после чего он больше не будет ничего делать.
 */
data class ScreenTransition<out T : ScreenTransitionEnum>(
    val transition: T,
    val bundle: Bundle? = null
) {
    var triggered: Boolean = false
        private set

    fun startActivity(context: Context, cls: Class<*>, requireLogin: Boolean = false) {
        if (triggered)
            return

        triggered = true
        straightStartActivity(context, cls)
    }

    fun guard(context: Context, requireLogin: Boolean = false, success: () -> Unit, fail: () -> Unit) {
        if (triggered)
            return

        triggered = true
        success()
    }

    fun startActivityForResult(context: Context, cls: Class<*>, resultCode: Int, requireLogin: Boolean = false) =
        startActivityForResult(context, Intent(context, cls), resultCode, requireLogin)

    fun startActivityForResult(context: Context, intent: Intent, resultCode: Int, requireLogin: Boolean = false) {
        if (triggered)
            return

        triggered = true

        bundle?.let { intent.putExtras(it) }
        (context as Activity).startActivityForResult(intent, resultCode)
    }

    private fun straightStartActivity(context: Context, cls: Class<*>) {
        val intent = Intent(context, cls)
        bundle?.let { intent.putExtras(it) }
        context.startActivity(intent)
    }
}

open class ViewState(protected val aware: ViewStateAware) {
    var screenTransition: ScreenTransition<*>? = null

    open fun subscribe() {}

    open fun unsubscribe() {}

    fun <T : ScreenTransitionEnum> gotoScreen(screen: T) {
        screenTransition = ScreenTransition(screen)
        aware.router?.transitionUpdate(screenTransition)
    }

    fun <T : ScreenTransitionEnum> gotoScreen(screen: T, bundle: Bundle) {
        screenTransition = ScreenTransition(screen, bundle)
        aware.router?.transitionUpdate(screenTransition)
    }
}
