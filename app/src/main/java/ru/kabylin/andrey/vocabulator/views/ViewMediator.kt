package ru.kabylin.andrey.vocabulator.views

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable

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

open class ViewMediator(protected val aware: ViewMediatorAware, private val lifecycle: Lifecycle) : LifecycleObserver {

    var lifecycleDisposer = CompositeDisposable()
    var screenTransition: ScreenTransition<*>? = null

    protected var isEnabled = false
        private set

    protected var isAttached = false
        private set

    init {
        lifecycle.addObserver(this)
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun subscribe() {
        if (isEnabled) {
            isAttached = true
        }
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun unsubscribe() {
        isAttached = false
        aware.unsubscribe()
        lifecycleDisposer.clear()
    }

    fun <T : ScreenTransitionEnum> gotoScreen(screen: T) {
        screenTransition = ScreenTransition(screen)
        aware.router?.transitionUpdate(screenTransition)
    }

    fun <T : ScreenTransitionEnum> gotoScreen(screen: T, bundle: Bundle) {
        screenTransition = ScreenTransition(screen, bundle)
        aware.router?.transitionUpdate(screenTransition)
    }

    fun enable() {
        isEnabled = true

        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            if (!isAttached)
                subscribe()
        }
    }
}
