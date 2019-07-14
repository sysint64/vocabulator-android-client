package ru.kabylin.andrey.vocabulator.views

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import ru.kabylin.andrey.vocabulator.client.*
import ru.kabylin.andrey.vocabulator.ext.disposeBy

interface ClientCallbacks : ErrorsListener, RequestStateListener {
    fun onClientClearErrors()

    fun onClientError(error: ClientResponse<Throwable>)

    fun onSessionError(error: SessionError?)
}

open class ClientViewMediator(val client: Client, aware: ViewMediatorAware, lifecycle: Lifecycle)
    : ViewMediator(aware, lifecycle), ErrorsListener
{
    var error: Throwable? = null

    private val clientCallbacks
        get() = aware as ClientCallbacks

    override fun subscribe() {
        super.subscribe()

        if (!isEnabled)
            return

        client.criticalErrors
            .subscribe(::defaultCriticalErrorBehavior)
            .disposeBy(lifecycleDisposer)

        client.accessErrors
            .subscribe(::defaultAccessErrorBehavior)
            .disposeBy(lifecycleDisposer)

        client.subscriberOnErrors(this)
            .disposeBy(lifecycleDisposer)

        client.requestState.subscribe { (clientCallbacks as RequestStateListener).onRequestStateUpdated(it) }
            .disposeBy(lifecycleDisposer)

        client.subscriberOnErrors(clientCallbacks)
            .disposeBy(lifecycleDisposer)
    }

    open fun clearErrors() {
        error = null
        clientCallbacks.onClientClearErrors()
    }

    override fun onError(error: ClientResponse<Throwable>) {
        val errorPayload = error.payload
        this.error = errorPayload
        clientCallbacks.onClientError(error)

        if (errorPayload is SessionError) {
            clientCallbacks.onSessionError(errorPayload)
        }
    }

    private fun defaultCriticalErrorBehavior(error: ClientResponse<Throwable>) {
        val bundle = Bundle()
        bundle.putSerializable("throwable", error.payload)
        gotoScreen(ErrorScreenTransition.CRITICAL_ERROR, bundle)
    }

    private fun defaultAccessErrorBehavior(error: ClientResponse<AccessError>) {
        val bundle = Bundle()
        bundle.putSerializable("throwable", error.payload)

        when (error.payload.reason) {
            is AccessErrorReason.VERSION_ERROR -> {
                if (error.payload.reason.level == VersionErrorLevel.ERROR)
                    gotoScreen(ErrorScreenTransition.VERSION_ERROR, bundle)
            }
        }
    }
}
