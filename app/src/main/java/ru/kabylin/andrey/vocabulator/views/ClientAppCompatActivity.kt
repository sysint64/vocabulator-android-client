package ru.kabylin.andrey.vocabulator.views

import ru.kabylin.andrey.vocabulator.client.*

abstract class ClientAppCompatActivity<out T : ClientViewState> : ViewStateAppCompatActivity<T>(),
    ClientAware, ErrorViewAware, ClientCallbacks, RequestStateListener
{
    val errorsView by lazy { DefaultErrorsView(this) }

    override fun onRetryClick() {
        viewState.clearErrors()
        client.retryLastRequest()
    }

    override fun onClientError(error: ClientResponse<Throwable>) {
        errorsView.setError(error)
    }

    override fun viewStateRefresh() {
        super.viewStateRefresh()
    }

    override fun onErrorViewCloseClick() {
        finish()
    }

    override fun onErrorViewDismissErrorClick(error: ClientResponse<Throwable?>) {
        viewState.clearErrors()
    }

    override fun onSessionError(error: SessionError?) {
        finish()
    }

    override fun onClientClearErrors() {}

    override fun onRequestStateUpdated(requestState: ClientResponse<RequestState>) {}
}
