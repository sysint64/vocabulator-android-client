package ru.kabylin.andrey.vocabulator.client

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import ru.kabylin.andrey.vocabulator.compositors.Compositor
import java.util.concurrent.LinkedBlockingQueue

class RequestBuilder {
    private var compositor: Compositor? = null
    private var requestCode: Int = Client.REQUEST_DEFAULT
    private var queue: LinkedBlockingQueue<ClientRequest>? = null

    fun withRequestCode(requestCode: Int): RequestBuilder {
        this.requestCode = requestCode
        return this
    }

    fun withCompositor(compositor: Compositor): RequestBuilder {
        this.compositor = compositor
        return this
    }

    fun <T> buildForSingle(query: Single<T>, onSuccess: (ClientResponse<T>) -> Unit): ClientRequest {
        return ClientRequest(requestCode) {
            val requestQuery = if (compositor != null) compositor!!.compose(query) else query
            this.disposable = requestQuery
                .flatMap { Single.just(ClientResponse(it, requestCode, true)) }
                .subscribe(
                    {
                        onSuccess(it)
                        queue?.remove(this)
                        this.markAsDone()
                    },
                    {
                        queue?.remove(this)
                        this.markAsDone()
                    }
                )
        }
    }

    fun buildForCompletable(query: Completable, onComplete: () -> Unit): ClientRequest {
        return ClientRequest(requestCode) {
            val requestQuery = if (compositor != null) compositor!!.compose(query) else query
            this.disposable = requestQuery
                .subscribe(
                    {
                        onComplete()
                        queue?.remove(this)
                        this.markAsDone()
                    },
                    {
                        queue?.remove(this)
                        this.markAsDone()
                    }
                )
        }
    }

    fun <T> buildForFlowable(query: Flowable<T>, onNext: (ClientResponse<T>) -> Unit, onComplete: () -> Unit): ClientRequest {
        return ClientRequest(requestCode) {
            val requestQuery = if (compositor != null) compositor!!.compose(query) else query
            this.disposable = requestQuery
                .flatMap { Flowable.just(ClientResponse(it, requestCode, true)) }
                .subscribe(
                    onNext,
                    {
                        queue?.remove(this)
                        this.markAsDone()
                    },
                    {
                        onComplete()
                        queue?.remove(this)
                        this.markAsDone()
                    }
                )
        }
    }

    fun <T> buildForObservable(query: Observable<T>, onNext: (ClientResponse<T>) -> Unit, onComplete: () -> Unit): ClientRequest {
        return ClientRequest(requestCode) {
            val requestQuery = if (compositor != null) compositor!!.compose(query) else query
            this.disposable = requestQuery
                .flatMap { Observable.just(ClientResponse(it, requestCode, true)) }
                .subscribe(
                    onNext,
                    {
                        queue?.remove(this)
                        this.markAsDone()
                    },
                    {
                        onComplete()
                        queue?.remove(this)
                        this.markAsDone()
                    }
                )
        }
    }

    fun withQueue(queue: LinkedBlockingQueue<ClientRequest>): RequestBuilder {
        this.queue = queue
        return this
    }
}
