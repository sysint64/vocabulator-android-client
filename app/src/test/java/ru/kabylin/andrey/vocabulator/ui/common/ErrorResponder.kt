package ru.kabylin.andrey.vocabulator.ui.common

import ru.kabylin.andrey.vocabulator.views.ClientAppCompatActivity
import ru.kabylin.andrey.vocabulator.views.ClientViewState

interface ErrorResponder {
    fun onCriticalError(activity: ClientAppCompatActivity<ClientViewState>)
    fun onVersionError(activity: ClientAppCompatActivity<ClientViewState>)
    fun onVersionWarning(activity: ClientAppCompatActivity<ClientViewState>)
    fun onTooManyRequestError(activity: ClientAppCompatActivity<ClientViewState>)
    fun onTimeoutError(activity: ClientAppCompatActivity<ClientViewState>)
    fun onConnectionLostError(activity: ClientAppCompatActivity<ClientViewState>)
    fun onNotFound(activity: ClientAppCompatActivity<ClientViewState>)
    fun onBadResponseError(activity: ClientAppCompatActivity<ClientViewState>)
    fun onInternalServerError(activity: ClientAppCompatActivity<ClientViewState>)
}