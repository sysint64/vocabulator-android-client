package ru.kabylin.andrey.vocabulator.views

import android.os.Bundle
import io.reactivex.disposables.CompositeDisposable
import ru.kabylin.andrey.vocabulator.client.*
import ru.kabylin.andrey.vocabulator.ext.disposeBy

interface ClientCallbacks : ErrorsListener, RequestStateListener {
    fun onClientClearErrors()

    fun onClientError(error: ClientResponse<Throwable>)

    fun onSessionError(error: SessionError?)
}

open class ClientViewState(val client: Client, aware: ViewStateAware)
    : ViewState(aware), ErrorsListener
{
    var compositeDisposable = CompositeDisposable()
    var error: Throwable? = null

    private val clientCallbacks
        get() = aware as ClientCallbacks

    override fun subscribe() {
        client.criticalErrors
            .subscribe(::defaultCriticalErrorBehavior)
            .disposeBy(compositeDisposable)

        client.accessErrors
            .subscribe(::defaultAccessErrorBehavior)
            .disposeBy(compositeDisposable)

        client.subscriberOnErrors(this)
            .disposeBy(compositeDisposable)

        client.requestState.subscribe { (clientCallbacks as RequestStateListener).onRequestStateUpdated(it) }
            .disposeBy(compositeDisposable)

        client.subscriberOnErrors(clientCallbacks)
            .disposeBy(compositeDisposable)
    }

    override fun unsubscribe() {
        compositeDisposable.clear()
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
