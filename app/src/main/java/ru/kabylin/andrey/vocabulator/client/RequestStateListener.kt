package ru.kabylin.andrey.vocabulator.client

interface RequestStateListener {
    fun onRequestStateUpdated(requestState: ClientResponse<RequestState>)
}
