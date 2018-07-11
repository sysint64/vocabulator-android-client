package ru.kabylin.andrey.vocabulator.compositors

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.client.ClientResponse
import ru.kabylin.andrey.vocabulator.client.RequestState

class RequestStateCompositor(
    val client: Client,
    private val requestCode: Int = Client.REQUEST_DEFAULT
) : Compositor {

    override fun <T> compose(single: Single<T>): Single<T> {
        return single
            .doOnSubscribe { client.requestState.onNext(ClientResponse(RequestState.STARTED, requestCode, true)) }
            .doAfterTerminate { client.requestState.onNext(ClientResponse(RequestState.FINISHED, requestCode, true)) }
            .doOnDispose { client.requestState.onNext(ClientResponse(RequestState.FINISHED, requestCode, true)) }
    }

    override fun <T> compose(flowable: Flowable<T>): Flowable<T> {
        return flowable
            .doOnSubscribe { client.requestState.onNext(ClientResponse(RequestState.STARTED, requestCode, true)) }
            .doAfterTerminate { client.requestState.onNext(ClientResponse(RequestState.FINISHED, requestCode, true)) }
            .doOnCancel { client.requestState.onNext(ClientResponse(RequestState.FINISHED, requestCode, true)) }
    }

    override fun <T> compose(observable: Observable<T>): Observable<T> {
        return observable
            .doOnSubscribe { client.requestState.onNext(ClientResponse(RequestState.STARTED, requestCode, true)) }
            .doAfterTerminate { client.requestState.onNext(ClientResponse(RequestState.FINISHED, requestCode, true)) }
            .doOnDispose { client.requestState.onNext(ClientResponse(RequestState.FINISHED, requestCode, true)) }
    }

    override fun compose(completable: Completable): Completable {
        return completable
            .doOnSubscribe { client.requestState.onNext(ClientResponse(RequestState.STARTED, requestCode, true)) }
            .doAfterTerminate { client.requestState.onNext(ClientResponse(RequestState.FINISHED, requestCode, true)) }
            .doOnDispose { client.requestState.onNext(ClientResponse(RequestState.FINISHED, requestCode, true)) }
    }
}
