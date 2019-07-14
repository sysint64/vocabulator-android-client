package ru.kabylin.andrey.vocabulator.ui.common

import ru.kabylin.andrey.vocabulator.views.ClientAppCompatActivity
import ru.kabylin.andrey.vocabulator.views.ClientViewMediator

interface ErrorResponder {
    fun onCriticalError(activity: ClientAppCompatActivity<ClientViewMediator>)
    fun onVersionError(activity: ClientAppCompatActivity<ClientViewMediator>)
    fun onVersionWarning(activity: ClientAppCompatActivity<ClientViewMediator>)
    fun onTooManyRequestError(activity: ClientAppCompatActivity<ClientViewMediator>)
    fun onTimeoutError(activity: ClientAppCompatActivity<ClientViewMediator>)
    fun onConnectionLostError(activity: ClientAppCompatActivity<ClientViewMediator>)
    fun onNotFound(activity: ClientAppCompatActivity<ClientViewMediator>)
    fun onBadResponseError(activity: ClientAppCompatActivity<ClientViewMediator>)
    fun onInternalServerError(activity: ClientAppCompatActivity<ClientViewMediator>)
}