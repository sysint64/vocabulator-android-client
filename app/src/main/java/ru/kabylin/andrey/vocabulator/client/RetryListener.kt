package ru.kabylin.andrey.vocabulator.client

interface RetryListener {
    fun onRetryClick()
}

interface ErrorViewAware {
    fun displayError(error: ClientResponse<Throwable?>): Boolean {
        return false
    }

    fun onErrorViewCloseClick() {}

    fun onErrorViewDismissErrorClick(error: ClientResponse<Throwable?>) {}

    fun onErrorViewDismissWarningClick(error: ClientResponse<Throwable?>) {}
}

interface ClientAware : RetryListener {
    val client: Client

    override fun onRetryClick() {
        client.retryLastRequest()
    }
}
